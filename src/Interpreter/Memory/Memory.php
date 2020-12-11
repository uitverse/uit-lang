<?php

namespace heinthanth\Uit\Interpreter\Memory;

use JetBrains\PhpStorm\Pure;

class Memory
{
    /**
     * List of variable
     * @var SymbolTable
     */
    public SymbolTable $symbols;

    /**
     * parent of variable. For e.g. Local variable in functions
     * @var string
     */
    public string $parent = "\0";

    /**
     * Initialize Symbol Table
     * Memory constructor.
     */
    #[Pure] public function __construct()
    {
        $this->symbols = new SymbolTable();
    }
}
