package com.heinthanth.uit.Utils;

import java.util.HashMap;
import java.util.Map;

import com.heinthanth.uit.Lexer.token_t;
import com.heinthanth.uit.Runtime.UitCallable;
import com.heinthanth.uit.Runtime.UitFunction;

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
        }
    };

    public static final Map<token_t, String> UitT2String = new HashMap<>() {
        /**
         *
         */
        private static final long serialVersionUID = 430777454135337239L;

        {
            put(token_t.VT_NUMBER, "Num");
            put(token_t.VT_STRING, "String");
            put(token_t.VT_BOOLEAN, "Boolean");
            put(token_t.FRT_VOID, "void");
        }
    };

    public static final Map<Class<?>, String> JavaT2String = new HashMap<>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            put(Double.class, "Num");
            put(String.class, "String");
            put(Boolean.class, "Boolean");
            put(Void.class, "void");
            put(UitCallable.class, "callable");
            put(UitFunction.class, "function");
        }
    };
}
