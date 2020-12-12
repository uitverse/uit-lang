<?php


namespace heinthanth\Uit\Parser\OperationNode;

use heinthanth\Uit\Lexer\Token;

class ForOperationNode implements OperationNodeInterface
{
    public function __construct(public Token $loopControl, public OperationNodeInterface $start, public OperationNodeInterface $end, public OperationNodeInterface $step, public OperationNodeInterface $expression)
    {
    }
}
