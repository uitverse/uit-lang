<?php


namespace heinthanth\Uit\Interpreter;


use heinthanth\Uit\Interpreter\DataTypes\BooleanType;
use heinthanth\Uit\Interpreter\DataTypes\DataTypeInterface;
use heinthanth\Uit\Interpreter\DataTypes\NullType;
use heinthanth\Uit\Interpreter\DataTypes\NumberType;
use heinthanth\Uit\Interpreter\Memory\Memory;
use heinthanth\Uit\Parser\OperationNode\BinOperationNode;
use heinthanth\Uit\Parser\OperationNode\ForOperationNode;
use heinthanth\Uit\Parser\OperationNode\IfOperationNode;
use heinthanth\Uit\Parser\OperationNode\MonoOperationNode;
use heinthanth\Uit\Parser\OperationNode\NumberNode;
use heinthanth\Uit\Parser\OperationNode\OperationNodeInterface;
use heinthanth\Uit\Parser\OperationNode\VariableAccessNode;
use heinthanth\Uit\Parser\OperationNode\VariableAssignNode;
use heinthanth\Uit\Parser\OperationNode\WhileOperationNode;
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
        } elseif ($node instanceof IfOperationNode) {
            return $this->visitIfOperationNode($node);
        } elseif ($node instanceof ForOperationNode) {
            return $this->visitForOperationNode($node);
        } elseif ($node instanceof WhileOperationNode) {
            return $this->visitWhileOperationNode($node);
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

        if ($node->operator->type === UIT_T_PLUS) {
            if ($left instanceof NumberType && $right instanceof NumberType) {
                return $left->add($right);
            } else {
                die("Error: cannot plus non-numbers" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_MINUS) {
            if ($left instanceof NumberType && $right instanceof NumberType) {
                return $left->minus($right);
            } else {
                die("Error: cannot subtract non-numbers" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_STAR) {
            if ($left instanceof NumberType && $right instanceof NumberType) {
                return $left->times($right);
            } else {
                die("Error: cannot multiply non-numbers" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_SLASH) {
            if ($left instanceof NumberType && $right instanceof NumberType) {
                return $left->divide($right);
            } else {
                die("Error: cannot divides non-numbers" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_PERCENT) {
            if ($left instanceof NumberType && $right instanceof NumberType) {
                return $left->modulo($right);
            } else {
                die("Error: cannot calculate reminder on non-numbers" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_CARET) {
            if ($left instanceof NumberType && $right instanceof NumberType) {
                return $left->power($right);
            } else {
                die("Error: cannot calculate exponent of non-numbers" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_EQ) {
            return $left->equal($right);
        } elseif ($node->operator->type === UIT_T_NE) {
            return $left->notEqual($right);
        } elseif ($node->operator->type === UIT_T_LT) {
            return $left->lessThan($right);
        } elseif ($node->operator->type === UIT_T_LE) {
            return $left->lessThanEqual($right);
        } elseif ($node->operator->type === UIT_T_GT) {
            return $left->greaterThan($right);
        } elseif ($node->operator->type === UIT_T_GE) {
            return $left->greaterThanEqual($right);
        } elseif ($node->operator->type === UIT_T_KEYWORD && $node->operator->value === "AND") {
            if ($left instanceof BooleanType && $right instanceof BooleanType) {
                return $left->and($right);
            } else {
                die("Error: cannot use AND with non-boolean" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_KEYWORD && $node->operator->value === "OR") {
            if ($left instanceof BooleanType && $right instanceof BooleanType) {
                return $left->or($right);
            } else {
                die("Error: cannot use OR with non-boolean" . PHP_EOL);
            }
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
        $data = $this->visit($node->rightNode);
        if ($node->operator->type === UIT_T_MINUS) {
            if ($data instanceof NumberType) {
                $data = $data->times(new NumberType('-1'));
            } else {
                die("Error: cannot negate non-number" . PHP_EOL);
            }
        } elseif ($node->operator->type === UIT_T_KEYWORD && $node->operator->value === 'NOT') {
            if ($data instanceof BooleanType) {
                $data = $data->not();
            } else {
                die("Error: cannot use NOT with non-boolean" . PHP_EOL);
            }
        }
        return $data;
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
     * @return NullType
     */
    private function visitVariableAssignNode(VariableAssignNode $node): NullType
    {
        $this->memory->symbols->set($node->variable->value, $this->visit($node->value));
        return new NullType();
    }

    /**
     * Eval if statement
     * @param IfOperationNode $node
     * @return DataTypeInterface
     */
    private function visitIfOperationNode(IfOperationNode $node): DataTypeInterface
    {
        foreach ($node->cases as $action) {
            [$condition, $expr] = $action;
            $conditionResult = $this->visit($condition);
            if ($conditionResult->value === 'true') {
                return $this->visit($expr);
            }
        }
        if ($node->elseExpr) {
            return $this->visit($node->elseExpr);
        }
        return new NullType();
    }

    /**
     * Evaluate For statement
     * @param ForOperationNode $node
     * @return DataTypeInterface
     */
    private function visitForOperationNode(ForOperationNode $node): DataTypeInterface
    {
        $startNode = intval($this->visit($node->start)->value);
        $endNode = intval($this->visit($node->end)->value);
        $stepNode = intval($this->visit($node->step)->value);
        $counter = $startNode;

        if ($stepNode >= 0) {
            while ($counter < $endNode) {
                $this->memory->symbols->set($node->loopControl->value, new NumberType(strval($counter)));
                $counter += $stepNode;
                $this->visit($node->expression);
            }
        } else {
            while ($counter > $endNode) {
                $this->memory->symbols->set($node->loopControl->value, new NumberType(strval($counter)));
                $counter -= $stepNode;
                $this->visit($node->expression);
            }
        }
        return new NullType();
    }

    /**
     * Evaluate While statement
     * @param WhileOperationNode $node
     * @return NullType
     */
    private function visitWhileOperationNode(WhileOperationNode $node): DataTypeInterface
    {
        while (true) {
            $condition = $this->visit($node->condition);
            if ($condition->value === 'false') break;
            $this->visit($node->expression);
        }
        return new NullType();
    }
}
