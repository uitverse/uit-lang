package com.heinthanth.uit.Node;

import com.heinthanth.uit.Lexer.Token;

public abstract class Expression {
    public interface Visitor<R> {
        R visitBinaryExpression(BinaryExpression expr);

        R visitGroupingExpression(GroupingExpression expr);

        R visitLiteralExpression(LiteralExpression expr);

        R visitUnaryExpression(UnaryExpression expr);
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
}
