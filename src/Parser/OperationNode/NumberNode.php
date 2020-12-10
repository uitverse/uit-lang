<?php

namespace heinthanth\Uit\Parser\OperationNode;

class NumberNode implements OperationNodeInterface
{

    public function __construct(public \heinthanth\Uit\Lexer\Token $token)
    {
    }

    public function __toString(): string
    {
        return "{$this->token}";
    }
}
