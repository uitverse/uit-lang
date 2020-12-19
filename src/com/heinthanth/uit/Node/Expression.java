package com.heinthanth.uit.Node;

import com.heinthanth.uit.Lexer.Token;

import java.util.List;

public abstract class Expression {
    public interface Visitor<R> {
        R visitBinaryExpression(BinaryExpression expr);

        R visitGroupingExpression(GroupingExpression expr);

        R visitLiteralExpression(LiteralExpression expr);

        R visitUnaryExpression(UnaryExpression expr);

        R visitVariableExpression(VariableExpression expr);

        R visitLogicalExpression(LogicalExpression expr);

        R visitCallExpression(CallExpression expr);

        R visitVariableAssignExpression(VariableAssignExpression expr);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    /**
     * Binary Operation
     */
    public static class BinaryExpression extends Expression {
        public final Expression left;
        public final Expression right;
        public final Token operator;

        public BinaryExpression(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }
    }

    /**
     * Grouping Expression
     */
    public static class GroupingExpression extends Expression {
        public final Expression expression;

        public GroupingExpression(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }
    }

    /**
     * Just Literal
     */
    public static class LiteralExpression extends Expression {
        public final Object value;

        public LiteralExpression(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }
    }

    /**
     * Unary Expression
     */
    public static class UnaryExpression extends Expression {
        public final Token operator;
        public final Expression right;

        public UnaryExpression(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }
    }

    /**
     * variable access expression
     */
    public static class VariableExpression extends Expression {
        public VariableExpression(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpression(this);
        }

        public final Token name;
    }

    /**
     * Logical expression
     */
    public static class LogicalExpression extends Expression {
        public LogicalExpression(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpression(this);
        }

        public final Expression left;
        public final Token operator;
        public final Expression right;
    }

    /**
     * Assign variable to new value
     */
    public static class VariableAssignExpression extends Expression {
        public VariableAssignExpression(Token name, Expression initializer) {
            this.name = name;
            this.value = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableAssignExpression(this);
        }

        public final Token name;
        public final Expression value;
    }

    /**
     * function call expression
     */
    public static class CallExpression extends Expression {
        public final Expression callee;
        public final Token paren;
        public final List<Expression> arguments;

        public CallExpression(Expression callee, Token paren, List<Expression> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpression(this);
        }
    }
}
