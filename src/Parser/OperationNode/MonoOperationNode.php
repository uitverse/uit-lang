<?php


namespace heinthanth\Uit\Parser\OperationNode;


use heinthanth\Uit\Lexer\Token;

class MonoOperationNode implements OperationNodeInterface
{
    public function __construct(public Token $operator, public OperationNodeInterface $rightNode)
    {
    }

    public function __toString(): string
    {
        return "({$this->operator}, {$this->rightNode})";
    }
}
