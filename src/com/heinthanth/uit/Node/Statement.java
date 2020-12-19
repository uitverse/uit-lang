package com.heinthanth.uit.Node;

import com.heinthanth.uit.Lexer.Token;

import java.util.List;
import java.util.Map;

public abstract class Statement {
    public interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);

        R visitOutputStatement(OutputStatement statement);

        R visitVariableDeclareStatement(VariableDeclareStatement statement);

        R visitBlockStatement(BlockStatement statement);

        R visitIfStatement(IfStatement statement);

        R visitWhileStatement(WhileStatement statement);

        R visitFunctionStatement(FunctionStatement statement);

        R visitReturnStatement(ReturnStatement statement);

        R visitBreakStatement(BreakStatement statement);
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

    /**
     * block statement like for, function, while
     */
    public static class BlockStatement extends Statement {
        public BlockStatement(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }

        public final List<Statement> statements;
    }

    /**
     * If statement
     */
    public static class IfStatement extends Statement {
        public IfStatement(Map<Expression, Statement> branches, Statement elseBranch) {
            this.branches = branches;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }

        public final Map<Expression, Statement> branches;
        public final Statement elseBranch;
    }

    /**
     * While statement
     */
    public static class WhileStatement extends Statement {
        public WhileStatement(Expression condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }

        public final Expression condition;
        public final Statement body;
    }

    /**
     * function declaration statement
     */
    public static class FunctionStatement extends Statement {
        public final Token typeDef;
        public final Token name;
        public final List<List<Token>> params;
        public final List<Statement> body;

        public FunctionStatement(Token typeDef, Token name, List<List<Token>> params, List<Statement> body) {
            this.typeDef = typeDef;
            this.name = name;
            this.params = params;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStatement(this);
        }
    }

    /**
     * Function return statement
     */
    public static class ReturnStatement extends Statement {
        public final Token keyword;
        public final Expression value;

        public ReturnStatement(Token keyword, Expression value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStatement(this);
        }
    }

    /**
     * Break statement
     */
    public static class BreakStatement extends Statement {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStatement(this);
        }
    }
}
