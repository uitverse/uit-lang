package com.heinthanth.uit.Runtime;

import com.heinthanth.uit.Lexer.Token;

public class UitInstance {
    public UitClass klass;

    public UitInstance(UitClass klass) {
        this.klass = klass;
    }

    public Object get(Token name, boolean fromThis) {
        Object prop = klass.findProp(name, fromThis, false);
        if (prop != null)
            return prop;
        UitFunction method = klass.findMethod(name, fromThis, false);
        if (method != null)
            return method.bind(this);
        throw new RuntimeError(name, "No member named '" + name.lexeme + "' in '" + klass.name + "' class.");
    }

    public void set(Token name, Object value, boolean fromThis) {
        klass.setProp(name, value, fromThis, false);
    }

    @Override
    public String toString() {
        return klass.name + "::object";
    }
}
