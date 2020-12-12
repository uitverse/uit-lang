<?php

namespace heinthanth\Uit\Interpreter\Memory;

use JetBrains\PhpStorm\Pure;

class Memory
{
    /**
     * Initialize Symbol Table
     * Memory constructor.
     * @param SymbolTable $symbols
     * @param ?Memory $parent
     */
    #[Pure] public function __construct(public SymbolTable $symbols, public ?Memory $parent = null)
    {
    }
}
