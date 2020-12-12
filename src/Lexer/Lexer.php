<?php

namespace heinthanth\Uit\Lexer;

require_once __DIR__ . '/TokenDefinition.php';

define('DIGIUIT_T_STRING', '0123456789');
define('LETTER_STRING', 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ');
define('LETTER_W_DIGIUIT_T_STRING', 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789');

class Lexer
{
    /**
     * Position of Lexer cursor
     * @var int
     */
    private int $pos = -1;

    /**
     * Current character at Lexer cursor position
     * @var string
     */
    private string $currentCharacter = "\0";

    /**
     * Lexer constructor.
     * @param string $code Source Code to tokenize
     */
    public function __construct(private string $code)
    {
        $this->goNext();
    }

    /**
     * Move to next character by incrementing Cursor Position.
     */
    private function goNext(): void
    {
        $this->pos++;
        $this->currentCharacter = $this->pos < strlen($this->code) ? $this->code[$this->pos] : "\0";
    }

    /**
     * Tokenize source code
     * @return array
     */
    public function tokenize(): array
    {
        $tokens = [];
        while ($this->currentCharacter !== "\0") {
            if ($this->currentCharacter === ' ' || $this->currentCharacter === "\t" || $this->currentCharacter === "\n") {
                $this->goNext();
            } elseif (str_contains(DIGIUIT_T_STRING, $this->currentCharacter)) {
                $tokens[] = $this->makeNumber();
            } elseif (str_contains(LETTER_STRING, $this->currentCharacter)) {
                $tokens[] = $this->makeIdentifier();
            } elseif ($this->currentCharacter === '+') {
                $tokens[] = new Token(UIT_T_PLUS);
                $this->goNext();
            } elseif ($this->currentCharacter === '-') {
                $tokens[] = new Token(UIT_T_MINUS);
                $this->goNext();
            } elseif ($this->currentCharacter === '*') {
                $tokens[] = new Token(UIT_T_STAR);
                $this->goNext();
            } elseif ($this->currentCharacter === '/') {
                $tokens[] = new Token(UIT_T_SLASH);
                $this->goNext();
            } elseif ($this->currentCharacter === '%') {
                $tokens[] = new Token(UIT_T_PERCENT);
                $this->goNext();
            } elseif ($this->currentCharacter === '^') {
                $tokens[] = new Token(UIT_T_CARET);
                $this->goNext();
            } elseif ($this->currentCharacter === '(') {
                $tokens[] = new Token(UIT_T_LPARAN);
                $this->goNext();
            } elseif ($this->currentCharacter === ')') {
                $tokens[] = new Token(UIT_T_RPARAN);
                $this->goNext();
            } elseif ($this->currentCharacter === ',') {
                $tokens[] = new Token(UIT_T_COMMA);
                $this->goNext();
            } elseif ($this->currentCharacter === '=') {
                $this->goNext();
                if ($this->currentCharacter === '=') {
                    // == logical operator
                    $tokens[] = new Token(UIT_T_EQ);
                    $this->goNext();
                } else {
                    // just = operator
                    $tokens[] = new Token(UIT_T_EQUAL);
                }
            } elseif ($this->currentCharacter === '<') {
                $this->goNext();
                if ($this->currentCharacter === '=') {
                    // <= logical operator
                    $tokens[] = new Token(UIT_T_LE);
                    $this->goNext();
                } elseif ($this->currentCharacter === '>') {
                    // <> not equal operator
                    $tokens[] = new Token(UIT_T_NE);
                    $this->goNext();
                } else {
                    // just < operator
                    $tokens[] = new Token(UIT_T_LT);
                }
            } elseif ($this->currentCharacter === '>') {
                $this->goNext();
                if ($this->currentCharacter === '=') {
                    // >= logical operator
                    $tokens[] = new Token(UIT_T_GE);
                    $this->goNext();
                } else {
                    // just > operator
                    $tokens[] = new Token(UIT_T_GT);
                }
            } else {
                // invalid token
                die("Error: Invalid Syntax [ Lexer ]" . PHP_EOL);
            }
        }
        $tokens[] = new Token(UIT_T_EOF);
        return $tokens;
    }

    /**
     * Analyze Number strings
     * @return Token
     */
    private function makeNumber(): Token
    {
        $numberString = '';
        $dotCount = 0;
        while ($this->currentCharacter !== null && str_contains(DIGIUIT_T_STRING . '.', $this->currentCharacter)) {
            if ($this->currentCharacter === '.') {
                if ($dotCount === 1) break;
                $dotCount++;
                $numberString .= '.';
            } else {
                $numberString .= $this->currentCharacter;
            }
            $this->goNext();
        }
        return new Token(UIT_T_NUMBER, $numberString);
    }

    private function makeIdentifier(): Token
    {
        $identifierString = '';
        while ($this->currentCharacter !== null && str_contains(LETTER_W_DIGIUIT_T_STRING . '_', $this->currentCharacter)) {
            $identifierString .= $this->currentCharacter;
            $this->goNext();
        }
        return new Token(in_array($identifierString, UIT_KEYWORDS) ? UIT_T_KEYWORD : UIT_T_IDENTIFIER, $identifierString);
    }
}
