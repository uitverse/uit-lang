package com.heinthanth.uit.Runtime;

import java.util.List;
import java.util.Map;

import com.heinthanth.uit.Interpreter.Interpreter;
import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.token_t;
import com.heinthanth.uit.Utils.TypeMapper;

public class UitClass implements UitCallable {
    public final String name;
    private final UitClass parent;
    private final Map<String, Token> accessModifier;
    private final Map<String, Object> properties;
    private final Map<String, UitFunction> methods;

    public UitClass(String name, UitClass parent, Map<String, Object> props, Map<String, UitFunction> methods,
            Map<String, Token> accessModifier) {
        this.name = name;
        this.parent = parent;
        this.properties = props;
        this.methods = methods;
        this.accessModifier = accessModifier;
    }

    public Object findProp(Token member, boolean fromThis, boolean fromChild) {
        if (properties.containsKey(member.lexeme)) {
            Token am = accessModifier.get(member.lexeme);
            if ((fromChild && (am.type == token_t.PUBLIC || am.type == token_t.PROTECTED))
                    || (!fromChild && (fromThis || am.type == token_t.PUBLIC))) {
                return properties.get(member.lexeme);
            } else {
                throw new RuntimeError(am, "Cannot access '" + am.lexeme + "' member outside of '" + name + "' class.");
            }
        }
        if (parent != null) {
            return parent.findProp(member, fromThis, true);
        }
        return null;
    }

    public UitFunction findMethod(Token member, boolean fromThis, boolean fromChild) {
        if (methods.containsKey(member.lexeme)) {
            Token am = accessModifier.get(member.lexeme);
            if ((fromChild && (am.type == token_t.PUBLIC || am.type == token_t.PROTECTED))
                    || (!fromChild && (fromThis || am.type == token_t.PUBLIC))) {
                return methods.get(member.lexeme);
            } else {
                throw new RuntimeError(am, "Cannot access '" + am.lexeme + "' member outside of '" + name + "' class.");
            }
        }
        if (parent != null) {
            return parent.findMethod(member, fromThis, true);
        }
        return null;
    }

    public void setProp(Token member, Object value, boolean fromThis, boolean fromChild) {
        if (properties.containsKey(member.lexeme)) {
            Token am = accessModifier.get(member.lexeme);
            if ((fromChild && (am.type == token_t.PUBLIC || am.type == token_t.PROTECTED))
                    || (!fromChild && (fromThis || am.type == token_t.PUBLIC))) {
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
                throw new RuntimeError(am, "Cannot access '" + am.lexeme + "' member outside of '" + name + "' class.");
            }
        } else if (methods.containsKey(member.lexeme)) {
            throw new RuntimeError(member, "Cannot re-assign method '" + member.lexeme + "'.");
        } else {
            if (parent != null) {
                parent.setProp(member, value, fromThis, true);
            } else {
                throw new RuntimeError(member, "No member named '" + member.lexeme + "' in '" + name + "' class.");
            }
        }
    }

    @Override
    public Object invoke(Interpreter interpreter, List<Object> arguments) {
        UitInstance instance = new UitInstance(this);
        UitFunction initializer = findMethod(new Token(token_t.IDENTIFIER, "__construct", -1, -1), true, false);
        if (initializer != null && initializer.declaration.type.type != token_t.FRT_VOID)
            throw new RuntimeError(initializer.declaration.type, "Object constructor must be void method.");
        if (initializer != null) {
            initializer.bind(instance).invoke(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int argsCount() {
        UitFunction initializer = findMethod(new Token(token_t.IDENTIFIER, "__construct", -1, -1), true, false);
        if (initializer == null)
            return 0;
        return initializer.argsCount();
    }

    @Override
    public String toString() {
        return name + "::class";
    }
}
