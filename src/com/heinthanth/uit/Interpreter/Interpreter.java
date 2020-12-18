package com.heinthanth.uit.Interpreter;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Node.Expression;
import com.heinthanth.uit.Node.Statement;
import com.heinthanth.uit.Uit;

import java.util.List;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    /**
     * visit to literal expression -> Java Object (double, string, bool)
     */
    @Override
    public Object visitLiteralExpression(Expression.LiteralExpression expression) {
        return expression.value;
    }

    /**
     * visit grouping expression -> recursive evaluation
     */
    @Override
    public Object visitGroupingExpression(Expression.GroupingExpression expression) {
        return evaluate(expression.expression);
    }

    /**
     * evaluate unary expression to value
     */
    @Override
    public Object visitUnaryExpression(Expression.UnaryExpression expression) {
        Object right = evaluate(expression.right);
        switch (expression.operator.type) {
            case MINUS:
                checkNumberOperand(expression.operator, right);
                return -(double) right;
            case NOT:
                return !isTrue(right);
        }
        return null;
    }

    /**
     * Get variable value from identifier
     *
     * @param expression variable access expression
     * @return value of variable
     */
    @Override
    public Object visitVariableExpression(Expression.VariableExpression expression) {
        return environment.get(expression.name);
    }

    /**
     * evaluate binary operation
     *
     * @param expression binary expression
     * @return result from expression
     */
    @Override
    public Object visitBinaryExpression(Expression.BinaryExpression expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case PLUS:
                checkNumberOperands(expression.operator, left, right);
                return (double) left + (double) right;
            case MINUS:
                checkNumberOperands(expression.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(expression.operator, left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expression.operator, left, right);
                return (double) left * (double) right;
            case GREATER:
                checkNumberOperands(expression.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expression.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expression.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expression.operator, left, right);
                return (double) left <= (double) right;
            case NOT_EQUAL:
                return !isEqual(left, right);
            case EQUAL:
                return isEqual(left, right);
        }
        return null;
    }

    /**
     * interpret expression statement
     *
     * @param statement expression statement
     * @return null
     */
    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
        evaluate(statement.expression);
        return null;
    }

    /**
     * interpret output statement
     *
     * @param stmt Output statement
     * @return null
     */
    @Override
    public Void visitOutputStatement(Statement.OutputStatement stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    /**
     * Declare variable
     *
     * @param stmt variable declaration statement
     * @return null
     */
    @Override
    public Void visitVariableDeclareStatement(Statement.VariableDeclareStatement stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.typeDef, stmt.name, value);
        return null;
    }

    /**
     * assign variable with new value
     *
     * @param stmt variable declaration statement
     * @return null
     */
    @Override
    public Void visitVariableAssignStatement(Statement.VariableAssignStatement stmt) {
        Object value = evaluate(stmt.initializer);
        environment.assign(stmt.name, value);
        return null;
    }

    @Override
    public Void visitBlockStatement(Statement.BlockStatement statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    /**
     * Environment for identifier
     */
    private Environment environment = new Environment();

    /**
     * Interpret parse statements
     *
     * @param statements Statements to interpret
     */
    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Uit.runtimeError(error);
        }
    }

    /**
     * Execute statements
     *
     * @param stmt Statement to execute
     */
    private void execute(Statement stmt) {
        stmt.accept(this);
    }

    /**
     * Execute list of statements
     *
     * @param statements  list of statements to execute
     * @param environment current environment
     */
    void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    /**
     * Evaluate expression to value
     *
     * @return value of expression
     */
    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    /**
     * Get boolean representation and check true
     *
     * @param object object to check
     */
    private boolean isTrue(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    /**
     * check give object are equal
     *
     * @param a Object one
     * @param b Object two
     * @return true if object are equal
     */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    /**
     * checking for number in unary operation
     *
     * @param operator Operator of expression
     * @param operand  Operand
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    /**
     * check number operand for binary operation
     *
     * @param operator operator of expression
     * @param left     left object
     * @param right    right object
     */
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    /**
     * Convert java object to string
     *
     * @param object object to convert
     * @return string representation of object
     */
    private String stringify(Object object) {
        if (object == null) return "null";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
}
