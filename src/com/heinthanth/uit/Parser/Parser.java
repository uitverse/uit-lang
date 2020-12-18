package com.heinthanth.uit.Parser;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.TokenType;
import com.heinthanth.uit.Node.Statement;
import com.heinthanth.uit.Uit;
import com.heinthanth.uit.Node.Expression;

import static com.heinthanth.uit.Lexer.TokenType.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {
    }

    /**
     * List of tokens to parse
     */
    private final List<Token> tokens;

    /**
     * current parser index
     */
    private int current = 0;

    /**
     * Parser constructor
     *
     * @param tokens List of token to parse
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * parse token
     *
     * @return statements to interpret
     */
    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    /**
     * declaration statement
     */
    private Statement declaration() {
        try {
            if (match(NUM, STRING, BOOLEAN)) return varDeclaration(previous());
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    /**
     * variable declaration statement
     */
    private Statement varDeclaration(Token typeDef) {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        Expression initializer = null;
        if (match(ASSIGN)) {
            initializer = expression();
        }
        //consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.VariableDeclareStatement(typeDef, name, initializer);
    }

    /**
     * operation statement or expression statement
     *
     * @return Statement
     */
    private Statement statement() {
        if (match(OUTPUT)) return outputStatement();

        return expressionStatement();
    }

    /**
     * Parse expression to Output statement
     *
     * @return output statement
     */
    private Statement outputStatement() {
        Expression value = expression();
        //consume(SEMICOLON, "Expect ';' after value.");
        return new Statement.OutputStatement(value);
    }

    /**
     * Get expression statement
     *
     * @return Expression statement
     */
    private Statement expressionStatement() {
        Expression expr = expression();
        //consume(SEMICOLON, "Expect ';' after expression.");
        return new Statement.ExpressionStatement(expr);
    }

    /**
     * expression -> equality
     */
    private Expression expression() {
        return equality();
    }

    /**
     * Equality -> comparison (<>|==) comparison
     */
    private Expression equality() {
        Expression left = comparison();

        while (match(NOT_EQUAL, EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            left = new Expression.BinaryExpression(left, operator, right);
        }

        return left;
    }

    /**
     * comparison -> term (>, <, >=, <=) term
     */
    private Expression comparison() {
        Expression left = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * term -> factor (+,-) factor
     */
    private Expression term() {
        Expression left = factor();
        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expression right = factor();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * factor -> unary
     */
    private Expression factor() {
        Expression left = unary();
        while (match(STAR, SLASH)) {
            Token operator = previous();
            Expression right = unary();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * unary -> element
     */
    private Expression unary() {
        if (match(NOT, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.UnaryExpression(operator, right);
        }

        return primary();
    }

    private Expression primary() {
        if (match(FALSE)) return new Expression.LiteralExpression(false);
        if (match(TRUE)) return new Expression.LiteralExpression(true);

        if (match(NUMBER_LITERAL, STRING_LITERAL)) {
            return new Expression.LiteralExpression(previous().value);
        }
        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.GroupingExpression(expr);
        }
        if (match(IDENTIFIER)) {
            return new Expression.VariableExpression(previous());
        }
        throw error(getCurrentToken(), "Expect expression.");
    }

    /**
     * check if current token is in given list
     *
     * @param types expect types
     * @return true if matched
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }
    //< match

    /**
     * force check next token and warn error if not match
     *
     * @param type    Type of token
     * @param message Eror message
     * @return next character
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(getCurrentToken(), message);
    }

    /**
     * check token type
     *
     * @param type Type of token
     * @return true if current token match expected
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return getCurrentToken().type == type;
    }

    /**
     * Go to next token
     *
     * @return current token
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /**
     * Check if we are at the end of token
     *
     * @return true if we are at last token
     */
    private boolean isAtEnd() {
        return getCurrentToken().type == EOF;
    }

    /**
     * Get current token
     *
     * @return current token
     */
    private Token getCurrentToken() {
        return tokens.get(current);
    }

    /**
     * Get previous token
     *
     * @return previous token
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * report error
     *
     * @param token   error causing token
     * @param message error message
     * @return Parser Error instance
     */
    private ParseError error(Token token, String message) {
        Uit.error(token, message);
        return new ParseError();
    }

    /**
     * Error synchronization
     */
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            //if (previous().type == SEMICOLON) return;

            switch (getCurrentToken().type) {
                case FUNC:
                case FOR:
                case IF:
                case WHILE:
                case OUTPUT:
                case NUM:
                case STRING:
                case BOOLEAN:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}
