#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <pty.h>
#include <sys/ioctl.h>
#include <android/log.h>
#include <errno.h>
#include <stdlib.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/wait.h>

#define LOG_TAG "termux-native"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Helper to throw IOException with errno message
static void throwIOException(JNIEnv* env, const char* prefix) {
    char buf[256];
    strerror_r(errno, buf, sizeof(buf));
    char msg[512];
    snprintf(msg, sizeof(msg), "%s: %s (errno=%d)", prefix, buf, errno);
    jclass ioEx = (*env)->FindClass(env, "java/io/IOException");
    if (ioEx) (*env)->ThrowNew(env, ioEx, msg);
}

JNIEXPORT jint JNICALL
Java_com_termux_terminal_JNI_createSubprocess(JNIEnv* env, jclass clazz,
                                             jstring jcmd, jstring jcwd,
                                             jobjectArray jargs, jobjectArray jenv,
                                             jintArray jpidArray, jint rows, jint cols) {
    const char* cmd = (*env)->GetStringUTFChars(env, jcmd, 0);
    const char* cwd = jcwd ? (*env)->GetStringUTFChars(env, jcwd, 0) : NULL;

    int argc = 0;
    if (jargs) argc = (*env)->GetArrayLength(env, jargs) + 1; // +1 for cmd
    char** argv = calloc(argc + 1, sizeof(char*));
    if (!argv) {
        (*env)->ReleaseStringUTFChars(env, jcmd, cmd);
        if (cwd) (*env)->ReleaseStringUTFChars(env, jcwd, cwd);
        throwIOException(env, "calloc argv failed");
        return -1;
    }
    argv[0] = (char*)cmd;
    for (int i = 1; i < argc; i++) {
        jstring s = (jstring)(*env)->GetObjectArrayElement(env, jargs, i - 1);
        const char* utf = (*env)->GetStringUTFChars(env, s, 0);
        argv[i] = strdup(utf);
        (*env)->ReleaseStringUTFChars(env, s, utf);
    }
    argv[argc] = NULL;

    // env vars
    int envc = jenv ? (*env)->GetArrayLength(env, jenv) : 0;
    char** envp = NULL;
    if (envc > 0) {
        envp = calloc(envc + 1, sizeof(char*));
        if (!envp) {
            throwIOException(env, "calloc envp failed");
            goto cleanup;
        }
        for (int i = 0; i < envc; i++) {
            jstring e = (jstring)(*env)->GetObjectArrayElement(env, jenv, i);
            const char* utf = (*env)->GetStringUTFChars(env, e, 0);
            envp[i] = strdup(utf);
            (*env)->ReleaseStringUTFChars(env, e, utf);
        }
        envp[envc] = NULL;
    }

    int masterFd, slaveFd;
    pid_t pid;
    struct winsize sz = { .ws_row = (unsigned short)rows, .ws_col = (unsigned short)cols };
    if (openpty(&masterFd, &slaveFd, NULL, NULL, &sz) < 0) {
        throwIOException(env, "openpty failed");
        goto cleanup;
    }

    pid = fork();
    if (pid < 0) {
        throwIOException(env, "fork failed");
        close(masterFd); close(slaveFd);
        goto cleanup;
    }

    if (pid == 0) { // child
        if (setsid() == -1) _exit(1);
        if (ioctl(slaveFd, TIOCSCTTY, 0) == -1) _exit(1);
        dup2(slaveFd, STDIN_FILENO);
        dup2(slaveFd, STDOUT_FILENO);
        dup2(slaveFd, STDERR_FILENO);
        if (slaveFd > STDERR_FILENO) close(slaveFd);
        close(masterFd);
        if (cwd && chdir(cwd) != 0) _exit(1);
        if (envp) execve(cmd, argv, envp); else execv(cmd, argv);
        _exit(127);
    }

    // parent
    close(slaveFd);
    if (jpidArray) {
        jint pidVal = (jint)pid;
        (*env)->SetIntArrayRegion(env, jpidArray, 0, 1, &pidVal);
    }
    // Release Java strings now (after fork, but safe in parent) 
    (*env)->ReleaseStringUTFChars(env, jcmd, cmd);
    if (cwd) (*env)->ReleaseStringUTFChars(env, jcwd, cwd);
    for (int i = 1; i < argc; i++) free(argv[i]);
    free(argv);
    if (envp) {
        for (int i = 0; i < envc; i++) free(envp[i]);
        free(envp);
    }
    return masterFd;

cleanup:
    (*env)->ReleaseStringUTFChars(env, jcmd, cmd);
    if (cwd) (*env)->ReleaseStringUTFChars(env, jcwd, cwd);
    for (int i = 1; i < argc; i++) if (argv[i]) free(argv[i]);
    free(argv);
    if (envp) {
        for (int i = 0; i < envc; i++) if (envp[i]) free(envp[i]);
        free(envp);
    }
    return -1;
}

JNIEXPORT void JNICALL
Java_com_termux_terminal_JNI_setPtyWindowSize(JNIEnv* env, jclass clazz, jint fd, jint rows, jint cols) {
    (void)env; (void)clazz;
    struct winsize sz = { .ws_row = (unsigned short)rows, .ws_col = (unsigned short)cols };
    if (ioctl(fd, TIOCSWINSZ, &sz) < 0) {
        LOGE("setPtyWindowSize ioctl failed errno=%d", errno);
    }
}

JNIEXPORT jint JNICALL
Java_com_termux_terminal_JNI_waitFor(JNIEnv* env, jclass clazz, jint processId) {
    (void)env; (void)clazz;
    int status = 0;
    pid_t r = waitpid((pid_t)processId, &status, 0);
    if (r < 0) {
        LOGE("waitpid failed errno=%d", errno);
        return -1;
    }
    if (WIFEXITED(status)) return WEXITSTATUS(status);
    if (WIFSIGNALED(status)) return - (WTERMSIG(status));
    return -1;
}

JNIEXPORT void JNICALL
Java_com_termux_terminal_JNI_close(JNIEnv* env, jclass clazz, jint fileDescriptor) {
    (void)env; (void)clazz;
    if (close(fileDescriptor) < 0) {
        LOGE("close(%d) failed errno=%d", fileDescriptor, errno);
    }
}

// Send a POSIX signal to a subprocess pid. Returns 0 on success, -1 on failure (and logs errno).
JNIEXPORT jint JNICALL
Java_com_termux_terminal_JNI_sendSignal(JNIEnv* env, jclass clazz, jint processId, jint sig) {
    (void)env; (void)clazz;
    if (kill((pid_t)processId, sig) == -1) {
        LOGE("kill(%d, %d) failed errno=%d", processId, sig, errno);
        return -1;
    }
    return 0;
}
