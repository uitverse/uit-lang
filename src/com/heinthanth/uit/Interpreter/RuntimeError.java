package com.heinthanth.uit.Interpreter;

import com.heinthanth.uit.Lexer.Token;

public class RuntimeError extends RuntimeException{
    public final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
