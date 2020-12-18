package com.heinthanth.uit.Lexer;

public enum TokenType {
    // arithmetic operators
    PLUS, MINUS, STAR, SLASH,
    // logical operators
    NOT, EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,
    // other operators
    ASSIGN, DOT, COMMA, SEMICOLON, EOF,
    // grouping
    LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, RIGHT_CURLY,
    // keywords
    AND, OR, TRUE, FALSE, IF, ELSE, FOR, ENDFOR, WHILE, ENDWHILE, FUNC, STOP, OUTPUT, RETURN,
    NUM, STRING, BOOLEAN, SET,
    // literal
    NUMBER_LITERAL, STRING_LITERAL, IDENTIFIER
}
