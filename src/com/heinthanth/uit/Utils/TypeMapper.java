package com.heinthanth.uit.Utils;

import java.util.HashMap;
import java.util.Map;

import com.heinthanth.uit.Lexer.token_t;
import com.heinthanth.uit.Runtime.UitCallable;
import com.heinthanth.uit.Runtime.UitFunction;
import com.heinthanth.uit.Runtime.UitInstance;

public class TypeMapper {
    /**
     * UIT Type mapper to Java Type
     */
    public static final Map<token_t, Class<?>> Uit2Java = new HashMap<>() {
        /**
         *
         */
        private static final long serialVersionUID = 4829726412486612834L;

        {
            put(token_t.VT_NUMBER, Double.class);
            put(token_t.VT_STRING, String.class);
            put(token_t.VT_BOOLEAN, Boolean.class);
            put(token_t.FRT_VOID, Void.class);
            put(token_t.OBJECT, UitInstance.class);
        }
    };

    /**
     * UIT Type mapper to Java Type
     */
    public static final Map<Class<?>, token_t> Java2Uit = new HashMap<>() {
        /**
         *
         */
        private static final long serialVersionUID = 5746582291469005442L;

        {
            put(Double.class, token_t.VT_NUMBER);
            put(String.class, token_t.VT_STRING);
            put(Boolean.class, token_t.VT_BOOLEAN);
            put(Void.class, token_t.FRT_VOID);
            put(UitInstance.class, token_t.OBJECT);
        }
    };

    public static final Map<token_t, String> UitT2String = new HashMap<>() {
        /**
         *
         */
        private static final long serialVersionUID = 430777454135337239L;

        {
            put(token_t.VT_NUMBER, "num");
            put(token_t.VT_STRING, "string");
            put(token_t.VT_BOOLEAN, "boolean");
            put(token_t.FRT_VOID, "void");
            put(token_t.OBJECT, "object");
        }
    };

    public static final Map<Class<?>, String> JavaT2String = new HashMap<>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            put(Double.class, "num");
            put(String.class, "string");
            put(Boolean.class, "boolean");
            put(Void.class, "void");
            put(UitCallable.class, "callable");
            put(UitFunction.class, "function");
            put(UitInstance.class, "object");
        }
    };
}
