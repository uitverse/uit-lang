package com.heinthanth.uit.Lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    // source code to tokenize
    private final String source;

    // tokens တွေကို collect မယ့် list ( collector )
    private final List<Token> tokens = new ArrayList<>();

    /**
     * သူက Lexer အတွက် constructor.
     *
     * @param source       tokenize လုပ်ချင်တဲ့ source code
     * @param ErrorHandler ဒါက Lexer ကနေ throw မယ့် error တွေအတွက် handler
     */
    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        return tokens;
    }
}
