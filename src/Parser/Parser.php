<?php


namespace heinthanth\Uit\Parser;


use heinthanth\Uit\Lexer\Token;
use heinthanth\Uit\Parser\OperationNode\BinOperationNode;
use heinthanth\Uit\Parser\OperationNode\ForOperationNode;
use heinthanth\Uit\Parser\OperationNode\FunctionCallNode;
use heinthanth\Uit\Parser\OperationNode\FunctionDeclarationNode;
use heinthanth\Uit\Parser\OperationNode\IfOperationNode;
use heinthanth\Uit\Parser\OperationNode\MonoOperationNode;
use heinthanth\Uit\Parser\OperationNode\NumberNode;
use heinthanth\Uit\Parser\OperationNode\OperationNodeInterface;
use heinthanth\Uit\Parser\OperationNode\VariableAccessNode;
use heinthanth\Uit\Parser\OperationNode\VariableAssignNode;
use heinthanth\Uit\Parser\OperationNode\VariableDeclareNode;
use heinthanth\Uit\Parser\OperationNode\WhileOperationNode;

class Parser
{
    /**
     * Current index of token array
     * @var int
     */
    private int $index = -1;

    /**
     * Current token
     * @var Token
     */
    private Token $currentToken;

    /**
     * Parser constructor.
     * @param array $tokens list of tokens from Lexer
     */
    public function __construct(private array $tokens)
    {
        $this->goNext();
    }

    /**
     * Parse Token to OperationNodes
     * @return OperationNodeInterface
     */
    public function parse(): OperationNodeInterface
    {
        $node = $this->expression();
        if ($this->currentToken->type !== UIT_T_EOF) {
            //print_r($this->currentToken);
            // not reached end of token. Must be error exist.
            die("Error: Invalid Syntax [ PARSER ]" . PHP_EOL);
        }
        return $node;
    }

    /**
     * Move to next token by incrementing index
     * and assign current Token
     */
    private function goNext(): void
    {
        $this->index++;
        if ($this->index < count($this->tokens)) {
            $this->currentToken = $this->tokens[$this->index];
        }
    }

    /**
     * Parse Expression
     * eg. ( term (+|-) term ), (let a = 5)
     *
     * @return OperationNodeInterface
     */
    private function expression(): OperationNodeInterface
    {
        if ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'Num') {
            $this->goNext();
            if ($this->currentToken->type !== UIT_T_IDENTIFIER) {
                die("Error: Invalid Syntax. Expecting Identifier" . PHP_EOL);
            }
            $varName = $this->currentToken;
            $this->goNext();
            if ($this->currentToken->type !== UIT_T_EQUAL) {
                die("Error: Invalid Syntax. Expecting '='" . PHP_EOL);
            }
            $this->goNext();
            $expression = $this->expression();
            return new VariableDeclareNode($varName, $expression);
        } elseif($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'set') {
            $this->goNext();
            if ($this->currentToken->type !== UIT_T_IDENTIFIER) {
                die("Error: Invalid Syntax. Expecting Identifier" . PHP_EOL);
            }
            $varName = $this->currentToken;
            $this->goNext();
            if ($this->currentToken->type !== UIT_T_EQUAL) {
                die("Error: Invalid Syntax. Expecting '='" . PHP_EOL);
            }
            $this->goNext();
            $expression = $this->expression();
            return new VariableAssignNode($varName, $expression);
        }

