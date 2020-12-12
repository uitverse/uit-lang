<?php


namespace heinthanth\Uit\Parser\OperationNode;


use heinthanth\Uit\Lexer\Token;

class VariableDeclareNode implements OperationNodeInterface
{
    public function __construct(public Token $variable, public OperationNodeInterface $value) {
    }
}
