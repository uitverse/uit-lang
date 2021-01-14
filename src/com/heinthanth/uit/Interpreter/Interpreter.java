package com.heinthanth.uit.Interpreter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.token_t;
import com.heinthanth.uit.Runtime.Expression;
import com.heinthanth.uit.Runtime.Statement;
import com.heinthanth.uit.Runtime.UitCallable;
import com.heinthanth.uit.Runtime.UitFunction;
import com.heinthanth.uit.Runtime.RuntimeError;
import com.heinthanth.uit.Runtime.Expression.BinaryExpression;
import com.heinthanth.uit.Runtime.Expression.CallExpression;
import com.heinthanth.uit.Runtime.Expression.DecrementExpression;
import com.heinthanth.uit.Runtime.Expression.GroupingExpression;
import com.heinthanth.uit.Runtime.Expression.IncrementExpression;
import com.heinthanth.uit.Runtime.Expression.LiteralExpression;
import com.heinthanth.uit.Runtime.Expression.LogicalExpression;
import com.heinthanth.uit.Runtime.Expression.UnaryExpression;
import com.heinthanth.uit.Runtime.Expression.VariableAccessExpression;
import com.heinthanth.uit.Runtime.Expression.VariableAssignExpression;
import com.heinthanth.uit.Runtime.Statement.BlockStatement;
import com.heinthanth.uit.Runtime.Statement.BreakStatement;
import com.heinthanth.uit.Runtime.Statement.ContinueStatement;
import com.heinthanth.uit.Runtime.Statement.ExpressionStatement;
import com.heinthanth.uit.Runtime.Statement.FunctionStatement;
import com.heinthanth.uit.Runtime.Statement.IfStatement;
import com.heinthanth.uit.Runtime.Statement.OutputStatement;
import com.heinthanth.uit.Runtime.Statement.ReturnStatement;
import com.heinthanth.uit.Runtime.Statement.VariableDeclarationStatement;
import com.heinthanth.uit.Runtime.Statement.WhileStatement;
import com.heinthanth.uit.Utils.ErrorHandler;

class BreakSignal extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 8307277094185297477L;
};

