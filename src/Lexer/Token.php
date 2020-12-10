<?php

namespace heinthanth\Uit\Lexer;

class Token
{
    /**
     * Token constructor.
     * @param string $type Type of token
     * @param string $value Value of token
     */
    public function __construct(public string $type, public string $value = "\0")
    {
    }

    /**
     * Get string representation of a token (optional method)
     * @return string
     */
    public function __toString(): string
    {
        return ($this->value !== "\0") ? "[{$this->type}:{$this->value}]" : "[{$this->type}]";
    }
}