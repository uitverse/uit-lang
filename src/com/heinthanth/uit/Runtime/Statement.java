package com.heinthanth.uit.Runtime;

import com.heinthanth.uit.Lexer.Token;

public abstract class Statement {

    public interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);
        R visitOutputStatement(OutputStatement statement);
        R visitVariableDeclarationStatement(VariableDeclarationStatement statement);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class ExpressionStatement extends Statement {

        public final Expression expression;

        public ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }

    public static class OutputStatement extends Statement {

        public final Expression expression;

        public OutputStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitOutputStatement(this);
        }
    }

    public static class VariableDeclarationStatement extends Statement {

        public final Token type;
        public final Token identifier;
        public final Expression initializer;

        public VariableDeclarationStatement(Token type, Token identifier, Expression initializer) {
            this.type = type;
            this.identifier = identifier;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableDeclarationStatement(this);
        }
    }

}
