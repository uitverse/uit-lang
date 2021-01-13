package com.heinthanth.uit.Runtime;

import com.heinthanth.uit.Lexer.Token;

public abstract class Expression {

    public interface Visitor<R> {
        R visitBinaryExpression(BinaryExpression expression);
        R visitGroupingExpression(GroupingExpression expression);
        R visitLiteralExpression(LiteralExpression expression);
        R visitUnaryExpression(UnaryExpression expression);
        R visitVariableAccessExpression(VariableAccessExpression expression);
        R visitVariableAssignExpression(VariableAssignExpression expression);
        R visitLogicalExpression(LogicalExpression expression);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class BinaryExpression extends Expression {

        public final Expression left;
        public final Token operator;
        public final Expression right;

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

    public static class LiteralExpression extends Expression {

        public final Token value;

        public LiteralExpression(Token value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }
    }

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

    public static class VariableAccessExpression extends Expression {

        public final Token identifier;

        public VariableAccessExpression(Token identifier) {
            this.identifier = identifier;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableAccessExpression(this);
        }
    }

    public static class VariableAssignExpression extends Expression {

        public final Token identifier;
        public final Expression value;

        public VariableAssignExpression(Token identifier, Expression value) {
            this.identifier = identifier;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableAssignExpression(this);
        }
    }

    public static class LogicalExpression extends Expression {

        public final Expression left;
        public final Token operator;
        public final Expression right;

        public LogicalExpression(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpression(this);
        }
    }

}
