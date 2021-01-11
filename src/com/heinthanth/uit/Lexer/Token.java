package com.heinthanth.uit.Lexer;

import static com.heinthanth.uit.Lexer.value_t.*;

public class Token {
    // ဒါက token type. parser မှာ သုံးဖို့
    public final token_t type;
    // lexeme ဆိုတာက source code မှာရေးထားတဲ့ code အပိုင်းအစ
    // ( var num = 1) ဆိုရင် var သည် lemexe, num သည် lexeme, 1 သည် lexeme
    public final String lexeme;

    // value type ထည့်ထားတဲ့ value ရဲ့ type
    public final value_t v_type;
    // value က value ပေါ့။ string value, number value, boolean value စသည်ဖြင့်။
    public final String v_string;
    public final double v_number;
    public final boolean v_boolean;
    public final Object v_object;

    // source code မှာရှိတဲ့ position (line, col)
    public final int line;
    public final int col;

    // string token အတွက် constructor
    Token(token_t type, String lexeme, String value, int line, int col) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.col = col;

        this.v_type = VT_STRING;
        this.v_string = value;

        this.v_number = 0.0;
        this.v_boolean = false;
        this.v_object = null;
    }

    // number token အတွက် constructor
    Token(token_t type, String lexeme, double value, int line, int col) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.col = col;

        this.v_type = VT_NUMBER;
        this.v_number = value;

        this.v_string = "";
        this.v_boolean = false;
        this.v_object = null;
    }

    // boolean token အတွက် constructor
    Token(token_t type, String lexeme, boolean value, int line, int col) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.col = col;

        this.v_type = VT_BOOLEAN;
        this.v_boolean = value;

        this.v_string = "";
        this.v_number = 0.0;
        this.v_object = null;
    }

    // general object token အတွက် constructor
    Token(token_t type, String lexeme, Object value, int line, int col) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.col = col;

        this.v_type = VT_OBJECT;
        this.v_object = value;

        this.v_string = "";
        this.v_number = 0.0;
        this.v_boolean = false;
    }

    // value မရှိတဲ့ token (eg. PLUS, MINUS, etc) အတွက် constructor
    Token(token_t type, String lexeme, int line, int col) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.col = col;

        this.v_type = VT_VOID;

        this.v_string = "";
        this.v_number = 0.0;
        this.v_boolean = false;
        this.v_object = null;
    }

    // ဒါက token ကို string အနေနဲ့ ေဖာ်ပြဖို့အတွက်
    @Override
    public String toString() {
        switch (v_type) {
            case VT_NUMBER:
                return "[" + type + ":" + lexeme + "|" + v_number + "(" + line + "," + col + ")]";
            case VT_STRING:
                return "[" + type + ":" + lexeme + "|" + v_string + "(" + line + "," + col + ")]";
            case VT_BOOLEAN:
                return "[" + type + ":" + lexeme + "|" + v_boolean + "(" + line + "," + col + ")]";
            case VT_OBJECT:
                return "[" + type + ":" + lexeme + "|" + v_object + "(" + line + "," + col + ")]";
            default:
                return "[" + type + ":" + lexeme + "(" + line + "," + col + ")]";
        }
    }
}
