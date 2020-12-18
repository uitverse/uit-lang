package com.heinthanth.uit.Interpreter;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Node.Expression;
import com.heinthanth.uit.Uit;

public class Interpreter implements Expression.Visitor<Object> {
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

    public void interpret(Expression expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Uit.runtimeError(error);
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
