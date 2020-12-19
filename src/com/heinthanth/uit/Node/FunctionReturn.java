package com.heinthanth.uit.Node;

public class FunctionReturn extends RuntimeException {
    final Object value;

    public FunctionReturn(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
