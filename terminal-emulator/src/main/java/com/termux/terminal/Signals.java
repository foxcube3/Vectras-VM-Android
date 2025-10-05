package com.termux.terminal;

/** POSIX signal numbers used by the terminal subsystem. */
public enum Signals {
    HUP(1),
    INT(2),
    QUIT(3),
    ILL(4),
    ABRT(6),
    FPE(8),
    KILL(9),
    SEGV(11),
    PIPE(13),
    ALRM(14),
    TERM(15),
    CHLD(17),
    CONT(18),
    STOP(19),
    TSTP(20),
    TTIN(21),
    TTOU(22);

    public final int number;
    Signals(int n) { this.number = n; }
}