class ContinueSignal extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
}

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    // global variable တွေ သိမ်းဖို့
    public final Environment globals = new Environment();
    private Environment environment = globals;

    private final Map<Expression, Integer> locals = new HashMap<>();

    // builtin function တွေကို define ဖို့ constructor
    public Interpreter() {

        // native time function -> return Unix Epoch
        globals._define("time", new UitCallable() {
            @Override
            public int argsCount() {
                return 0;
            }

            @Override
            public Object invoke(Interpreter interpreter, List<Object> arguments) {
                return (double) Instant.now().getEpochSecond();
            }

            @Override
            public String toString() {
                return "[ builtin fn - time ]";
            }
        });
        ;

        // string
        globals._define("toString", new UitCallable() {
            @Override
            public int argsCount() {
                return 1;
            }

            @Override
            public Object invoke(Interpreter interpreter, List<Object> arguments) {
                return stringify(arguments.get(0));
            }

            @Override
            public String toString() {
                return "[ builtin fn - toString ]";
            }
        });

        // string to number
        globals._define("String2Num", new UitCallable() {
            @Override
            public int argsCount() {
                return 1;
            }

            @Override
            public Object invoke(Interpreter interpreter, List<Object> arguments) {
                try {
                    return Double.valueOf((String) arguments.get(0));
                } catch (Exception e) {
                    return 0.0;
                }
            }

            @Override
            public String toString() {
                return "[ builtin fn - String2Num ]";
            }
        });

        globals._define("exit", new UitCallable() {
            @Override
            public int argsCount() {
                return 1;
            }

            @Override
            public Object invoke(Interpreter interpreter, List<Object> arguments) {
                try {
                    int exitCode = Integer.valueOf((String) arguments.get(0));
                    System.exit(exitCode);
                } catch (Exception e) {
                    System.exit(0);
                }
                return null;
            }

            @Override
            public String toString() {
                return "[ builtin fn - exit ]";
            }
        });
    }

    /**
     * parser ကရလာတဲ့ expression ကို evaluate လုပ်မယ်။
     *
     * @param expression
     * @param errorHandler
     * @param fromREPL
     */
    public void interpret(List<Statement> statements, ErrorHandler errorHandler, boolean fromREPL) {
        try {
            if (fromREPL && statements.size() == 1 && statements.get(0) instanceof ExpressionStatement) {
                // REPL မှာ expression statement run ခဲ့ရင် auto output ထုတ်ပေးမယ်။
                ExpressionStatement statement = (ExpressionStatement) statements.get(0);
                Object value = evaluate(statement.expression);
                if (value != null)
                    System.out.println(stringify(value));
                return;
            } else {
                for (Statement statement : statements) {
                    execute(statement);
                }
            }
        } catch (RuntimeError error) {
            errorHandler.reportRuntimeError(error.getMessage(), error.token.line, error.token.col);
        }
    }

    /**
     * function call တွေကို interpret မယ်။
     */
    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {
        UitFunction function = new UitFunction(statement, environment);
        environment.define(statement.identifier, function);
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        Object value = null;
        if (statement.value != null)
            value = evaluate(statement.value);
        throw new UitFunction.ReturnSignal(value);
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
     * if statement ကို interpret မယ်။
     *
     * @param statement
     * @return
     */
    @Override
    public Void visitIfStatement(IfStatement statement) {
        for (Map.Entry<Expression, Statement> stmt : statement.branches.entrySet()) {
            if (isTrue(evaluate(stmt.getKey()))) {
                execute(stmt.getValue());
                return null;
            }
        }
        if (statement.elseBranch != null) {
            execute(statement.elseBranch);
        }
        return null;
    }

    /**
     * while statement ကို interpret မယ်။
     *
     * @param statement
     * @return
     */
    @Override
    public Void visitWhileStatement(WhileStatement statement) {
        while (isTrue(evaluate(statement.condition))) {
            try {
                execute(statement.instructions);
            } catch (BreakSignal sig) {
                break;
            } catch (ContinueSignal sig) {
                continue;
            }
        }
        return null;
    }

    // loop break မယ်။
    @Override
    public Void visitBreakStatement(BreakStatement statement) {
        throw new BreakSignal();
    }

    // continue break မယ်။
    @Override
    public Void visitContinueStatement(ContinueStatement statement) {
        throw new ContinueSignal();
    }

    @Override
    public Object visitLogicalExpression(LogicalExpression expression) {
        Object left = evaluate(expression.left);
        if (expression.operator.type == token_t.OR) {
            if (isTrue(left))
                return left;
        } else {
            if (!isTrue(left))
                return left;
        }
        return evaluate(expression.right);
    }

    /**
     * block statement တွေကို interpret လုပ်မယ်။
     */
    @Override
    public Void visitBlockStatement(BlockStatement statement) {
        executeBlock(statement.statements, new Environment(this.environment));
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
     * function call တွေကို interpret မယ်။
     */
    @Override
    public Object visitCallExpression(CallExpression expression) {
        Object callee = evaluate(expression.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expression argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof UitCallable)) {
            throw new RuntimeError(expression.paren, "Cannot invoke non-functions.");
        }

        UitCallable function = (UitCallable) callee;
        if (arguments.size() != function.argsCount()) {
            throw new RuntimeError(expression.paren,
                    "Expected " + function.argsCount() + " arguments but got " + arguments.size() + ".");
        }

        return function.invoke(this, arguments);
    }

    /**
     * variable ကို value အသစ်ထည့်မယ်။
     */
    @Override
    public Object visitVariableAssignExpression(VariableAssignExpression expression) {
        Object value = evaluate(expression.value);

        Integer distance = locals.get(expression);
        if (distance != null) {
            environment.assignAt(distance, expression.identifier, value);
        } else {
            globals.assign(expression.identifier, value);
        }

        return value;
    }

    /**
     * prefix / postfix increment လုပ်မယ်။
     */
    @Override
    public Object visitIncrementExpression(IncrementExpression expression) {
        if (expression.identifier instanceof VariableAccessExpression) {
            VariableAccessExpression variable = (VariableAccessExpression) expression.identifier;
            Object previous = evaluate(variable);
            if (previous instanceof Double) {
                Object current = (double) previous + 1;

                Integer distance = locals.get(expression);
                if (distance != null) {
                    environment.assignAt(distance, variable.identifier, current);
                } else {
                    globals.assign(variable.identifier, current);
                }

                if ("prefix".equals(expression.mode)) {
                    return current;
                } else {
                    return previous;
                }
            } else {
                throw new RuntimeError(expression.operator, "Cannot increase non-number.");
            }
        } else {
            throw new RuntimeError(expression.operator, "Cannot increase non-variable.");
        }
    }

    /**
     * prefix / postfix decrement လုပ်မယ်။
     */
    @Override
    public Object visitDecrementExpression(DecrementExpression expression) {
        if (expression.identifier instanceof VariableAccessExpression) {
            VariableAccessExpression variable = (VariableAccessExpression) expression.identifier;
            Object previous = evaluate(variable);
            if (previous instanceof Double) {
                Object current = (double) previous - 1;

                Integer distance = locals.get(expression);
                if (distance != null) {
                    environment.assignAt(distance, variable.identifier, current);
                } else {
                    globals.assign(variable.identifier, current);
                }

                if ("prefix".equals(expression.mode)) {
                    return current;
                } else {
                    return previous;
                }
            } else {
                throw new RuntimeError(expression.operator, "Cannot decrease non-number.");
            }
        } else {
            throw new RuntimeError(expression.operator, "Cannot decrease non-variable.");
        }
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
        return lookUpVariable(expression.identifier, expression);
    }

    private Object lookUpVariable(Token name, Expression expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
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

    public void resolve(Expression expr, int depth) {
        locals.put(expr, depth);
    }

    /**
     * block statement တွေကို scope အသစ်နဲ့ interpret လုပ်ဖို့။
     *
     * @param statements
     * @param environment
     */
    public void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
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
            double value = (double) object;
            if ((value == Math.floor(value)) && !Double.isInfinite(value)) {
                return String.valueOf((long) value);
            } else {
                String text = object.toString();
                if (text.endsWith(".0")) {
                    text = text.substring(0, text.length() - 2);
                }
                return text;
            }
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
