package com.heinthanth.uit.lexer;

public enum Token {
    // arithmetic operators
    PLUS, MINUS, STAR, SLASH,
    // grouping
    LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, RIGHT_CURLY,
    // other operators
    ASSIGN, EQUAL, NOT_EQUAL, LESS_THAN, LESS_OR_EQUAL, GREATER_THAN, GREATER_OR_EQUAL,
    AND, OR, NOT,
    // other symbols
    COMMA, DOT, SEMICOLON,
    // reserved keywords
    IF, ELSE, FOR, ENDFOR, WHILE, ENDWHILE, FUNC, STOP, BLOCK, ENDBLOCK,
    TRUE, FALSE, NULL,
}
