package com.heinthanth.uit.Runtime;

import java.util.List;

import com.heinthanth.uit.Interpreter.Environment;
import com.heinthanth.uit.Interpreter.Interpreter;
import com.heinthanth.uit.Runtime.Statement.FunctionStatement;
import com.heinthanth.uit.Utils.TypeMapper;

public class UitFunction implements UitCallable {
    /**
     * single for return statement
     */
    public static class ReturnSignal extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = -1082944320718946285L;
        final Object value;

        public ReturnSignal(Object value) {
            super(null, null, false, false);
            this.value = value;
        }
    }

    /**
     * မူရင်း function declaration ကိုသိမ်းဖို့
     */
    public final FunctionStatement declaration;

    private final boolean isInitializer;

    /**
     * closure environment
     */
    private final Environment closure;

    public UitFunction(FunctionStatement declaration, Environment closure, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.declaration = declaration;
        this.closure = closure;
    }

    public UitFunction bind(UitInstance instance) {
        Environment environment = new Environment(closure);
        environment._define("this", instance);
        return new UitFunction(declaration, environment, isInitializer);
    }

    @Override
    public Object invoke(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.define(declaration.parameters.get(i).get(0), declaration.parameters.get(i).get(1),
                    arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.instructions, environment);
        } catch (ReturnSignal sig) {
            if (isInitializer)
                return closure.getAt(0, "this");
            if (sig.value.getClass() != TypeMapper.Uit2Java.get(declaration.type.type)) {
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot return ");
                msg.append(TypeMapper.JavaT2String.get(sig.value.getClass()));
                msg.append(" from ");
                msg.append(TypeMapper.UitT2String.get(declaration.type.type));
                msg.append(" function '");
                msg.append(declaration.identifier.lexeme);
                msg.append("'.");
                throw new RuntimeError(declaration.type, msg.toString());
            }
            return sig.value;
        }

        if (isInitializer)
            return closure.getAt(0, "this");
        return "";
    }

    @Override
    public int argsCount() {
        return declaration.parameters.size();
    }

    @Override
    public String toString() {
        return "[ function - " + this.declaration.identifier.lexeme + " ]";
    }
}
