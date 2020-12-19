package com.heinthanth.uit.Node;

import com.heinthanth.uit.Interpreter.Environment;
import com.heinthanth.uit.Interpreter.Interpreter;

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

        interpreter.executeBlock(declaration.body, environment);
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
