package com.heinthanth.uit.Interpreter;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Node.Expression;
import com.heinthanth.uit.Node.Statement;
import com.heinthanth.uit.Uit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class Resolver implements Expression.Visitor<Void>, Statement.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBlockStatement(Statement.BlockStatement stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitVariableDeclareStatement(Statement.VariableDeclareStatement stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitFunctionStatement(Statement.FunctionStatement stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStatement(Statement.IfStatement stmt) {
        for (Map.Entry<Expression, Statement> branch : stmt.branches.entrySet()) {
            resolve(branch.getKey());
            resolve(branch.getValue());
        }
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitOutputStatement(Statement.OutputStatement stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStatement(Statement.ReturnStatement stmt) {
        if (currentFunction == FunctionType.NONE) {
            Uit.error(stmt.keyword, "Can't use return from top-level code (outside of function).");
        }
        if (stmt.value != null) {
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitBreakStatement(Statement.BreakStatement stmt) {
        return null;
    }

    @Override
    public Void visitWhileStatement(Statement.WhileStatement stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitVariableExpression(Expression.VariableExpression expr) {
        if (!scopes.isEmpty() &&
                scopes.peek().get(expr.name.sourceString) == Boolean.FALSE) {
            Uit.error(expr.name,
                    "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitVariableAssignExpression(Expression.VariableAssignExpression expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpression(Expression.BinaryExpression expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpression(Expression.CallExpression expr) {
        resolve(expr.callee);
        for (Expression argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGroupingExpression(Expression.GroupingExpression expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpression(Expression.LiteralExpression expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpression(Expression.LogicalExpression expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpression(Expression.UnaryExpression expr) {
        resolve(expr.right);
        return null;
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.sourceString)) {
            Uit.error(name,
                    "Identifier with this name exists in this scope.");
        }
        scope.put(name.sourceString, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.sourceString, true);
    }

    private void resolveLocal(Expression expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.sourceString)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Expression expr) {
        expr.accept(this);
    }

    private void resolve(Statement stmt) {
        stmt.accept(this);
    }

    private void resolveFunction(Statement.FunctionStatement function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (List<Token> param : function.params) {
            declare(param.get(1));
            define(param.get(1));
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }
}