        $leftNode = $this->comparison();
        while (($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === "and")
            || ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === "or")) {
            $operator = $this->currentToken;
            $this->goNext();
            $rightNode = $this->comparison();
            $leftNode = new BinOperationNode($leftNode, $operator, $rightNode);
        }
        return $leftNode;
    }

    /**
     * Solve comparison expression
     * @return OperationNodeInterface
     */
    private function comparison(): OperationNodeInterface
    {
        // if start with not
        if ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'not') {
            $operator = $this->currentToken;
            $this->goNext();
            $comparisonNode = $this->comparison();
            return new MonoOperationNode($operator, $comparisonNode);
        }
        $leftNode = $this->arithmeticExpr();
        while (in_array($this->currentToken->type, [UIT_T_EQ, UIT_T_NE, UIT_T_LT, UIT_T_LE, UIT_T_GT, UIT_T_GE])) {
            $operator = $this->currentToken;
            $this->goNext();
            $rightNode = $this->arithmeticExpr();
            $leftNode = new BinOperationNode($leftNode, $operator, $rightNode);
        }
        return $leftNode;
    }

    /**
     * Solve arithmetic expression
     * @return OperationNodeInterface
     */
    private function arithmeticExpr(): OperationNodeInterface
    {
        $leftNode = $this->term();
        while ($this->currentToken->type === UIT_T_PLUS || $this->currentToken->type === UIT_T_MINUS) {
            $operator = $this->currentToken;
            $this->goNext();
            $rightNode = $this->term();
            $leftNode = new BinOperationNode($leftNode, $operator, $rightNode);
        }
        return $leftNode;
    }

    /**
     * Parse Term
     * eg. ( factor (*|/) factor )
     * @return OperationNodeInterface
     */
    private function term(): OperationNodeInterface
    {
        $leftNode = $this->factor();
        while ($this->currentToken->type === UIT_T_STAR || $this->currentToken->type === UIT_T_SLASH || $this->currentToken->type === UIT_T_PERCENT) {
            $operator = $this->currentToken;
            $this->goNext();
            $rightNode = $this->factor();
            $leftNode = new BinOperationNode($leftNode, $operator, $rightNode);
        }
        return $leftNode;
    }

    /**
     * Parser Factor
     * eg. (1,2) or -1
     *
     * @return OperationNodeInterface
     */
    private function factor(): OperationNodeInterface
    {
        $token = $this->currentToken;
        // if mono operation like -1, -(-2)
        if ($token->type === UIT_T_PLUS || $token->type === UIT_T_MINUS) {
            $this->goNext();
            $factor = $this->factor();
            return new MonoOperationNode($token, $factor);
        }
        // else maybe power expression.
        return $this->power();
    }

    /**
     * Solve power (exponential) expression
     * @return OperationNodeInterface
     */
    private function power(): OperationNodeInterface
    {
        $leftNode = $this->call();
        if ($this->currentToken->type === UIT_T_CARET) {
            $operator = $this->currentToken;
            $this->goNext();
            $rightNode = $this->factor();
            $leftNode = new BinOperationNode($leftNode, $operator, $rightNode);
        }
        return $leftNode;
    }

    /**
     * Call function
     * @return OperationNodeInterface
     */
    private function call(): OperationNodeInterface
    {
        $atom = $this->atom();
        if ($this->currentToken->type === UIT_T_LPARAN) {
            $this->goNext();
            $argumentNodes = [];
            if ($this->currentToken->type === UIT_T_RPARAN) {
                $this->goNext();
            } else {
                $argumentNodes[] = $this->expression();
                while ($this->currentToken->type === UIT_T_COMMA) {
                    $this->goNext();
                    $argumentNodes[] = $this->expression();
                }
                if ($this->currentToken->type !== UIT_T_RPARAN) die("Error: Syntax Error. Expecting ',' or ')' " . PHP_EOL);
                $this->goNext();
            }
            return new FunctionCallNode($atom, $argumentNodes);
        }
        return $atom;
    }

    /**
     * solve if statement
     * @return OperationNodeInterface
     */
    private function if(): OperationNodeInterface
    {
        $cases = [];
        // todo need to find datatype
        $elseExpr = null;

        $condition = $this->expression();
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'then') {
            die("Error: Syntax Error. Expecting 'then'" . PHP_EOL);
        }
        $this->goNext();
        $expression = $this->expression();
        $cases[] = [$condition, $expression];
        while ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'elseif') {
            $this->goNext();
            $condition = $this->expression();
            if (!($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'then')) {
                die("Error: Syntax Error. Expecting 'then'" . PHP_EOL);
            }
            $this->goNext();
            $expression = $this->expression();
            $cases[] = [$condition, $expression];
        }
        if ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'else') {
            $this->goNext();
            $elseExpr = $this->expression();
        }
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'endif') {
            die("Error: Syntax Error. Expecting 'endif'" . PHP_EOL);
        }
        $this->goNext();
        return new IfOperationNode($cases, $elseExpr);
    }

    /**
     * solve while statement
     * @return OperationNodeInterface
     */
    private function while(): OperationNodeInterface
    {
        $condition = $this->expression();
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'do') die("Error: Syntax Error. Expecting 'do'." . PHP_EOL);
        $this->goNext();
        $expression = $this->expression();
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'endwhile') die("Error: Syntax Error. Expecting 'endwhile'." . PHP_EOL);
        $this->goNext();
        return new WhileOperationNode($condition, $expression);
    }

    /**
     * Solve for statement
     * @return OperationNodeInterface
     */
    private function for(): OperationNodeInterface
    {
        if ($this->currentToken->type !== UIT_T_IDENTIFIER) die("Error: Syntax Error. Expecting Loop Control variable in For loop." . PHP_EOL);
        $loopControl = $this->currentToken;
        $this->goNext();
        if ($this->currentToken->type !== UIT_T_EQUAL) die("Error: Syntax Error. Expecting '=' " . PHP_EOL);
        $this->goNext();
        $startNode = $this->expression();
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'to') die("Error: Syntax Error. Expecting 'to'." . PHP_EOL);
        $this->goNext();
        $endNode = $this->expression();
        $stepNode = new NumberNode(new Token(UIT_T_NUMBER, '1'));
        if ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'step') {
            $this->goNext();
            $stepNode = $this->expression();
        }
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'do') die("Error: Syntax Error. Expecting 'do'." . PHP_EOL);
        $this->goNext();
        $expression = $this->expression();
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'endfor') die("Error: Syntax Error. Expecting 'endfor'." . PHP_EOL);
        $this->goNext();
        return new ForOperationNode($loopControl, $startNode, $endNode, $stepNode, $expression);
    }

    /**
     * @return OperationNodeInterface
     */
    private function declareFunction(): OperationNodeInterface
    {
        if ($this->currentToken->type !== UIT_T_IDENTIFIER) die("Error: Syntax Error. Expecting function name IDENTIFIER." . PHP_EOL);
        $functionName = $this->currentToken;
        $this->goNext();
        if ($this->currentToken->type !== UIT_T_LPARAN) die("Error: Syntax Error. Expecting '(' " . PHP_EOL);
        $this->goNext();
        $argumentTokens = [];
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'Num') {
            die("Error: Syntax Error. Expecting Data type for argument." . PHP_EOL);
        }
        $this->goNext();
        if ($this->currentToken->type !== UIT_T_IDENTIFIER) die("Error: Syntax Error. Expecting argument name IDENTIFIER." . PHP_EOL);
        $argumentTokens[] = $this->currentToken;
        $this->goNext();

        while ($this->currentToken->type === UIT_T_COMMA) {
            $this->goNext();
            if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'Num') {
                die("Error: Syntax Error. Expecting Data type for argument." . PHP_EOL);
            }
            $this->goNext();
            if ($this->currentToken->type !== UIT_T_IDENTIFIER) die("Error: Syntax Error. Expecting argument name IDENTIFIER." . PHP_EOL);
            $argumentTokens[] = $this->currentToken;
            $this->goNext();
        }

        if ($this->currentToken->type !== UIT_T_RPARAN) die("Error: Syntax Error. Expecting ',' or ')' " . PHP_EOL);
        $this->goNext();
        $expr = $this->expression();
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'stop') die("Error: Syntax Error. Expecting 'stop'." . PHP_EOL);
        $this->goNext();
        return new FunctionDeclarationNode($functionName, $argumentTokens, $expr);
    }

    /**
     * Solve number node, power node,etc
     * @return OperationNodeInterface
     */
    private function atom(): OperationNodeInterface
    {
        $token = $this->currentToken;
        if ($token->type === UIT_T_NUMBER) {
            $this->goNext();
            return new NumberNode($token);
        } elseif ($token->type === UIT_T_IDENTIFIER) {
            $this->goNext();
            return new VariableAccessNode($token);
        } elseif ($token->type === UIT_T_LPARAN) {
            $this->goNext();
            $expr = $this->expression();
            if ($this->currentToken->type === UIT_T_RPARAN) {
                $this->goNext();
                return $expr;
            }
            // something went wrong here. invalid syntax missing ')'
            die("Error: Invalid Syntax. Expecting ')'" . PHP_EOL);
        } elseif ($token->type === UIT_T_KEYWORD && $token->value === 'if') {
            $this->goNext();
            return $this->if();
        } elseif ($token->type === UIT_T_KEYWORD && $token->value === 'for') {
            $this->goNext();
            return $this->for();
        } elseif ($token->type === UIT_T_KEYWORD && $token->value === 'while') {
            $this->goNext();
            return $this->while();
        } elseif ($token->type === UIT_T_KEYWORD && $token->value === 'func') {
            $this->goNext();
            return $this->declareFunction();
        }
        // something went wrong here. invalid syntax
        die("Error: Invalid Syntax. Expecting Number, Operator or '('" . PHP_EOL);
    }
}
