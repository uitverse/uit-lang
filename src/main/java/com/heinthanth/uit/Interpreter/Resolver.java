package com.heinthanth.uit.Interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Runtime.Expression;
import com.heinthanth.uit.Runtime.Statement;
import com.heinthanth.uit.Runtime.Expression.BinaryExpression;
import com.heinthanth.uit.Runtime.Expression.CallExpression;
import com.heinthanth.uit.Runtime.Expression.DecrementExpression;
import com.heinthanth.uit.Runtime.Expression.GroupingExpression;
import com.heinthanth.uit.Runtime.Expression.IncrementExpression;
import com.heinthanth.uit.Runtime.Expression.InputExpression;
import com.heinthanth.uit.Runtime.Expression.LiteralExpression;
import com.heinthanth.uit.Runtime.Expression.LogicalExpression;
import com.heinthanth.uit.Runtime.Expression.UnaryExpression;
import com.heinthanth.uit.Runtime.Expression.VariableAccessExpression;
import com.heinthanth.uit.Runtime.Expression.VariableAssignExpression;
import com.heinthanth.uit.Runtime.Statement.BlockStatement;
import com.heinthanth.uit.Runtime.Statement.BreakStatement;
import com.heinthanth.uit.Runtime.Statement.ContinueStatement;
import com.heinthanth.uit.Runtime.Statement.ExpressionStatement;
import com.heinthanth.uit.Runtime.Statement.FunctionStatement;
import com.heinthanth.uit.Runtime.Statement.IfStatement;
import com.heinthanth.uit.Runtime.Statement.OutputStatement;
import com.heinthanth.uit.Runtime.Statement.ReturnStatement;
import com.heinthanth.uit.Runtime.Statement.VariableDeclarationStatement;
import com.heinthanth.uit.Runtime.Statement.WhileStatement;
import com.heinthanth.uit.Utils.ErrorHandler;

public class Resolver implements Expression.Visitor<Void>, Statement.Visitor<Void> {
    /**
     * interpreter
     */
    private final Interpreter interpreter;

    /**
     * error handler instance
     */
    private ErrorHandler errorHandler;

    // scope list
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    private function_t currentFunction = function_t.NONE;

    /**
     * interpreter ကို initialize လုပ်ဖို့
     *
     * @param interpreter
     */
    public Resolver(Interpreter interpreter, ErrorHandler errorHandler) {
        this.interpreter = interpreter;
        this.errorHandler = errorHandler;
    }

    private enum function_t {
        NONE, FUNCTION
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {
        // scope အသစ်လုပ်မယ်။
        beginScope();
        resolve(statement.statements);
        // scope ပြန်ဖျက်မယ်။
        endScope();
        return null;
    }

    @Override
    public Void visitVariableDeclarationStatement(VariableDeclarationStatement statement) {
        declare(statement.identifier);
        if (statement.initializer != null)
            resolve(statement.initializer);
        define(statement.identifier);
        return null;
    }

    @Override
    public Void visitVariableAccessExpression(VariableAccessExpression expression) {
        if (!scopes.isEmpty() && scopes.peek().get(expression.identifier.lexeme) == Boolean.FALSE) {
            errorHandler.reportError(expression.identifier, "Can't read local variable in its own initializer.");
        }
        resolveLocal(expression, expression.identifier);
        return null;
    }

    @Override
    public Void visitInputExpression(InputExpression expression) {
        resolveLocal(expression, expression.identifier);
        return null;
    }

    @Override
    public Void visitVariableAssignExpression(VariableAssignExpression expression) {
        resolve(expression.value);
        resolveLocal(expression, expression.identifier);
        return null;
    }

    @Override
    public Void visitIncrementExpression(IncrementExpression expression) {
        VariableAccessExpression var = (VariableAccessExpression) expression.identifier;
        resolveLocal(var, var.identifier);
        return null;
    }

    @Override
    public Void visitDecrementExpression(DecrementExpression expression) {
        VariableAccessExpression var = (VariableAccessExpression) expression.identifier;
        resolveLocal(var, var.identifier);
        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatement stmt) {
        declare(stmt.identifier);
        define(stmt.identifier);

        resolveFunction(stmt, function_t.FUNCTION);
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        resolve(statement.expression);
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) {
        for (Map.Entry<Expression, Statement> branch : stmt.branches.entrySet()) {
            resolve(branch.getKey());
            resolve(branch.getValue());
        }
        if (stmt.elseBranch != null)
            resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitOutputStatement(OutputStatement statement) {
        resolve(statement.expression);
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement stmt) {
        if (currentFunction == function_t.NONE) {
            errorHandler.reportError(stmt.ret, "Can't use return from top-level code (outside of function).");
        }
        if (stmt.value != null) {
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement statement) {
        resolve(statement.condition);
        resolve(statement.instructions);
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitLogicalExpression(LogicalExpression expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitCallExpression(CallExpression expression) {
        resolve(expression.callee);
        for (Expression argument : expression.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGroupingExpression(GroupingExpression expression) {
        resolve(expression.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression expression) {
        return null;
    }

    @Override
    public Void visitBreakStatement(BreakStatement statement) {
        return null;
    }

    @Override
    public Void visitContinueStatement(ContinueStatement statement) {
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression expression) {
        resolve(expression.right);
        return null;
    }

    /**
     * block statement အတွက်
     *
     * @param statements
     */
    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    /**
     * fake interpret မယ်။
     */
    private void resolve(Statement stmt) {
        stmt.accept(this);
    }

    /**
     * expression တွေကို evaluate လုပ်မယ်။
     */
    private void resolve(Expression expr) {
        expr.accept(this);
    }

    private void resolveLocal(Expression expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Statement.FunctionStatement function, function_t type) {
        function_t enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (List<Token> param : function.parameters) {
            declare(param.get(1));
            define(param.get(1));
        }
        resolve(function.instructions);
        endScope();
        currentFunction = enclosingFunction;
    }

    /**
     * variable declare မယ်။
     */
    private void declare(Token name) {
        if (scopes.isEmpty())
            return;
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            errorHandler.reportError(name, "variable with name '" + name.lexeme + "' exists in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    /**
     * variable define လုပ်မယ်။
     */
    private void define(Token name) {
        if (scopes.isEmpty())
            return;
        scopes.peek().put(name.lexeme, true);
    }

    /**
     * scope အသစ်လုပ်မယ်။
     */
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    /**
     * scope ပြန်ဖျက်မယ်။
     */
    private void endScope() {
        scopes.pop();
    }
}
