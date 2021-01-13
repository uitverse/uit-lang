package com.heinthanth.uit.Interpreter;

import java.util.HashMap;
import java.util.Map;

import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Runtime.RuntimeError;
import com.heinthanth.uit.Utils.TypeMapper;

public class Environment {
    /**
     * define variable တွေ အားလုံးကို သိမ်းထားတဲ့နေရာ
     */
    private final Map<String, Object> values = new HashMap<>();

    /**
     * nested loop တွေအတွက် child variable
     */
    private final Environment child;

    /**
     * assign child environment
     */
    Environment() {
        child = null;
    }

    /**
     * assign child environemtn
     *
     * @param child
     */
    Environment(Environment child) {
        this.child = child;
    }

    /**
     * variable အသစ် define လုပ်ဖို့။ type မတူရင် error တက်မယ်။
     *
     * @param type
     * @param identifier
     * @param value
     */
    public void define(Token type, Token identifier, Object value) {
        if (values.containsKey(identifier.lexeme)) {
            throw new RuntimeError(identifier, "variable '" + identifier.lexeme + "' exists.");
        } else {
            if (value.getClass() == TypeMapper.Uit2Java.get(type.type)) {
                values.put(identifier.lexeme, value);
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot assign ");
                msg.append(TypeMapper.JavaT2String.get(value.getClass()));
                msg.append(" to ");
                msg.append(TypeMapper.UitT2String.get(type.type));
                msg.append(" variable '");
                msg.append(identifier.lexeme);
                msg.append("'.");
                throw new RuntimeError(type, msg.toString());
            }
        }
    }

    /**
     * ရှိပြီးသား variable ကို value အသစ်ထည့်မယ်။ variable မရှိရင် error တက်မယ်။
     *
     * @param identifer
     * @param value
     */
    public void assign(Token identifier, Object value) {
        if (values.containsKey(identifier.lexeme)) {
            Object old = values.get(identifier.lexeme);
            if (value.getClass() == old.getClass()) {
                values.put(identifier.lexeme, value);
            } else if (child != null) {
                child.assign(identifier, value);
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot assign ");
                msg.append(TypeMapper.JavaT2String.get(value.getClass()));
                msg.append(" to ");
                msg.append(TypeMapper.JavaT2String.get(old.getClass()));
                msg.append(" variable '");
                msg.append(identifier.lexeme);
                msg.append("'.");
                throw new RuntimeError(identifier, msg.toString());
            }
        } else {
            throw new RuntimeError(identifier, "variable '" + identifier.lexeme + "' does not exists.");
        }
    }

    /**
     * variable တန်ဖိုးယူမယ်။ မရှိရင် error တက်မယ်။
     *
     * @param identifier
     * @return
     */
    public Object get(Token identifier) {
        if (values.containsKey(identifier.lexeme)) {
            return values.get(identifier.lexeme);
        }
        if (child != null)
            return child.get(identifier);
        throw new RuntimeError(identifier, "variable '" + identifier.lexeme + "'.");
    }
}
