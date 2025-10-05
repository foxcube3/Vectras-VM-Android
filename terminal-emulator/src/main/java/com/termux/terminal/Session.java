package com.termux.terminal;

import androidx.annotation.Nullable;

/** Represents a PTY-backed subprocess session (fd + pid) with convenience operations. */
public final class Session implements AutoCloseable {
    private final int fd;
    private final int pid;
    private volatile boolean closed;

    private Session(int fd, int pid) {
        this.fd = fd;
        this.pid = pid;
    }

    /** Launch a new session; returns null if creation failed. */
    @Nullable
    public static Session start(String cmd, String cwd, String[] args, String[] env, int rows, int cols) {
        int[] pidHolder = new int[1];
        int master = JNI.createSubprocess(cmd, cwd, args, env, pidHolder, rows, cols);
        if (master < 0) return null;
        return new Session(master, pidHolder[0]);
    }

    public int getFd() { return fd; }
    public int getPid() { return pid; }

    /** Resize the underlying PTY. */
    public void resize(int rows, int cols) {
        if (!closed) JNI.setPtyWindowSize(fd, rows, cols);
    }

    /** Send a signal to the process. */
    public boolean signal(Signals sig) { return !closed && JNI.sendSignal(pid, sig.number) == 0; }

    /** Wait for the process to exit; returns exit status or negative signal code. */
    public int waitForExit() { return JNI.waitFor(pid); }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            JNI.close(fd);
        }
    }
}
