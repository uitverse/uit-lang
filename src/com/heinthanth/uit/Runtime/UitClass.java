package com.heinthanth.uit.Runtime;

import java.util.List;
import java.util.Map;

import com.heinthanth.uit.Interpreter.Interpreter;
import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.token_t;
import com.heinthanth.uit.Utils.TypeMapper;

public class UitClass implements UitCallable {
    final String name;

    private final Map<String, Token> accessModifier;
    private final Map<String, Object> properties;
    private final Map<String, UitFunction> methods;

    public UitClass(String name, Map<String, Object> props, Map<String, UitFunction> methods,
            Map<String, Token> accessModifier) {
        this.name = name;
        this.properties = props;
        this.methods = methods;
        this.accessModifier = accessModifier;
    }

    public Object findProp(Token member, boolean fromThis) {
        if (properties.containsKey(member.lexeme)) {
            Token am = accessModifier.get(member.lexeme);
            if (fromThis || am.type == token_t.PUBLIC) {
                return properties.get(member.lexeme);
            } else {
                throw new RuntimeError(am, "Cannot access '" + am.lexeme + "' member outside of class.");
            }
        }
        return null;

    }

    public UitFunction findMethod(Token member, boolean fromThis) {
        if (methods.containsKey(member.lexeme)) {
            Token am = accessModifier.get(member.lexeme);
            if (fromThis || am.type == token_t.PUBLIC) {
                return methods.get(member.lexeme);
            } else {
                throw new RuntimeError(am, "Cannot access '" + am.lexeme + "' member outside of class.");
            }
        }
        return null;
    }

    public void setProp(Token member, Object value, boolean fromThis) {
        if (properties.containsKey(member.lexeme)) {
            Token am = accessModifier.get(member.lexeme);
            if (fromThis || am.type == token_t.PUBLIC) {
                Object old = properties.get(member.lexeme);
                if (value.getClass() == old.getClass()) {
                    properties.put(member.lexeme, value);
                } else {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Cannot assign ");
                    msg.append(TypeMapper.JavaT2String.get(value.getClass()));
                    msg.append(" to ");
                    msg.append(TypeMapper.JavaT2String.get(old.getClass()));
                    msg.append(" variable '");
                    msg.append(member.lexeme);
                    msg.append("'.");
                    throw new RuntimeError(member, msg.toString());
                }
            } else {
                throw new RuntimeError(am, "Cannot access '" + am.lexeme + "' member outside of class.");
            }
        } else if (methods.containsKey(member.lexeme)) {
            throw new RuntimeError(member, "Cannot re-assign method '" + member.lexeme + "'.");
        } else {
            throw new RuntimeError(member, "No member named '" + member.lexeme + "' in '" + name + "' class.");
        }
    }

    @Override
    public Object invoke(Interpreter interpreter, List<Object> arguments) {
        UitInstance instance = new UitInstance(this);
        UitFunction initializer = findMethod(new Token(token_t.IDENTIFIER, "__construct", -1, -1), true);
        if (initializer.declaration.type.type != token_t.FRT_VOID)
            throw new RuntimeError(initializer.declaration.type, "Object constructor must be void method.");
        if (initializer != null) {
            initializer.bind(instance).invoke(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int argsCount() {
        UitFunction initializer = findMethod(new Token(token_t.IDENTIFIER, "__construct", -1, -1), true);
        if (initializer == null)
            return 0;
        return initializer.argsCount();
    }

    @Override
    public String toString() {
        return name + "::class";
    }
}
