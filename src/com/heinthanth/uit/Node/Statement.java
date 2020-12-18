package com.heinthanth.uit.Node;

import com.heinthanth.uit.Lexer.Token;

public abstract class Statement {
    public interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);

        R visitOutputStatement(OutputStatement statement);

        R visitVariableDeclareStatement(VariableDeclareStatement statement);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    /**
     * Expression statement
     */
    public static class ExpressionStatement extends Statement {
        public ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }

        public final Expression expression;
    }

    /**
     * Output statement
     */
    public static class OutputStatement extends Statement {
        public OutputStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitOutputStatement(this);
        }

        public final Expression expression;
    }

    /**
     * variable declaration statement
     */
    public static class VariableDeclareStatement extends Statement {
        public VariableDeclareStatement(Token typeDef, Token name, Expression initializer) {
            this.typeDef = typeDef;
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableDeclareStatement(this);
        }

        public final Token typeDef;
        public final Token name;
        public final Expression initializer;
    }
}
