# The uit-lang Interpreter

[![MIT license](https://img.shields.io/badge/License-MIT-green.svg)](https://lbesson.mit-license.org/)
![Lines of code](https://img.shields.io/tokei/lines/github/heinthanth/uit-lang?label=Lines%20of%20Code&style=flat)
[![Code Quality Score](https://www.code-inspector.com/project/17719/score/svg)](https://www.code-inspector.com/project/17719/score/svg)

`uit` is interpreter for `uit-lang`, a pseudo-code like programming language, designed for beginners in programming. Not too slow with JVM, and with must-have programming language features.

## Documentation

Since it's under development, documentations hasn't been written yet.

## Installation

You can install `uit-lang` interpreter in various methods.

clone first!

``` shell
git clone -b main https://github.com/heinthanth/uit-lang && cd uit-lang
```

### Prebuilt Package

Well, you can download prebuilt `jar` file [here](build/uit.jar). Then, run it through JRE.

### Building with JDK

```shell
javac -g:none -Werror -d build -cp src src/com/heinthanth/uit/Main.java
java -cp build com.heinthanth.uit.Main
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
