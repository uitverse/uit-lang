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
     * nested loop တွေအတွက် parent variable
     */
    private final Environment parent;

    /**
     * assign parent environment
     */
    Environment() {
        parent = null;
    }

    /**
     * assign parent environemtn
     *
     * @param parent
     */
    public Environment(Environment parent) {
        this.parent = parent;
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

    public void define(Token identifier, Object value) {
        if (values.containsKey(identifier.lexeme)) {
            throw new RuntimeError(identifier, "variable '" + identifier.lexeme + "' exists.");
        } else {
            values.put(identifier.lexeme, value);
        }
    }

    /**
     * internal interpreter ကနေ built in function တွေ define ဖို့
     */
    public void _define(String identifier, Object value) {
        values.put(identifier, value);
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
        } else if (parent != null) {
            parent.assign(identifier, value);
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
        if (parent != null)
            return parent.get(identifier);
        throw new RuntimeError(identifier, "variable '" + identifier.lexeme + "'.");
    }
}
