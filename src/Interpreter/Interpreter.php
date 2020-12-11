<?php


namespace heinthanth\Uit\Interpreter;


use heinthanth\Uit\Interpreter\DataTypes\DataTypeInterface;
use heinthanth\Uit\Interpreter\DataTypes\NumberType;
use heinthanth\Uit\Interpreter\Memory\Memory;
use heinthanth\Uit\Parser\OperationNode\BinOperationNode;
use heinthanth\Uit\Parser\OperationNode\MonoOperationNode;
use heinthanth\Uit\Parser\OperationNode\NumberNode;
use heinthanth\Uit\Parser\OperationNode\OperationNodeInterface;
use heinthanth\Uit\Parser\OperationNode\VariableAccessNode;
use heinthanth\Uit\Parser\OperationNode\VariableAssignNode;
use JetBrains\PhpStorm\NoReturn;
use JetBrains\PhpStorm\Pure;

class Interpreter
{
    /**
     * Interpreter constructor.
     * @param Memory $memory
     */
    public function __construct(private Memory $memory)
    {
    }

    /**
     * Interpret parser Operation Node
     * @param OperationNodeInterface $node
     * @return DataTypeInterface
     */
    public function interpret(OperationNodeInterface $node): DataTypeInterface
    {
        return $this->visit($node);
    }

    /**
     * Recursively visit parsed nodes and solve to value
     * @param OperationNodeInterface $node
     * @return DataTypeInterface
     */
    private function visit(OperationNodeInterface $node): DataTypeInterface
    {
        if ($node instanceof NumberNode) {
            return $this->visitNumberNode($node);
        } elseif ($node instanceof BinOperationNode) {
            return $this->visitBinOperationNode($node);
        } elseif ($node instanceof MonoOperationNode) {
            return $this->visitMonoOperationNode($node);
        } elseif ($node instanceof VariableAssignNode) {
            return $this->visitVariableAssignNode($node);
        } elseif ($node instanceof VariableAccessNode) {
            return $this->visitVariableAccessNode($node);
        }
        die("Error: Invalid Operation" . PHP_EOL);
    }

    /**
     * Convert NumberNode to NumberType
     * @param NumberNode $node
     * @return NumberType
     */
    #[Pure] private function visitNumberNode(NumberNode $node): NumberType
    {
        return new NumberType($node->token->value);
    }

    /**
     * Solve Binary Operation Node
     * @param BinOperationNode $node
     * @return DataTypeInterface
     */
    #[NoReturn]
    private function visitBinOperationNode(BinOperationNode $node): DataTypeInterface
    {
        $left = $this->visit($node->leftNode);
        $right = $this->visit($node->rightNode);

        if ($node->operator->type === T_PLUS) {
            return $left->add($right);
        } elseif ($node->operator->type === T_MINUS) {
            return $left->minus($right);
        } elseif ($node->operator->type === T_STAR) {
            return $left->times($right);
        } elseif ($node->operator->type === T_SLASH) {
            return $left->divide($right);
        } elseif ($node->operator->type === T_PERCENT) {
            return $left->modulo($right);
        } elseif ($node->operator->type === T_CARET) {
            return $left->power($right);
        }
        die("Error: Something went wrong" . PHP_EOL);
    }

    /**
     * Solve MonoOperationNode
     * @param MonoOperationNode $node
     * @return DataTypeInterface
     */
    #[NoReturn]
    private function visitMonoOperationNode(MonoOperationNode $node): DataTypeInterface
    {
        $number = $this->visit($node->rightNode);
        if ($node->operator->type === T_MINUS) {
            $number = $number->times(new NumberType('-1'));
        }
        return $number;
    }

    /**
     * Get value from variable name
     * @param VariableAccessNode $node
     * @return DataTypeInterface
     */
    private function visitVariableAccessNode(VariableAccessNode $node): DataTypeInterface
    {
        return $this->memory->symbols->get($node->variable->value);
    }

    /**
     * Assign value to variable name
     * @param VariableAssignNode $node
     * @return DataTypeInterface
     */
    private function visitVariableAssignNode(VariableAssignNode $node): DataTypeInterface
    {
        $value = $this->visit($node->value);
        $this->memory->symbols->set($node->variable->value, $value);
        return $value;
    }
}
