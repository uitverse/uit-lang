<?php


namespace heinthanth\Uit\Parser\OperationNode;


use heinthanth\Uit\Lexer\Token;

class FunctionDeclarationNode implements OperationNodeInterface
{
    public function __construct(public Token $functionName, public array $arguments, public OperationNodeInterface $expression)
    {
    }
}
