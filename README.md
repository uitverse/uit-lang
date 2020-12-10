# UIT

UIT Interpreter for Techie - a simple interpreter written in PHP for PseudoCode like Programming Language.

## HOW TO RUN

Simple `git clone` and Run.

```shell
git clone https://github.com/heinthanth/uit
cd uit
composer install
php uit.php script # to run script
php uit.php # to run REPL
```

## EXAMPLES

Since it's in development, currently, accept only number calculations.

Make a file like the following. E.g. save as `hello.uit`

```text
4 + 5 * ( 4 + 3 / 2 ) - 4 
```

Then run as the following. Result should be `27.5`.

```shell
php uit.php hello.uit
```

Good Luck playing! You'll need PHP 8.0 and above.

## FAQ

### WHY I MADE THIS

When I was in First Year, our Uni taught us Pseudo-code for Programming Logic. I could understand well since I was
familiar with Programming but some of my friends couldn't. Since that time, I want to make an interpreter for PseudoCode
which might helps my friends to understand Pseudo-code and Programming Logic.\
Since first year, I tried to make an interpreter with different programming languages, but I failed. I hope this project
would help beginners ( students ) in learning Programming.

### WHY NAMED 'UIT'

No reason. I just want to make my Uni proud. And maybe I want to help them in specific ways.

### WHY NOT CHOOSING OTHER LANGUAGE

LOL. I'm a PHP fan and PHP is like an army swift knife for me. And it's the only language I can master ( use well ).\
Maybe you can port to other languages. Feel free to show me your work! I'm trying to port to C++ or C, too (or maybe
Java). According to my skills of writing in PHP, I need language which support interface in OOP.

## LICENSE

Just MIT license. See [License](LICENSE) for more information.
