<?php


namespace heinthanth\Uit\Parser\OperationNode;


class WhileOperationNode implements OperationNodeInterface
{
    public function __construct(public OperationNodeInterface $condition, public OperationNodeInterface $expression)
    {
    }
}