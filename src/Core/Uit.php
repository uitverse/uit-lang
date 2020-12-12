<?php

namespace heinthanth\Uit\Core;

use heinthanth\Uit\Interpreter\Interpreter;
use heinthanth\Uit\Interpreter\Memory\Memory;
use heinthanth\Uit\Interpreter\Memory\SymbolTable;
use heinthanth\Uit\Lexer\Lexer;
use heinthanth\Uit\Parser\Parser;
use JetBrains\PhpStorm\NoReturn;
use League\CLImate\CLImate;

class Uit
{
    /**
     * CLIMate instance to output error and messages to console.
     * @var CLImate
     */
    private CLImate $cliMate;

    /**
     * Uit constructor.
     * Initialize some instances
     */
    public function __construct()
    {
        $this->cliMate = new CLImate();
    }

    public function run(array $args): void
    {
        $argsCount = count($args);
        if ($argsCount === 1) {
            $option = $args[0];
            // if argument count is only One
            // if asking for help just show Help. else search and run file.
            if ($option == '-h' || $option == '--help' || $option == '-help') $this->showHelp();
            if ($option == '-v' || $option == '--version' || $option == '-version') $this->version();
            $this->runFile($option);
        } elseif ($argsCount == 2) {
            [$option, $code] = $args;
            if ($option == '-e' || $option == '--eval' || $option == '-eval') {
                $this->runFromString($code);
            }
        } elseif ($argsCount > 2) {
            // if malformed argument
            $this->showHelp(1);
        } else {
            // just REPL it
            $this->runREPL();
        }
    }

    /**
     * Run REPL if no argument exists
     * @return void
     */
    #[NoReturn]
    private function runREPL(): void
    {
        $_ENV['UIT_IN_REPL'] = true;
        $memory = new Memory(new SymbolTable());
        while (true) {
            $input = $this->cliMate->input('uit > ')->prompt();
            if ($input === 'exit') exit(0);
            if ($input != null && ord($input) != 4) $this->runCode($input, $memory);
        }
    }

    /**
     * Run from string such as -e argument, stdin
     * @param string $code
     */
    private function runFromString(string $code): void
    {
        if ($code === '-') {
            $code = file_get_contents("php://stdin");
        }
        $memory = new Memory(new SymbolTable());
        $lines = explode("\n", $code);
        foreach ($lines as $line) {
            if (trim($line) !== '') $this->runCode($line, $memory);
        }
    }

    /**
     * Run file if argument ( path to file ) exists.
     * @param string $path path to script file to run ( interpret )
     */
    private function runFile(string $path): void
    {
        $absolutePath = realpath($path);
        if (!file_exists($absolutePath)) {
            $this->cliMate->to('error')->red("Oops! No such file, '$path'");
            exit(1);
        }
        $this->runFromString(file_get_contents($absolutePath));
    }

    /**
     * Interpret code string
     * @param string $code
     * @param Memory $memory
     */
    private function runCode(string $code, Memory $memory): void
    {
        $tokens = (new Lexer($code))->tokenize();
        //echo implode(" ", $tokens) . PHP_EOL;
        $ast = (new Parser($tokens))->parse();
        //echo $ast . PHP_EOL;
        //print_r($ast);
        (new Interpreter($memory))->interpret($ast);
    }

    /**
     * Show Help / Info and exit the script
     * @param int $exitCode
     */
    #[NoReturn]
    private function showHelp(int $exitCode = 0): void
    {
        $this->cliMate->yellow("UIT Interpreter for Techie ( v0.1.0 )");
        $this->cliMate->yellow('(c) ' . date('Y') . " Hein Thant Maung Maung\n");
        if ($exitCode) {
            $this->cliMate->to('error')->red("Usage: php uit.php [script?]");
        } else {
            $this->cliMate->to('error')->out("Usage: php uit.php [script?]");
        }
        exit($exitCode);
    }

    /**
     * Show version info of interpreter
     */
    #[NoReturn]
    private function version(): void
    {
        $this->cliMate->yellow("UIT Interpreter for Techie ( v0.1.0 )");
        $this->cliMate->yellow('(c) ' . date('Y') . " Hein Thant Maung Maung");
        exit(0);
    }
}
