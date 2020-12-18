package com.heinthanth.uit.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heinthanth.uit.Uit;

import static com.heinthanth.uit.Lexer.TokenType.*;

public class Lexer {
    /**
     * current source code to tokenize
     */
    private final String source;

    /**
     * list of token ( collector ) from lexer
     */
    private final List<Token> tokens = new ArrayList<>();

    /**
     * Map of reserved keywords
     */
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("for", FOR);
        keywords.put("endfor", ENDFOR);
        keywords.put("while", WHILE);
        keywords.put("endwhile", ENDWHILE);
        keywords.put("func", FUNC);
        keywords.put("stop", STOP);
        keywords.put("output", OUTPUT);
        keywords.put("return", RETURN);
        keywords.put("Num", NUM);
        keywords.put("String", STRING);
        keywords.put("Boolean", BOOLEAN);
        keywords.put("set", SET);
    }

    /**
     * current lexer index
     */
    private int start = 0;
    private int current = 0;
    private int line = 0;

    /**
     * Lexer constructor
     *
     * @param source Source code to tokenize
     */
    public Lexer(String source) {
        this.source = source;
    }

    /**
     * convert ( tokenize ) source string to list of tokens
     *
     * @return List of tokens
     */
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line, current));
        return tokens;
    }

    /**
     * Scan and check character one by one
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '+':
                addToken(PLUS);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '/':
                if (match('/')) {
                    while (getCurrentCharacter() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case '!':
                addToken(NOT);
                break;
            case '=':
                addToken(match('=') ? EQUAL : ASSIGN);
                break;
            case '<':
                TokenType type = LESS;
                if (match('>')) {
                    type = NOT_EQUAL;
                } else if (match('=')) {
                    type = LESS_EQUAL;
                }
                addToken(type);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"':
                makeString();
                break;
            default:
                if (isDigit(c)) {
                    makeNumber();
                } else if (isAlpha(c)) {
                    makeIdentifier();
                } else {
                    Uit.error(line, current, "Unexpect character");
                }
        }
    }

    /**
     * check if current character is something
     *
     * @param expected expected character
     * @return true if match given with expected
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    /**
     * Get current character from source string
     *
     * @return current character
     */
    private char getCurrentCharacter() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * peek next character without increasing Index.
     *
     * @return next character
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Check if given character is A-Za-z or _
     *
     * @param c character to test
     * @return true if character is in [a-zA-Z_]
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    /**
     * Check if given character is Alphabet or Number
     *
     * @param c character to test
     * @return true if character is alphabet or number
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Check if given character is number
     *
     * @param c character to test
     * @return true if character is number
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * check if we're at the end of source
     *
     * @return true if we are at last character
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Increase index to next and
     *
     * @return previous character ( aka current character - in the view of next )
     */
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    /**
     * add to token
     *
     * @param type Token type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Add token of value
     *
     * @param type  Token type
     * @param value value of token
     */
    private void addToken(TokenType type, Object value) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, value, line, current - 1));
    }

    /**
     * build java String from source string
     */
    private void makeString() {
        boolean isEscaped = false;
        StringBuilder stringString = new StringBuilder();

        Map<Character, Character> escapedValue = new HashMap<>();
        escapedValue.put('n', '\n');
        escapedValue.put('t', '\t');

        while ((getCurrentCharacter() != '"' || isEscaped) && !isAtEnd()) {
            if (getCurrentCharacter() == '\n') line++;
            if (isEscaped) {
                char c = getCurrentCharacter();
                if (escapedValue.containsKey(c)) {
                    stringString.append(escapedValue.get(c));
                    isEscaped = false;
                } else {
                    stringString.append(c);
                }
            } else {
                if (getCurrentCharacter() == '\\') {
                    isEscaped = true;
                } else {
                    stringString.append(getCurrentCharacter());
                }
            }
            advance();
        }

        if (isAtEnd()) {
            Uit.error(line, current, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        //String value = source.substring(start + 1, current - 1);
        addToken(STRING_LITERAL, stringString.toString());
    }

    /**
     * build java Double from source string
     */
    private void makeNumber() {
        while (isDigit(getCurrentCharacter())) advance();

        // Look for a fractional part.
        if (getCurrentCharacter() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(getCurrentCharacter())) advance();
        }

        addToken(NUMBER_LITERAL,
                Double.parseDouble(source.substring(start, current)));
    }

    /**
     * detect identifier
     */
    private void makeIdentifier() {
        while (isAlphaNumeric(getCurrentCharacter())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }
}
