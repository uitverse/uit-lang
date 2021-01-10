package com.heinthanth.uit.Lexer;

public enum token_t {
    // arithmetic နဲ့ဆိုင်တဲ့ operator တွေအတွက်
    PLUS, MINUS, STAR, SLASH, CARET, PERCENT,
    // logical operators
    NOT, EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,
    // grouping expression
    LEFT_PAREN, RIGHT_PAREN,
    // literal တွေ
    STRING_LITERAL, NUMBER_LITERAL, BOOLEAN_LITERAL,
    // variable declaration အတွက် type (eg. String demo = "HELLO")
    VT_STRING, VT_NUMBER, VT_BOOLEAN,
    // function return type
    FRT_NUMBER, FRT_STRING, FRT_BOOL, FRT_VOID,
    // program main function start & stop
    START, STOP,
    // and or
    AND, OR,
    // block
    BLOCK, ENDBLOCK,
    // if
    IF, ELSEIF, ELSE, THEN,
    // for
    FOR, ENDFOR,
    // while
    WHILE, ENDWHILE,
    // loop control
    BREAK, CONTINUE,
    // function
    FUNC, ENDFUNC, RETURN,
    // other operators
    SET, INPUT, ASSIGN, DOT, SEMICOLON, EOF
}