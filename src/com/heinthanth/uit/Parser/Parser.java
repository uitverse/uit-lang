package com.heinthanth.uit.Parser;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.TokenType;
import com.heinthanth.uit.Node.Statement;
import com.heinthanth.uit.Uit;
import com.heinthanth.uit.Node.Expression;

import static com.heinthanth.uit.Lexer.TokenType.*;

import java.util.*;

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

    private int loopDepth = 0;

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
            if (match(NUM, STRING, BOOLEAN, VOID)) return varDeclaration(previous());
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
        Token name = consume(IDENTIFIER, "Expect identifier.");
        Expression initializer = null;
        // may be function
        if (match(LEFT_PAREN)) {
            List<List<Token>> parameters = new ArrayList<>();
            if (!check(RIGHT_PAREN)) {
                do {
                    if (parameters.size() >= 255) {
                        error(getCurrentToken(), "Can't have more than 255 parameters.");
                    }
                    List<Token> param = new ArrayList<>();
                    if (!(check(NUM) || check(STRING) || check(BOOLEAN) || check(VOID))) {
                        throw error(getCurrentToken(), "Expect return type.");
                    } else {
                        param.add(advance());
                    }
                    param.add(consume(IDENTIFIER, "Expect parameter name."));
                    parameters.add(param);
                } while (match(COMMA));
            }
            consume(RIGHT_PAREN, "Expect ')' after parameters.");
            // consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
            List<Statement> body = functionBlock();
            return new Statement.FunctionStatement(typeDef, name, parameters, body);
        }
        if (match(ASSIGN)) {
            initializer = expression();
        }
        //consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.VariableDeclareStatement(typeDef, name, initializer);
    }

    private Expression varAssignment() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        consume(ASSIGN, "Expect '='.");
        Expression initializer = expression();
        //consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Expression.VariableAssignExpression(name, initializer);
    }

    /**
     * operation statement or expression statement
     *
     * @return Statement
     */
    private Statement statement() {
        if (match(SET)) return new Statement.ExpressionStatement(varAssignment());
        if (match(IF)) return ifStatement();
        if (match(OUTPUT)) return outputStatement();
        if (match(RETURN)) return returnStatement();
        if (match(BREAK)) return breakStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();
        if (match(BLOCK)) return new Statement.BlockStatement(block());

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
     * Parse return Statement
     */
    private Statement returnStatement() {
        Token keyword = previous();
        Expression value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }
        //consume(SEMICOLON, "Expect ';' after return value.");
        return new Statement.ReturnStatement(keyword, value);
    }

    private Statement breakStatement() {
        //consume(SEMICOLON, "Expect ';' after 'break'.");
        if (loopDepth == 0) {
            error(previous(), "'break' must be use inside loop.");
        }
        //consume(SEMICOLON, "Expect ';' after 'break'.");
        return new Statement.BreakStatement();
    }

    /**
     * Get If statement
     *
     * @return if statement
     */
    private Statement ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        Map<Expression, Statement> branches = new HashMap<>();
        List<Statement> branch = new ArrayList<>();
        while (!(check(ENDIF) || check(ELSEIF) || check(ELSE)) && !isAtEnd()) {
            branch.add(declaration());
        }
        branches.put(condition, new Statement.BlockStatement(branch));
        while (match(ELSEIF)) {
            consume(LEFT_PAREN, "Expect '(' after 'if'.");
            condition = expression();
            consume(RIGHT_PAREN, "Expect ')' after if condition.");
            branch = new ArrayList<>();
            while (!(check(ENDIF) || check(ELSE) || check(ELSEIF)) && !isAtEnd()) {
                branch.add(declaration());
            }
            branches.put(condition, new Statement.BlockStatement(branch));
        }
        Statement elseBranch = null;
        if (match(ELSE)) {
            List<Statement> statements = new ArrayList<>();
            while (!check(ENDIF) && !isAtEnd()) {
                statements.add(declaration());
            }
            elseBranch = new Statement.BlockStatement(statements);
        }
        consume(ENDIF, "Expect 'endif' after if statement");
        return new Statement.IfStatement(branches, elseBranch);
    }

    /**
     * while statement
     */
    private Statement whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        try {
            loopDepth++;
            Statement body = new Statement.BlockStatement(whileBlock());
            return new Statement.WhileStatement(condition, body);
        } finally {
            loopDepth--;
        }
    }

    /**
     * for statement
     */
    private Statement forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        Statement initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(NUM)) {
            initializer = varDeclaration(previous());
            consume(SEMICOLON, "Expect ';' after initializer.");
        } else {
            initializer = expressionStatement();
            consume(SEMICOLON, "Expect ';' after initializer expression.");
        }
        Expression condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");
        Statement increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = statement();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        try {
            loopDepth++;
            Statement body = new Statement.BlockStatement(forBlock());

            if (increment != null) {
                body = new Statement.BlockStatement(
                        Arrays.asList(body, increment)
                );
            }
            if (condition == null) condition = new Expression.LiteralExpression(true);
            body = new Statement.WhileStatement(condition, body);

            if (initializer != null) {
                body = new Statement.BlockStatement(Arrays.asList(initializer, body));
            }
            return body;
        } finally {
            loopDepth--;
        }
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
     * block statements
     *
     * @return list of statements inside block
     */
    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while (!check(ENDBLOCK) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(ENDBLOCK, "Expect 'endblock' after block.");
        return statements;
    }

    /**
     * while block statements
     *
     * @return list of statements inside while block
     */
    private List<Statement> whileBlock() {
        List<Statement> statements = new ArrayList<>();

        while (!check(ENDWHILE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(ENDWHILE, "Expect 'endwhile' after while statement.");
        return statements;
    }

    /**
     * for block statements
     *
     * @return list of statements inside for block
     */
    private List<Statement> forBlock() {
        List<Statement> statements = new ArrayList<>();

        while (!check(ENDFOR) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(ENDFOR, "Expect 'endfor' after for statement.");
        return statements;
    }

    /**
     * for block statements
     *
     * @return list of statements inside for block
     */
    private List<Statement> functionBlock() {
        List<Statement> statements = new ArrayList<>();

        while (!check(STOP) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(STOP, "Expect 'stop' after function statement.");
        return statements;
    }

    /**
     * expression -> or
     */
    private Expression expression() {
        return or();
    }

    /**
     * or -> and
     */
    private Expression or() {
        Expression left = and();
        while (match(OR)) {
            Token operator = previous();
            Expression right = and();
            left = new Expression.LogicalExpression(left, operator, right);
        }
        return left;
    }

    /**
     * or -> and
     */
    private Expression and() {
        Expression left = equality();
        while (match(AND)) {
            Token operator = previous();
            Expression right = equality();
            left = new Expression.LogicalExpression(left, operator, right);
        }
        return left;
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

        return call();
    }

    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(getCurrentToken(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expression.CallExpression(callee, paren, arguments);
    }

    /**
     * function call
     */
    private Expression call() {
        Expression expr = primary();
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }
        return expr;
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
