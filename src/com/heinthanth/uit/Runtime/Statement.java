package com.heinthanth.uit.Runtime;

import java.util.List;
import java.util.Map;
import com.heinthanth.uit.Lexer.Token;

public abstract class Statement {

    public interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);
        R visitOutputStatement(OutputStatement statement);
        R visitVariableDeclarationStatement(VariableDeclarationStatement statement);
        R visitBlockStatement(BlockStatement statement);
        R visitIfStatement(IfStatement statement);
        R visitWhileStatement(WhileStatement statement);
        R visitBreakStatement(BreakStatement statement);
        R visitContinueStatement(ContinueStatement statement);
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

    public static class BlockStatement extends Statement {

        public final List<Statement> statements;

        public BlockStatement(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }
    }

    public static class IfStatement extends Statement {

        public final Map<Expression,Statement> branches;
        public final Statement elseBranch;

        public IfStatement(Map<Expression,Statement> branches, Statement elseBranch) {
            this.branches = branches;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }
    }

    public static class WhileStatement extends Statement {

        public final Expression condition;
        public final Statement instructions;

        public WhileStatement(Expression condition, Statement instructions) {
            this.condition = condition;
            this.instructions = instructions;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }
    }

    public static class BreakStatement extends Statement {


        public BreakStatement() {
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStatement(this);
        }
    }

    public static class ContinueStatement extends Statement {


        public ContinueStatement() {
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStatement(this);
        }
    }

}
