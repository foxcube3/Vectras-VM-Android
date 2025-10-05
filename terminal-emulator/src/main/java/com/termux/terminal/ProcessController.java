package com.termux.terminal;

/** High-level helpers around JNI for terminal subprocess management. */
public final class ProcessController {

    private ProcessController() {}

    /** Send SIGINT (Ctrl-C equivalent). */
    public static boolean sendInterrupt(int pid) {
        return JNI.sendSignal(pid, Signals.INT.number) == 0;
    }

    /** Send SIGTERM (polite terminate). */
    public static boolean sendTerminate(int pid) {
        return JNI.sendSignal(pid, Signals.TERM.number) == 0;
    }

    /** Force kill. */
    public static boolean sendKill(int pid) {
        return JNI.sendSignal(pid, Signals.KILL.number) == 0;
    }

    /** Generic signal. */
    public static boolean send(int pid, Signals signal) {
        return JNI.sendSignal(pid, signal.number) == 0;
    }
}
