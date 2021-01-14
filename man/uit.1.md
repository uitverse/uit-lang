% uit(1) uit 1.0.0-alpha
% Hein Thant Maung Maung
% Jan 2021

# NAME

uit - uit-lang interpreter

# SYNOPSIS

**uit** [*OPTIONS*] [*FILE*] [*args ...*]

# DESCRIPTION

**uit** is interpreter for uit-lang, a pseudo-code like programming language, designed for beginners in programming.

Interpret uit lang script

With no FILE or FILE is -, read standard input.

# OPTIONS

**\-h**, **\-\-help**
: Show help message like usage information.

**\-v**, **\-\-version**
: Show interpreter version.

**\-i**, **\-\-interactive**
: Run interpreter in REPL mode.

# EXAMPLES

**uit**
: Interpret *standard input* (stdin).

**uit hello-world.uit**
: Interpret code from *hello-world.uit*.

**uit -i**
: Run REPL mode. Get input and interpret it.

# EXIT VALUES

**0**
: Success

**1**
: Invalid option

**2**
: File read error ( ENOENT, EACCES )

**65**
: Lexer / Parser error

**70**
: Runtime error

# COPYRIGHT

Copyright (c) 2021 Hein Thant Maung Maung. License MIT <https://opensource.org/licenses/MIT>