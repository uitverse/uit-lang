# The uit-lang Interpreter

[![MIT license](https://img.shields.io/badge/License-MIT-green.svg)](https://lbesson.mit-license.org/)
![Lines of code](https://img.shields.io/tokei/lines/github/uitverse/uit-lang?label=Lines%20of%20Code&style=flat)
[![Code Quality Score](https://www.code-inspector.com/project/17782/score/svg)](https://www.code-inspector.com/project/17782/score/svg)

`uit` is interpreter for `uit-lang`, a pseudo-code like programming language, designed for beginners in programming. Not too slow with JVM, and with must-have programming language features.

## Documentation

Since it's under development, documentations hasn't been written yet. It's basically heavy modified version of Lox Lang ( `CraftingInterpreters` ).

## New Features

- [x] Static Data Types
- [x] `Modulo`, `Exponent`, `Prefix`, `Postfix` operators
- [x] Loop Control ( `Break`, `Continue` )
- [x] OOP with Access Modifier (`Public`, `Private`, `Protected`)
- [ ] `Super` 

## Installation

You can install `uit-lang` interpreter in various methods.
### Prebuilt Package

Well, you can download prebuilt `jar` [here](build/). Then, run it through JRE.

``` shell
git clone -b main https://github.com/uitverse/uit-lang && cd uit-lang
```

clone first!

### Building with JDK

```shell
mvn clean package
```

### Building with Docker

```shell
docker build -t uit .
docker run -ti --rm uit
```

## License

The interpreter is licensed under MIT. See [LICENSE](LICENSE) for more details.

## Credits

Thanks to:

-   [Bob](https://twitter.com/munificentbob) for his awesome [Crafting Interpreters](https://craftinginterpreters.com) - a handbook for making programming languages.
-   [David Callanan](https://github.com/davidcallanan) for his awesome [tutorial on creating BASIC interpreter](https://youtube.com/playlist?list=PLZQftyCk7_SdoVexSmwy_tBgs7P0b97yD) with Python.
-   [JLine3](https://github.com/jline/jline3) for awesome input handling. Without this, uit REPL won't allow editing, etc.
-   [Jansi](https://github.com/fusesource/jansi) for awesome output formatter. Without this, I have to write from Scratch to get better colored response.