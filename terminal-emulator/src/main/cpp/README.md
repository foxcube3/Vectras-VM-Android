Native layer for libtermux.so
================================

This directory contains the C implementation backing JNI methods declared in `com.termux.terminal.JNI`.

Provided JNI functions:
- createSubprocess: fork + exec in a PTY created with openpty().
- setPtyWindowSize: issues TIOCSWINSZ to resize the pseudo terminal.
- waitFor: waitpid wrapper returning exit status or negative signal code.
- close: closes the master file descriptor.

Build: Integrated via CMake (see `CMakeLists.txt`). The Gradle module `terminal-emulator` now builds the shared library instead of using a prebuilt binary. Adjust or extend as needed (e.g. adding architecture-specific sources or feature flags).

Security considerations:
- Minimal argument validation; Java side should ensure non-null or handle -1 returns.
- No privilege escalation; standard fork/exec model.

Future Enhancements:
- Optional login shell environment setup.
- Support for sending signals (e.g., SIGINT) through additional JNI methods.
- Improved error propagation (throw specialized exceptions).
