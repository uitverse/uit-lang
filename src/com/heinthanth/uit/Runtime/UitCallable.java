package com.heinthanth.uit.Runtime;

import java.util.List;

import com.heinthanth.uit.Interpreter.Interpreter;

public interface UitCallable {
    // callable တစ်ခုကို invoke လုပ်ဖို့
    public Object invoke(Interpreter interpreter, List<Object> arguments);

    // callable အတွက် argument count ကြည့်ဖို့
    int argsCount();
}
