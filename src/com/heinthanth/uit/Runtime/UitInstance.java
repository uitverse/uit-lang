package com.heinthanth.uit.Runtime;

import com.heinthanth.uit.Lexer.Token;

public class UitInstance {
    private UitClass klass;

    public UitInstance(UitClass klass) {
        this.klass = klass;
    }

    public Object get(Token name, boolean fromThis) {
        Object prop = klass.findProp(name, fromThis);
        if (prop != null)
            return prop;
        UitFunction method = klass.findMethod(name, fromThis);
        if (method != null)
            return method.bind(this);
        throw new RuntimeError(name, "No member named '" + name.lexeme + "' in '" + klass.name + "' class.");
    }

    public void set(Token name, Object value, boolean fromThis) {
        klass.setProp(name, value, fromThis);
    }

    @Override
    public String toString() {
        return klass.name + "::object";
    }
}
