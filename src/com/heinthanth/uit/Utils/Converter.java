package com.heinthanth.uit.Utils;

import com.heinthanth.uit.Lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class Converter {

    /**
     * UIT Type mapper to Java Type
     */
    public static final Map<TokenType, Class<?>> Uit2Java = new HashMap<>() {{
        put(TokenType.NUM, Double.class);
        put(TokenType.STRING, String.class);
        put(TokenType.BOOLEAN, Boolean.class);
    }};

    /**
     * UIT Type mapper to Java Type
     */
    public static final Map<Class<?>, TokenType> Java2Uit = new HashMap<>() {{
        put(Double.class, TokenType.NUM);
        put(String.class, TokenType.STRING);
        put(Boolean.class, TokenType.BOOLEAN);
    }};
}
