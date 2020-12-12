<?php


namespace heinthanth\Uit\Parser\OperationNode;


use heinthanth\Uit\Lexer\Token;

class InputOperationNode implements OperationNodeInterface
{
    public function __construct(public Token $variable)
    {
    }
}