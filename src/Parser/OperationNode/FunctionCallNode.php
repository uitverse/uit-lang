<?php


namespace heinthanth\Uit\Parser\OperationNode;


class FunctionCallNode implements OperationNodeInterface
{
    /**
     * FunctionCallNode constructor.
     * @param OperationNodeInterface $functionNode
     * @param array $argumentNode
     */
    public function __construct(public OperationNodeInterface $functionNode, public array $argumentNode)
    {
    }
}
