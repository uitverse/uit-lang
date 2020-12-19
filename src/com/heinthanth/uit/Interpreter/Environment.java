package com.heinthanth.uit.Interpreter;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    /**
     * List of identifier and value
     */
    private final Map<String, Object> values = new HashMap<>();

    /**
     * nest environment
     */
    final Environment enclosing;

    /**
     * environment constructor
     */
    Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * UIT Type mapper to Java Type
     */
    private static final Map<TokenType, Class<?>> Uit2Java;

    static {
        Uit2Java = new HashMap<>();
        Uit2Java.put(TokenType.NUM, Double.class);
        Uit2Java.put(TokenType.STRING, String.class);
        Uit2Java.put(TokenType.BOOLEAN, Boolean.class);
    }

    /**
     * UIT Type mapper to Java Type
     */
    private static final Map<Class<?>, TokenType> Java2Uit;

    static {
        Java2Uit = new HashMap<>();
        Java2Uit.put(Double.class, TokenType.NUM);
        Java2Uit.put(String.class, TokenType.STRING);
        Java2Uit.put(Boolean.class, TokenType.BOOLEAN);
    }

    /**
     * define variable
     */
    public void define(Token typeDef, Token name, Object value) {
        if (values.containsKey(name.sourceString)) {
            throw new RuntimeError(name,
                    "variable '" + name.sourceString + "' exists.");
        } else {
            if (value.getClass() != Uit2Java.get(typeDef.type)) {
                throw new RuntimeError(typeDef,
                        "Cannot assign " + Java2Uit.get(value.getClass()) + " to " + typeDef.type + " variable '" + name.sourceString + "'.");
            } else {
                values.put(name.sourceString, value);
            }
        }
    }

    /**
     * define variable
     */
    public void defineFuncParam(Token typeDef, Token name, Object value) {
        if (values.containsKey(name.sourceString)) {
            throw new RuntimeError(name,
                    "variable '" + name.sourceString + "' exists.");
        } else {
            if (value.getClass() != Uit2Java.get(typeDef.type)) {
                throw new RuntimeError(typeDef,
                        "Cannot assign " + Java2Uit.get(value.getClass()) + " to " + typeDef.type + " parameter '" + name.sourceString + "'.");
            } else {
                values.put(name.sourceString, value);
            }
        }
    }

    /**
     * define for internal
     */
    public void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * define variable
     */
    void assign(Token name, Object value) {
        if (values.containsKey(name.sourceString)) {
            Object oldvalue = values.get(name.sourceString);
            if (value.getClass() != oldvalue.getClass()) {
                throw new RuntimeError(name,
                        "Cannot assign " + Java2Uit.get(value.getClass()) + " to " + Java2Uit.get(oldvalue.getClass()) + " variable '" + name.sourceString + "'.");
            } else {
                values.put(name.sourceString, value);
                return;
            }
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name,
                "variable '" + name.sourceString + "' not defined.");
    }

    /**
     * Get identifier value
     *
     * @param name identifier
     * @return java value object
     */
    Object get(Token name) {
        if (values.containsKey(name.sourceString)) {
            return values.get(name.sourceString);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
                "variable '" + name.sourceString + "' not defined.");
    }
}
