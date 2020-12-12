<?php


namespace heinthanth\Uit\Parser\OperationNode;


class OutputOperationNode implements OperationNodeInterface
{
    /**
     * OutputOperationNode constructor.
     * @param OperationNodeInterface $expression
     */
    public function __construct(public OperationNodeInterface $expression)
    {
    }
}