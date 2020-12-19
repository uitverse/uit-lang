package com.heinthanth.uit.Node;

import com.heinthanth.uit.Interpreter.Interpreter;

import java.util.List;

public interface UitCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}