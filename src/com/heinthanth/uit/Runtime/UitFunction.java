package com.heinthanth.uit.Runtime;

import java.util.List;

import com.heinthanth.uit.Interpreter.Environment;
import com.heinthanth.uit.Interpreter.Interpreter;
import com.heinthanth.uit.Runtime.Statement.FunctionStatement;

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
    private final FunctionStatement declaration;

    public UitFunction(FunctionStatement declaration) {
        this.declaration = declaration;
    }

    @Override
    public Object invoke(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.define(declaration.parameters.get(i).get(0), declaration.parameters.get(i).get(1),
                    arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.instructions, environment);
        } catch (ReturnSignal sig) {
            return sig.value;
        }
        return null;
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
