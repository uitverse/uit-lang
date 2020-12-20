package com.heinthanth.uit.Interpreter;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Utils.Converter;

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
     * define variable
     */
    public void define(Token typeDef, Token name, Object value) {
        if (values.containsKey(name.sourceString)) {
            throw new RuntimeError(name,
                    "variable '" + name.sourceString + "' exists.");
        } else {
            if (value.getClass() != Converter.Uit2Java.get(typeDef.type)) {
                throw new RuntimeError(typeDef,
                        "Cannot assign " + Converter.Java2Uit.get(value.getClass()) + " to " + typeDef.type + " variable '" + name.sourceString + "'.");
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
            if (value.getClass() != Converter.Uit2Java.get(typeDef.type)) {
                throw new RuntimeError(typeDef,
                        "Cannot assign " + Converter.Java2Uit.get(value.getClass()) + " to " + typeDef.type + " parameter '" + name.sourceString + "'.");
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
                        "Cannot assign " + Converter.Java2Uit.get(value.getClass()) + " to " + Converter.Java2Uit.get(oldvalue.getClass()) + " variable '" + name.sourceString + "'.");
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

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        if (ancestor(distance).values.containsKey(name.sourceString)) {
            Object oldvalue = ancestor(distance).values.get(name.sourceString);
            if (value.getClass() != oldvalue.getClass()) {
                throw new RuntimeError(name,
                        "Cannot assign " + Converter.Java2Uit.get(value.getClass()) + " to " + Converter.Java2Uit.get(oldvalue.getClass()) + " variable '" + name.sourceString + "'.");
            } else {
                ancestor(distance).values.put(name.sourceString, value);
                return;
            }
        }
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }
}
