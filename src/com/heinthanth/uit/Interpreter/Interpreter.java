package com.heinthanth.uit.Interpreter;

import java.util.List;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Runtime.Expression;
import com.heinthanth.uit.Runtime.Statement;
import com.heinthanth.uit.Runtime.RuntimeError;
import com.heinthanth.uit.Runtime.Expression.BinaryExpression;
import com.heinthanth.uit.Runtime.Expression.GroupingExpression;
import com.heinthanth.uit.Runtime.Expression.LiteralExpression;
import com.heinthanth.uit.Runtime.Expression.UnaryExpression;
import com.heinthanth.uit.Runtime.Expression.VariableAccessExpression;
import com.heinthanth.uit.Runtime.Statement.ExpressionStatement;
import com.heinthanth.uit.Runtime.Statement.OutputStatement;
import com.heinthanth.uit.Runtime.Statement.VariableAssignStatement;
import com.heinthanth.uit.Runtime.Statement.VariableDeclarationStatement;
import com.heinthanth.uit.Utils.ErrorHandler;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    // global variable တွေ သိမ်းဖို့
    private Environment environment = new Environment();

    /**
     * parser ကရလာတဲ့ expression ကို evaluate လုပ်မယ်။
     *
     * @param expression
     * @param errorHandler
     */
    public void interpret(List<Statement> statements, ErrorHandler errorHandler) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            errorHandler.reportRuntimeError(error.getMessage(), error.token.line, error.token.col);
        }
    }

    /**
     * variable declare မယ်။
     */
    @Override
    public Void visitVariableDeclarationStatement(VariableDeclarationStatement statement) {
        Object value = null;
        switch (statement.type.type) {
            case VT_STRING:
                value = "";
                break;
            case VT_BOOLEAN:
                value = false;
                break;
            case VT_NUMBER:
                value = 0.0;
                break;
            default:
                break;
        }
        if (statement.initializer != null) {
            value = evaluate(statement.initializer);
        }
        environment.define(statement.type, statement.identifier, value);
        return null;
    }

    /**
     * variable ကို value အသစ်ထည့်မယ်။
     */
    @Override
    public Void visitVariableAssignStatement(VariableAssignStatement statement) {
        Object value = evaluate(statement.value);
        environment.assign(statement.identifier, value);
        return null;
    }

    /**
     * output statement ကို interpret မယ်။
     */
    @Override
    public Void visitOutputStatement(OutputStatement statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    /**
     * expression statement ကို interpret မယ်။
     */
    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        evaluate(statement.expression);
        return null;
    }

    /**
     * literal expression တစ်ခုကနေ value ကိုယူမယ်။
     */
    @Override
    public Object visitLiteralExpression(LiteralExpression expression) {
        return expression.value.getValue();
    }

    /**
     * grouping expression ကို evaluate လုပ်မယ်။
     */
    @Override
    public Object visitGroupingExpression(GroupingExpression expression) {
        return evaluate(expression.expression);
    }

    /**
     * unary operation တွေကို ေဖြရှင်းမယ်။
     */
    @Override
    public Object visitUnaryExpression(UnaryExpression expression) {
        Object right = evaluate(expression.right);
        switch (expression.operator.type) {
            case NOT:
                return !isTrue(right);
            case MINUS:
                checkNumberOperand(expression.operator, right);
                return (double) right;
            default:
                return null;
        }
    }

    /**
     * binary expression တွေကို solve မယ်။
     */
    @Override
    public Object visitBinaryExpression(BinaryExpression expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case PLUS:
                checkNumberOperands(expression.operator, left, right);
                return (double) left + (double) right;
            case MINUS:
                checkNumberOperands(expression.operator, left, right);
                return (double) left - (double) right;
            case STAR:
                checkNumberOperands(expression.operator, left, right);
                return (double) left * (double) right;
            case SLASH:
                checkNumberOperands(expression.operator, left, right);
                checkZeroOprand(expression.operator, right);
                return (double) left / (double) right;
            case PERCENT:
                checkNumberOperands(expression.operator, left, right);
                checkZeroOprand(expression.operator, right);
                return (double) left % (double) right;
            case CARET:
                checkNumberOperands(expression.operator, left, right);
                return Math.pow((double) left, (double) right);
            case DOT:
                return (String) left + (String) right;
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
            default:
                return null;
        }
    }

    /**
     * variable ကနေ တန်ဖိုးကိုယူမယ်။
     */
    @Override
    public Object visitVariableAccessExpression(VariableAccessExpression expression) {
        return environment.get(expression.identifier);
    }

    /**
     * statement တွေကို interpret မယ်။
     *
     * @param statement
     * @return
     */
    private void execute(Statement statement) {
        statement.accept(this);
    }

    /**
     * Expression ကနေ value ရအောင်ပြောင်းမယ်။
     *
     * @param expression
     * @return
     */
    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    /**
     * evaluate လုပ်လို့ ရလာတဲ့ object ကို string ပြောင်းမယ်။
     *
     * @param object
     * @return
     */
    private String stringify(Object object) {
        if (object == null)
            return "null";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    /**
     * value တစ်ခုကို boolean true ဟုတ် မဟုတ် စစ်မယ်။ null ဆို false, bool ဆို သူ့
     * value အတိုင်း ကျန်တာက exists သဘောနဲ့ true
     *
     * @param value
     * @return
     */
    private boolean isTrue(Object value) {
        if (value == null)
            return false;
        if (value instanceof Boolean)
            return (boolean) value;
        return true;
    }

    /**
     * unary operand က number ဖြစ်ကြောင်း စစ်မယ်။
     *
     * @param operator
     * @param operand
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    /**
     * binary operands က number ဖြစ်ကြောင်း စစ်မယ်။
     *
     * @param operator
     * @param left
     * @param right
     */
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double)
            return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    /**
     * အစား, ဘာညာအတွက် zero ဟုတ် မဟုတ် စစ်မယ်။
     *
     * @param operator
     * @param right
     */
    private void checkZeroOprand(Token operator, Object right) {
        if ((double) right != 0)
            return;
        throw new RuntimeError(operator, "Divider must not be zero.");
    }

    /**
     * Object (value) နှစ်ခုတူမတူစစ်မယ်။
     *
     * @param a
     * @param b
     * @return
     */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;

        return a.equals(b);
    }
}
