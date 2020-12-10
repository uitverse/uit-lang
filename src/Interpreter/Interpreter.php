<?php


namespace heinthanth\Uit\Interpreter;


use heinthanth\Uit\Interpreter\Types\DataTypeInterface;
use heinthanth\Uit\Interpreter\Types\NumberType;
use heinthanth\Uit\Parser\OperationNode\BinOperationNode;
use heinthanth\Uit\Parser\OperationNode\MonoOperationNode;
use heinthanth\Uit\Parser\OperationNode\NumberNode;
use heinthanth\Uit\Parser\OperationNode\OperationNodeInterface;
use JetBrains\PhpStorm\NoReturn;
use JetBrains\PhpStorm\Pure;

class Interpreter
{
    public function interpret(OperationNodeInterface $node): DataTypeInterface
    {
        return $this->visit($node);
    }

    private function visit(OperationNodeInterface $node): DataTypeInterface
    {
        if ($node instanceof NumberNode) {
            return $this->visitNumberNode($node);
        } elseif ($node instanceof BinOperationNode) {
            return $this->visitBinOperationNode($node);
        } elseif ($node instanceof MonoOperationNode) {
            return $this->visitMonoOperationNode($node);
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
        }  elseif ($node->operator->type === T_PERCENT) {
            return $left->modulo($right);
        }
        die("Error: Something went wrong" . PHP_EOL);
    }

    #[NoReturn]
    private function visitMonoOperationNode(MonoOperationNode $node): DataTypeInterface
    {
        $number = $this->visit($node->rightNode);
        if ($node->operator->type === T_MINUS) {
            $number = $number->times(new NumberType('-1'));
        }
        return $number;
    }
}
