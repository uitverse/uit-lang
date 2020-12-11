<?php


namespace heinthanth\Uit\Parser;


use heinthanth\Uit\Lexer\Token;
use heinthanth\Uit\Parser\OperationNode\BinOperationNode;
use heinthanth\Uit\Parser\OperationNode\IfOperationNode;
use heinthanth\Uit\Parser\OperationNode\MonoOperationNode;
use heinthanth\Uit\Parser\OperationNode\NumberNode;
use heinthanth\Uit\Parser\OperationNode\OperationNodeInterface;
use heinthanth\Uit\Parser\OperationNode\VariableAccessNode;
use heinthanth\Uit\Parser\OperationNode\VariableAssignNode;

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
            return new VariableAssignNode($varName, $expression);
        }

        $leftNode = $this->comparison();
        while (($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === "AND")
            || ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === "OR")) {
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
        // if start with NOT
        if ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'NOT') {
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
        $leftNode = $this->atom();
        if ($this->currentToken->type === UIT_T_CARET) {
            $operator = $this->currentToken;
            $this->goNext();
            $rightNode = $this->factor();
            $leftNode = new BinOperationNode($leftNode, $operator, $rightNode);
        }
        return $leftNode;
    }

    private function if(): OperationNodeInterface
    {
        $cases = [];
        // TODO need to find datatype
        $elseExpr = null;

        $condition = $this->expression();
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'THEN') {
            die("Error: Syntax Error. Expecting 'THEN'" . PHP_EOL);
        }
        $this->goNext();
        $expression = $this->expression();
        $cases[] = [$condition, $expression];
        while ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'ELSEIF') {
            $this->goNext();
            $condition = $this->expression();
            if (!($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'THEN')) {
                die("Error: Syntax Error. Expecting 'THEN'" . PHP_EOL);
            }
            $this->goNext();
            $expression = $this->expression();
            $cases[] = [$condition, $expression];
        }
        if ($this->currentToken->type === UIT_T_KEYWORD && $this->currentToken->value === 'ELSE') {
            $this->goNext();
            $elseExpr = $this->expression();
        }
        if ($this->currentToken->type !== UIT_T_KEYWORD && $this->currentToken->value !== 'ENDIF') {
            die("Error: Syntax Error. Expecting 'ENDIF'" . PHP_EOL);
        } else {
            $this->goNext();
        }
        return new IfOperationNode($cases, $elseExpr);
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
        } elseif ($token->type === UIT_T_KEYWORD && $token->value === 'IF') {
            $this->goNext();
            return $this->if();
        }
        // something went wrong here. invalid syntax
        die("Error: Invalid Syntax. Expecting Number, Operator or '('" . PHP_EOL);
    }
}
