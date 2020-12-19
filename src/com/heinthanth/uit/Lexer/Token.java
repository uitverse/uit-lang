package com.heinthanth.uit.Lexer;

public class Token {
    public final TokenType type;
    public final String sourceString;
    public final Object value;
    public final int line;
    public final int index;

    /**
     * token constructor
     *
     * @param type         Type of token
     * @param sourceString source snippet of a token
     * @param value        Java value object of a token (Double, String, Bool)
     * @param line         line number of a token
     * @param index        index of current token in source string.
     */
    public Token(TokenType type, String sourceString, Object value, int line, int index) {
        this.type = type;
        this.sourceString = sourceString;
        this.value = value;
        this.line = line;
        this.index = index;
    }

    /**
     * String representation of token to debug
     *
     * @return string representation
     */
    public String toString() {
        return "[" + type + ":" + sourceString + "|" + value + "(" + line + "," + index + ")]";
    }
}
