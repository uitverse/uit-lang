package com.heinthanth.uit.Runtime;

import com.heinthanth.uit.Lexer.Token;

public class RuntimeError extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 3922933078957786955L;

    // error ဖြစ်စေတဲ့ token
    public final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
