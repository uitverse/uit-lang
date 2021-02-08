package com.heinthanth.uit.Lexer;

public enum token_t {
    // arithmetic နဲ့ဆိုင်တဲ့ operator တွေအတွက်
    PLUS, MINUS, STAR, SLASH, CARET, PERCENT, INCREMENT, DECREMENT,
    // logical operators
    NOT, EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,
    // grouping expression
    LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, RIGHT_CURLY,
    // literal တွေ
    STRING_LITERAL, NUMBER_LITERAL, BOOLEAN_LITERAL, IDENTIFIER,
    // variable declaration အတွက် type (eg. String demo = "HELLO")
    VT_STRING, VT_NUMBER, VT_BOOLEAN, FRT_VOID, OBJECT,
    // program main function start & stop
    START, STOP,
    // and or
    AND, OR,
    // block
    BLOCK, ENDBLOCK,
    // if
    IF, ELSEIF, ELSE, THEN, ENDIF,
    // for
    FOR, ENDFOR,
    // while
    WHILE, ENDWHILE,
    // loop control
    BREAK, CONTINUE,
    // function
    FUNC, ENDFUNC, RETURN,
    // class
    CLASS, ENDCLASS, NEW, DART, PUBLIC, PRIVATE, PROTECTED, EXTENDS, THIS, SUPER,
    // other keywords
    SET, INPUT, OUTPUT,
    // other operator
    ASSIGN, COMMA, DOT, SEMICOLON, EOF,
}