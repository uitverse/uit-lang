package com.heinthanth.uit.Interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Runtime.Expression;
import com.heinthanth.uit.Runtime.Statement;
import com.heinthanth.uit.Runtime.Expression.GetExpression;
import com.heinthanth.uit.Runtime.Expression.SetExpression;
import com.heinthanth.uit.Runtime.Expression.ThisExpression;
import com.heinthanth.uit.Runtime.Statement.ClassStatement;
import com.heinthanth.uit.Runtime.Statement.FunctionStatement;
import com.heinthanth.uit.Runtime.Statement.VariableDeclarationStatement;
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
        NONE, FUNCTION, METHOD, INITIALIZER
    }

    private enum class_t {
        NONE, CLASS
    }

    private class_t currentClass = class_t.NONE;

    @Override
    public Void visitBlockStatement(Statement.BlockStatement statement) {
        // scope အသစ်လုပ်မယ်။
        beginScope();
        resolve(statement.statements);
        // scope ပြန်ဖျက်မယ်။
        endScope();
        return null;
    }

    @Override
    public Void visitThisExpression(ThisExpression expression) {
        if (currentClass == class_t.NONE) {
            errorHandler.reportError(expression.thiss, "Can't use this from outside of a class.");
            return null;
        }
        resolveLocal(expression, expression.thiss);
        return null;
    }

    @Override
    public Void visitClassStatement(ClassStatement statement) {
        class_t enclosingClass = currentClass;
        currentClass = class_t.CLASS;

        declare(statement.identifier);
        define(statement.identifier);

        if (statement.parent != null && statement.identifier.lexeme.equals(statement.parent.identifier.lexeme)) {
            errorHandler.reportError(statement.parent.identifier, "A class can't inherit from itself.");
        }

        beginScope();
        scopes.peek().put("this", true);

        for (Map.Entry<VariableDeclarationStatement, Token> var : statement.properties.entrySet()) {
            declare(var.getKey().identifier);
            if (var.getKey().initializer != null)
                resolve(var.getKey().initializer);
            define(var.getKey().identifier);
        }

        for (Map.Entry<FunctionStatement, Token> method : statement.methods.entrySet()) {
            function_t declaration = function_t.METHOD;
            if ("__construct".equals(method.getKey().identifier.lexeme)) {
                declaration = function_t.INITIALIZER;
            }
            resolveFunction(method.getKey(), declaration);
        }

        endScope();

        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitVariableDeclarationStatement(Statement.VariableDeclarationStatement statement) {
        declare(statement.identifier);
        if (statement.initializer != null)
            resolve(statement.initializer);
        define(statement.identifier);
        return null;
    }

    @Override
    public Void visitVariableAccessExpression(Expression.VariableAccessExpression expression) {
        if (!scopes.isEmpty() && scopes.peek().get(expression.identifier.lexeme) == Boolean.FALSE) {
            errorHandler.reportError(expression.identifier, "Can't read local variable in its own initializer.");
        }
        resolveLocal(expression, expression.identifier);
        return null;
    }

    @Override
    public Void visitInputExpression(Expression.InputExpression expression) {
        resolveLocal(expression, expression.identifier);
        return null;
    }

    @Override
    public Void visitVariableAssignExpression(Expression.VariableAssignExpression expression) {
        resolve(expression.value);
        resolveLocal(expression, expression.identifier);
        return null;
    }

    @Override
    public Void visitIncrementExpression(Expression.IncrementExpression expression) {
        Expression.VariableAccessExpression var = (Expression.VariableAccessExpression) expression.identifier;
        resolveLocal(var, var.identifier);
        return null;
    }

    @Override
    public Void visitDecrementExpression(Expression.DecrementExpression expression) {
        Expression.VariableAccessExpression var = (Expression.VariableAccessExpression) expression.identifier;
        resolveLocal(var, var.identifier);
        return null;
    }

    @Override
    public Void visitFunctionStatement(Statement.FunctionStatement stmt) {
        declare(stmt.identifier);
        define(stmt.identifier);

        resolveFunction(stmt, function_t.FUNCTION);
        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
        resolve(statement.expression);
        return null;
    }

    @Override
    public Void visitIfStatement(Statement.IfStatement stmt) {
        for (Map.Entry<Expression, Statement> branch : stmt.branches.entrySet()) {
            resolve(branch.getKey());
            resolve(branch.getValue());
        }
        if (stmt.elseBranch != null)
            resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitOutputStatement(Statement.OutputStatement statement) {
        resolve(statement.expression);
        return null;
    }

    @Override
    public Void visitReturnStatement(Statement.ReturnStatement stmt) {
        if (currentFunction == function_t.NONE) {
            errorHandler.reportError(stmt.ret, "Can't use return from top-level code (outside of function).");
        }
        if (stmt.value != null) {
            if (currentFunction == function_t.INITIALIZER) {
                errorHandler.reportError(stmt.ret, "Can't return a value from a constructor.");
            }
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitWhileStatement(Statement.WhileStatement statement) {
        resolve(statement.condition);
        resolve(statement.instructions);
        return null;
    }

    @Override
    public Void visitBinaryExpression(Expression.BinaryExpression expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitLogicalExpression(Expression.LogicalExpression expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitCallExpression(Expression.CallExpression expression) {
        resolve(expression.callee);
        for (Expression argument : expression.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGetExpression(GetExpression expression) {
        resolve(expression.object);
        return null;
    }

    @Override
    public Void visitSetExpression(SetExpression expression) {
        resolve(expression.value);
        resolve(expression.object);
        return null;
    }

    @Override
    public Void visitGroupingExpression(Expression.GroupingExpression expression) {
        resolve(expression.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpression(Expression.LiteralExpression expression) {
        return null;
    }

    @Override
    public Void visitBreakStatement(Statement.BreakStatement statement) {
        return null;
    }

    @Override
    public Void visitContinueStatement(Statement.ContinueStatement statement) {
        return null;
    }

    @Override
    public Void visitUnaryExpression(Expression.UnaryExpression expression) {
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
