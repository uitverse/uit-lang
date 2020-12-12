<?php


namespace heinthanth\Uit\Parser\OperationNode;


class IfOperationNode implements OperationNodeInterface
{
    /**
     * IfOperationNode constructor.
     * @param array $cases
     * @param mixed $elseExpr
     */
    public function __construct(public array $cases, public mixed $elseExpr)
    {
    }
}
