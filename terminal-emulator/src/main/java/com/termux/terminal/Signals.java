package com.termux.terminal;

/**
 * POSIX signals referenced by the terminal subsystem.
 * <p>
 * <table border="1">
 *   <tr><th>Signal</th><th>#</th><th>Typical Meaning / Use</th></tr>
 *   <tr><td>HUP</td><td>1</td><td>Terminal line hangup / reload config</td></tr>
 *   <tr><td>INT</td><td>2</td><td>User interrupt (Ctrl-C)</td></tr>
 *   <tr><td>QUIT</td><td>3</td><td>Quit and dump core (Ctrl-\)</td></tr>
 *   <tr><td>ILL</td><td>4</td><td>Illegal instruction</td></tr>
 *   <tr><td>ABRT</td><td>6</td><td>Abort</td></tr>
 *   <tr><td>FPE</td><td>8</td><td>Floating point exception</td></tr>
 *   <tr><td>KILL</td><td>9</td><td>Force kill (cannot be caught)</td></tr>
 *   <tr><td>SEGV</td><td>11</td><td>Segmentation fault</td></tr>
 *   <tr><td>PIPE</td><td>13</td><td>Broken pipe write</td></tr>
 *   <tr><td>ALRM</td><td>14</td><td>Timer expired</td></tr>
 *   <tr><td>TERM</td><td>15</td><td>Polite terminate</td></tr>
 *   <tr><td>CHLD</td><td>17</td><td>Child stopped/terminated</td></tr>
 *   <tr><td>CONT</td><td>18</td><td>Continue if stopped</td></tr>
 *   <tr><td>STOP</td><td>19</td><td>Job control stop (cannot be ignored)</td></tr>
 *   <tr><td>TSTP</td><td>20</td><td>Interactive stop (Ctrl-Z)</td></tr>
 *   <tr><td>TTIN</td><td>21</td><td>Background read from tty</td></tr>
 *   <tr><td>TTOU</td><td>22</td><td>Background write to tty</td></tr>
 * </table>
 */
public enum Signals {
    HUP(1, "hangup"),
    INT(2, "interrupt"),
    QUIT(3, "quit"),
    ILL(4, "illegal instruction"),
    ABRT(6, "abort"),
    FPE(8, "floating point exception"),
    KILL(9, "kill"),
    SEGV(11, "segmentation fault"),
    PIPE(13, "broken pipe"),
    ALRM(14, "alarm"),
    TERM(15, "terminate"),
    CHLD(17, "child status changed"),
    CONT(18, "continue"),
    STOP(19, "stop"),
    TSTP(20, "tty stop"),
    TTIN(21, "background tty read"),
    TTOU(22, "background tty write");

    public final int number;
    public final String description;
    Signals(int n, String description) {
        this.number = n;
        this.description = description;
    }
}
