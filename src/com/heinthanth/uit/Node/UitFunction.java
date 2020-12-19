package com.heinthanth.uit.Node;

import com.heinthanth.uit.Interpreter.Environment;
import com.heinthanth.uit.Interpreter.Interpreter;
import com.heinthanth.uit.Interpreter.RuntimeError;
import com.heinthanth.uit.Utils.Converter;

import java.util.List;

public class UitFunction implements UitCallable {
    private final Statement.FunctionStatement declaration;

    public UitFunction(Statement.FunctionStatement declaration) {
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.defineFuncParam(declaration.params.get(i).get(0), declaration.params.get(i).get(1), arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (FunctionReturn returnValue) {
            if(returnValue.value.getClass() != Converter.Uit2Java.get(declaration.typeDef.type)) {
                throw new RuntimeError(declaration.typeDef, "Cannot return " + Converter.Java2Uit.get(returnValue.value.getClass()) + " from " + declaration.typeDef.type + " function '" + declaration.name.sourceString + "'.");
            }
            return returnValue.value;
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<func " + declaration.name.sourceString + ">";
    }
}
