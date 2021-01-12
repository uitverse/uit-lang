package com.heinthanth.uit.Runtime;


public abstract class Statement {

    public interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);
        R visitOutputStatement(OutputStatement statement);
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

}
