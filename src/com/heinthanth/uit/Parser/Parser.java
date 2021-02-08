package com.heinthanth.uit.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.token_t;
import com.heinthanth.uit.Runtime.Expression;
import com.heinthanth.uit.Runtime.Statement;
import com.heinthanth.uit.Utils.ErrorHandler;

import static com.heinthanth.uit.Lexer.token_t.*;

class ParseError extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -9119994735797982514L;
};

public class Parser {
    // tokenizer ကနေ ရလာတဲ့ token array
    private final List<Token> tokens;

    // error handle လုပ်ဖို့ handler
    private ErrorHandler errorHandler;

    // parser cursor ရဲ့ position
    private int current = 0;

    // loop ထဲမှာ ဟုတ်မဟုတ် စစ်ဖို့။
    private int loopDepth = 0;

    private boolean fromREPL;

    // entry point တွေ့ မတွေ့ စစ်ဖို့။
    private boolean foundStart = false;

    // parser constructor
    public Parser(List<Token> tokens, ErrorHandler errorHandler, boolean fromREPL) {
        this.tokens = tokens;
        this.errorHandler = errorHandler;
        this.fromREPL = fromREPL;
    }

    // token တွေကို parse မယ်။
    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isEOF()) {
            Statement dec = declaration();
            if (dec != null)
                statements.add(dec);
        }
        Token EOF_t = tokens.get(tokens.size() - 1);
        if (!fromREPL) {
            if (!foundStart) {
                errorHandler.reportError(EOF_t, "'start', 'stop' is needed for ENTRY unless running from REPL.");
            } else {
                statements.add(new Statement.ExpressionStatement(new Expression.CallExpression(
                        new Expression.VariableAccessExpression(new Token(IDENTIFIER, "__uit_start", -1, -1)),
                        new Token(RIGHT_PAREN, ")", -1, -1), new ArrayList<>())));
            }
        }
        return statements;
    }

    // grammar.txt မှာရေးထားတဲ့အတိုင်း precedence တွေနဲ့ parse သွားမယ်။
    // အရင်ဆုံး declaration

    // var declaration ဘာညာစစ်မယ်။
    private Statement declaration() {
        try {
            if (match(START))
                return mainFunctionDeclaration();
            if (match(CLASS))
                return classDeclaration();
            if (match(FRT_VOID))
                return functionDeclaration();
            if (match(VT_NUMBER, VT_STRING, VT_BOOLEAN))
                return variableDeclaration();
            if (match(OBJECT))
                return objectDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    /**
     * entry point of program
     */
    private Statement mainFunctionDeclaration() {
        foundStart = true;
        Token name = new Token(IDENTIFIER, "__uit_start", -1, -1);
        List<Statement> instructions = block(STOP, "stop");
        return new Statement.FunctionStatement(null, name, new ArrayList<>(), instructions);
    }

    private Statement classDeclaration() {
        Token identifier = expect(IDENTIFIER, "Expect class identifier.");
        Map<Statement.VariableDeclarationStatement, Token> properties = new HashMap<>();
        Map<Statement.FunctionStatement, Token> methods = new HashMap<>();

        Expression.VariableAccessExpression parent = null;
        if (match(EXTENDS)) {
            expect(IDENTIFIER, "Expect superclass name.");
            parent = new Expression.VariableAccessExpression(previous());
        }

        while (!check(ENDCLASS) && !isEOF()) {
            if (check(PUBLIC) || check(PRIVATE) || check(PROTECTED)) {
                Token access = advance();
                advance();

                Statement field = variableDeclaration();
                if (field instanceof Statement.VariableDeclarationStatement) {
                    properties.put((Statement.VariableDeclarationStatement) field, access);
                } else if (field instanceof Statement.FunctionStatement) {
                    methods.put((Statement.FunctionStatement) field, access);
                }
            }
        }
        expect(ENDCLASS, "Expect 'endclass' after class statement.");
        return new Statement.ClassStatement(identifier, parent, properties, methods);
    }

    /**
     * variable အသစ်ကို declare လုပ်မယ်။
     */
    private Statement variableDeclaration() {
        Token type = previous();
        Token next = peekNext();
        if (next != null && next.type == LEFT_PAREN) {
            return functionDeclaration();
        }
        Token identifier = expect(IDENTIFIER, "Expect variable identifier.");

        Expression initializer = null;
        if (match(ASSIGN)) {
            initializer = expression();
        }
        expect(SEMICOLON, "Expect ';' after statement.");

        return new Statement.VariableDeclarationStatement(type, identifier, initializer);
    }

    private Statement objectDeclaration() {
        Token type = previous();
        Token identifier = expect(IDENTIFIER, "Expect variable identifier.");

        Expression initializer = null;
        if (match(ASSIGN)) {
            initializer = expression();
        }
        expect(SEMICOLON, "Expect ';' after statement.");

        return new Statement.VariableDeclarationStatement(type, identifier, initializer);
    }

    private Statement functionDeclaration() {
        Token type = previous();
        Token identifier = expect(IDENTIFIER, "Expect function identifier.");

        match(LEFT_PAREN);

        List<List<Token>> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(getCurrentToken(), "Can't have more than 255 parameters.");
                }
                List<Token> param = new ArrayList<>();
                if (check(FRT_VOID))
                    throw error(getCurrentToken(), "Invalid data type for variable.");
                if (!(check(VT_NUMBER) || check(VT_STRING) || check(VT_BOOLEAN))) {
                    throw error(getCurrentToken(), "Expect parameter data type.");
                } else {
                    param.add(advance());
                }
                param.add(expect(IDENTIFIER, "Expect parameter name."));
                parameters.add(param);
            } while (match(COMMA));
        }
        expect(RIGHT_PAREN, "Expect ')' after function parameters.");
        if (match(THEN)) {
            return new Statement.FunctionStatement(type, identifier, parameters, Arrays.asList(statement()));
        }
        List<Statement> instructions = block(ENDFUNC, "endfunc");
        return new Statement.FunctionStatement(type, identifier, parameters, instructions);
    }

    // statement တွေစစ်မယ်။ မဟုတ်ရင် expression statement ပေါ့။
    private Statement statement() {
        if (match(IF))
            return ifStatement();
        if (match(WHILE))
            return whileStatement();
        if (match(FOR))
            return forStatement();
        if (match(BREAK))
            return breakStatement();
        if (match(CONTINUE))
            return continueStatement();
        if (match(RETURN))
            return returnStatement();
        if (match(OUTPUT))
            return outputStatement();
        if (match(BLOCK))
            return new Statement.BlockStatement(block(ENDBLOCK, "endblock"));
        if (match(LEFT_CURLY))
            return new Statement.BlockStatement(block(RIGHT_CURLY, "}"));
        if (match(INPUT))
            return inputStatement();
        if (match(SEMICOLON))
            return null;

        // ကျန်တာကတော့ expression ပေါ့။
        return expressionStatement();
    }

    /**
     * while statement ဖြစ်အောင် parse မယ်။
     *
     * @return
     */
    private Statement whileStatement() {
        expect(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        expect(RIGHT_PAREN, "Expect ')' after condition.");

        try {
            loopDepth++;
            if (match(THEN)) {
                Statement instructions = statement();
                return new Statement.WhileStatement(condition, instructions);
            }
            Statement instructions = new Statement.BlockStatement(block(ENDWHILE, "endblock"));
            return new Statement.WhileStatement(condition, instructions);
        } finally {
            loopDepth--;
        }
    }

    /**
     * for statement ကို while statement အဖြစ်ပြောင်းမယ်။
     */
    private Statement forStatement() {
        expect(LEFT_PAREN, "Expect '(' after 'while'.");

        Statement initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VT_NUMBER, VT_STRING, VT_BOOLEAN)) {
            initializer = variableDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expression condition = null;
        if (!check(SEMICOLON))
            condition = expression();
        expect(SEMICOLON, "Expect ';' after loop condition.");

        Expression increment = null;
        if (!check(RIGHT_PAREN))
            increment = expression();
        expect(RIGHT_PAREN, "Expect ')' after loop clause.");

        try {
            loopDepth++;
            Statement instructions;
            if (match(THEN)) {
                instructions = statement();
            } else {
                instructions = new Statement.BlockStatement(block(ENDFOR, "endfor"));
            }

            if (increment != null) {
                instructions = new Statement.BlockStatement(
                        Arrays.asList(instructions, new Statement.ExpressionStatement(increment)));
            }
            if (condition == null)
                condition = new Expression.LiteralExpression(new Token(BOOLEAN_LITERAL, "true", true, -1, -1));

            instructions = new Statement.WhileStatement(condition, instructions);

            if (initializer != null) {
                instructions = new Statement.BlockStatement(Arrays.asList(initializer, instructions));
            }
            return instructions;
        } finally {
            loopDepth--;
        }
    }

    private Statement breakStatement() {
        expect(SEMICOLON, "Expect ';' after 'break'.");
        if (loopDepth == 0) {
            error(previous(), "'break' must be use inside loop.");
        }
        return new Statement.BreakStatement();
    }

    private Statement continueStatement() {
        expect(SEMICOLON, "Expect ';' after 'continue'.");
        if (loopDepth == 0) {
            error(previous(), "'continue' must be use inside loop.");
        }
        return new Statement.ContinueStatement();
    }

    private Statement returnStatement() {
        Token ret = previous();
        Expression value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }
        expect(SEMICOLON, "Expect ';' after return");
        return new Statement.ReturnStatement(ret, value);
    }

    /**
     * if statement ကို parse မယ်။
     */
    private Statement ifStatement() {
        expect(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        expect(RIGHT_PAREN, "Expect ')' after if condition.");

        Map<Expression, Statement> branches = new HashMap<>();
        List<Statement> branch = new ArrayList<>();

        // single line then statement
        if (match(THEN)) {
            branch.add(statement());
            branches.put(condition, new Statement.BlockStatement(branch));
            return new Statement.IfStatement(branches, null);
        }

        // checking if branch
        while (!(check(ENDIF) || check(ELSEIF) || check(ELSE)) && !isEOF()) {
            branch.add(declaration());
        }
        branches.put(condition, new Statement.BlockStatement(branch));
        // checking elseif
        while (match(ELSEIF)) {
            expect(LEFT_PAREN, "Expect '(' after 'elseif'.");
            condition = expression();
            expect(RIGHT_PAREN, "Expect ')' after if condition.");

            branch = new ArrayList<>();
            while (!(check(ENDIF) || check(ELSE) || check(ELSEIF)) && !isEOF()) {
                branch.add(declaration());
            }
            branches.put(condition, new Statement.BlockStatement(branch));
        }
        // checking else branch
        Statement elseBranch = null;
        if (match(ELSE)) {
            List<Statement> statements = new ArrayList<>();
            while (!check(ENDIF) && !isEOF()) {
                statements.add(declaration());
            }
            elseBranch = new Statement.BlockStatement(statements);
        }
        expect(ENDIF, "Expect 'endif' after if statement");
        return new Statement.IfStatement(branches, elseBranch);
    }

    /**
     * block level statement တွေကို parse ဖို့
     *
     * @param end
     * @param stringForm
     * @return
     */
    private List<Statement> block(token_t end, String stringForm) {
        List<Statement> statements = new ArrayList<>();
        while (!check(end) && !isEOF()) {
            statements.add(declaration());
        }
        expect(end, "Expect '" + stringForm + "' after block statement");
        return statements;
    }

    // output statement parse မယ်
    private Statement outputStatement() {
        Expression value = expression();
        expect(SEMICOLON, "Missing ';' after statement");
        return new Statement.OutputStatement(value);
    }

    // expression statement
    private Statement expressionStatement() {
        Expression expression = expression();
        expect(SEMICOLON, "Missing ';' after statement");
        return new Statement.ExpressionStatement(expression);
    }

    // expression ထက် precedence ပုိမြင့်တာက logic or
    private Expression expression() {
        return assignment();
    }

    private Statement inputStatement() {
        Token identifier = expect(IDENTIFIER, "Expect variable identifier.");
        expect(SEMICOLON, "Missing ';' after statement");
        return new Statement.ExpressionStatement(new Expression.InputExpression(identifier));
    }

    /**
     * ရှိပြီးသား variable ကို value အသစ်ထည့်မယ်။
     *
     * @return
     */
    private Expression assignment() {
        if (match(SET)) {
            Expression identifier = logicOr();
            Token eq = expect(ASSIGN, "Expect '=' in variable assignment.");
            Expression value = assignment();

            if (identifier instanceof Expression.VariableAccessExpression) {
                Token name = ((Expression.VariableAccessExpression) identifier).identifier;
                return new Expression.VariableAssignExpression(name, value);
            } else if (identifier instanceof Expression.GetExpression) {
                Expression.GetExpression member = (Expression.GetExpression) identifier;
                return new Expression.SetExpression(member.object, member.name, value, member.fromThis);
            } else {
                error(eq, "Invalid assignment.");
            }
        }
        return logicOr();
    }

    // expression ထက်ပိုမြင့်တာက logic and
    private Expression logicOr() {
        Expression left = logicAnd();
        while (match(OR)) {
            Token operator = previous();
            Expression right = logicAnd();
            left = new Expression.LogicalExpression(left, operator, right);
        }
        return left;
    }

    // logic or ထက်ပိုမြင့်တာက logic equal
    private Expression logicAnd() {
        Expression left = logicEqual();
        while (match(AND)) {
            Token operator = previous();
            Expression right = logicEqual();
            left = new Expression.LogicalExpression(left, operator, right);
        }
        return left;
    }

    // equal ထက် ပိုမြင့်တာက comparison: သူ့ကို အရင်ရှာမယ်။ ပြီးရင် binary operation
    // လုပ်မယ်။
    private Expression logicEqual() {
        Expression left = comparison();
        // ==, != ရှိမရှိ ... ရှိရင် binary operation ေပါ့ မဟုတ်ရင် ကျန် node
        // အတိုင်းပေါ့။
        while (match(NOT_EQUAL, EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    // comparison ထက်မြင့်တာက term
    private Expression comparison() {
        Expression left = term();
        // >, <, >=, <= ရှိမရှိ ... ရှိရင် binary operation ေပါ့ မဟုတ်ရင် ကျန် node
        // အတိုင်းပေါ့။
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    // term ထက်မြင့်တာ factor
    private Expression term() {
        Expression left = factor();
        // +, -, . ရှိမရှိ ... ရှိရင် binary operation ေပါ့ မဟုတ်ရင် ကျန် node
        // အတိုင်းပေါ့။
        while (match(MINUS, PLUS, DOT)) {
            Token operator = previous();
            Expression right = factor();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    // factor ထက်မြင့်တာ power
    private Expression factor() {
        Expression left = power();
        // / * ရှိမရှိ ... ရှိရင် binary operation ေပါ့ မဟုတ်ရင် ကျန် node
        // အတိုင်းပေါ့။
        while (match(PERCENT, SLASH, STAR)) {
            Token operator = previous();
            Expression right = power();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    // power ထက်မြင့်တာ unary
    private Expression power() {
        Expression left = unary();
        // ^ ရှိမရှိ ... ရှိရင် binary operation ေပါ့ မဟုတ်ရင် ကျန် node
        // အတိုင်းပေါ့။
        while (match(CARET)) {
            Token operator = previous();
            Expression right = unary();
            left = new Expression.BinaryExpression(left, operator, right);
        }
        return left;
    }

    // unary (!, -)
    private Expression unary() {
        // -, ! မရှိရင် primary ဆီသွားမယ်။
        if (match(NOT, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.UnaryExpression(operator, right);
        }
        return prefix();
    }

    // prefix (++a, --a)
    private Expression prefix() {
        if (match(INCREMENT, DECREMENT)) {
            Token operator = previous();
            Expression right = prefix();
            if (operator.type.toString() == "INCREMENT") {
                return new Expression.IncrementExpression(right, operator, "prefix");
            } else {
                return new Expression.DecrementExpression(right, operator, "prefix");
            }
        }
        return postfix();
    }

    // postfix ( a++, a-- )
    private Expression postfix() {
        Expression expr = call();
        if (match(INCREMENT, DECREMENT)) {
            Token operator = previous();
            if (operator.type.toString() == "INCREMENT") {
                expr = new Expression.IncrementExpression(expr, operator, "postfix");
            } else {
                expr = new Expression.DecrementExpression(expr, operator, "postfix");
            }
        }
        return expr;
    }

    // call expression
    private Expression call() {
        if (match(NEW)) {
            Expression expr = primary();
            expect(LEFT_PAREN, "Expect '(' after class identifier.");
            return finishCall(expr);
        }
        Expression expr = primary();
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DART)) {
                Token name = expect(IDENTIFIER, "Expect member name after '->'.");
                if (expr instanceof Expression.VariableAccessExpression) {
                    expr = new Expression.GetExpression(expr, name, false);
                } else if (expr instanceof Expression.ThisExpression) {
                    expr = new Expression.GetExpression(expr, name, true);
                }
            } else {
                break;
            }
        }
        return expr;
    }

    // finish call
    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(getCurrentToken(), "Callable can't have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token paren = expect(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expression.CallExpression(callee, paren, arguments);
    }

    // ဒါက ထပ်ခွဲမရတော့တဲ့ basic element တွေ literal ဘာညာ
    private Expression primary() {
        if (match(NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL))
            return new Expression.LiteralExpression(previous());

        if (match(THIS))
            return new Expression.ThisExpression(previous());

        if (match(IDENTIFIER))
            return new Expression.VariableAccessExpression(previous());

        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            expect(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.GroupingExpression(expr);
        }

        throw error(getCurrentToken(), "Expect expression.");
    }

    // token type မဟုတ်ရင် error တက်ဖို့
    private Token expect(token_t type, String message) {
        if (check(type))
            return advance();
        throw error(previous(), message);
    }

    // parse error တက်ဖို့
    private ParseError error(Token token, String message) {
        errorHandler.reportError(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isEOF()) {
            if (previous().type == SEMICOLON)
                return;
            // new line ဖြစ်တဲ့ token အားလုံး
            switch (getCurrentToken().type) {
                case VT_STRING:
                case VT_NUMBER:
                case VT_BOOLEAN:
                case FRT_VOID:
                case BLOCK:
                case IF:
                case FOR:
                case WHILE:
                case BREAK:
                case CONTINUE:
                case FUNC:
                case RETURN:
                case INPUT:
                case OUTPUT:
                    return;
                default:
                    advance();
                    break;
            }
        }
    }

    // လက်ရှိ token type ကို စစ်ဖို့
    private boolean match(token_t... types) {
        for (token_t type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    // သူလည်း token type စစ်ဖို့ပဲ။ match က multiple types
    private boolean check(token_t type) {
        if (isEOF())
            return false;
        return getCurrentToken().type == type;
    }

    // လက်ရှိ token ကို ပြန်ပေးတယ်။ index 1 တိုးတယ်။
    private Token advance() {
        if (!isEOF())
            current++;
        return previous();
    }

    // index တို့ 1 မတိုးဘဲ ကြည့်ကြည့်မယ်။
    private Token peekNext() {
        if (current + 1 >= tokens.size())
            return null;
        return tokens.get(current + 1);
    }

    // အဆုံး token ဟုတ် မဟုတ် စစ်ဖို့
    private boolean isEOF() {
        return getCurrentToken().type == EOF;
    }

    // လက်ရှိ token ကို ယူဖို့
    private Token getCurrentToken() {
        return tokens.get(current);
    }

    // အရင် token ကို ကြည့်ဖို့
    private Token previous() {
        return tokens.get(current - 1);
    }
}