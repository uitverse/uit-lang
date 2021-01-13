package com.heinthanth.uit.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heinthanth.uit.Utils.ErrorHandler;

import static com.heinthanth.uit.Lexer.token_t.*;

public class Lexer {
    // source code to tokenize
    private final String source;

    // tokens တွေကို collect မယ့် list ( collector )
    private final List<Token> tokens = new ArrayList<>();

    // error handling အတွက် object instance
    private final ErrorHandler errorHandler;

    // reserved keywords တွေ
    private static final Map<String, token_t> reserved;

    static {
        reserved = new HashMap<>();
        reserved.put("String", VT_STRING);
        reserved.put("Num", VT_NUMBER);
        reserved.put("Boolean", VT_BOOLEAN);
        reserved.put("void", FRT_VOID);
        reserved.put("start", START);
        reserved.put("stop", STOP);
        reserved.put("and", AND);
        reserved.put("or", OR);
        reserved.put("block", BLOCK);
        reserved.put("endblock", ENDBLOCK);
        reserved.put("if", IF);
        reserved.put("elseif", ELSEIF);
        reserved.put("else", ELSE);
        reserved.put("then", THEN);
        reserved.put("while", WHILE);
        reserved.put("endwhile", ENDWHILE);
        reserved.put("break", BREAK);
        reserved.put("continue", CONTINUE);
        reserved.put("func", FUNC);
        reserved.put("endfunc", ENDFUNC);
        reserved.put("return", RETURN);
        reserved.put("set", SET);
        reserved.put("input", INPUT);
        reserved.put("output", OUTPUT);
        reserved.put("true", BOOLEAN_LITERAL);
        reserved.put("false", BOOLEAN_LITERAL);
    }

    // lexer ရဲ့ position တွေ
    private int start = 0;
    private int current = 0;
    private int line = 0;

    /**
     * သူက Lexer အတွက် constructor.
     *
     * @param source       tokenize လုပ်ချင်တဲ့ source code
     * @param ErrorHandler ဒါက Lexer ကနေ throw မယ့် error တွေအတွက် handler
     */
    public Lexer(String source, ErrorHandler errorHandler) {
        this.source = source;
        this.errorHandler = errorHandler;
    }

    /**
     * string ကို token အဖြစ်ေပြာင်းပေးမယ့် methd
     *
     * @return ဒါက ရလာတဲ့ token list
     */
    public List<Token> tokenize() {
        // EOF အဆုံးမရောက်မချင်း token တွေကို scan သွားမယ်။
        while (!isEOF()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", line, current));
        return tokens;
    }

    // token တွေကို character တွေေပါ်မူတည်ပြီး သက်ဆိုင်ရာ token ထုတ်ပေးမယ်။
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
                    while (getCurrentCharacter() != '\n' && !isEOF())
                        advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case '^':
                addToken(CARET);
                break;
            case '%':
                addToken(PERCENT);
                break;
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_CURLY);
                break;
            case '}':
                addToken(RIGHT_CURLY);
                break;
            case '&':
                if (match('&')) {
                    addToken(AND);
                } else {
                    errorHandler.reportError("Expecting '&&', but not found.", line, current);
                }
                break;
            case '|':
                if (match('|')) {
                    addToken(OR);
                } else {
                    errorHandler.reportError("Expecting '||', but not found.", line, current);
                }
                break;
            case '!':
                addToken(match('=') ? NOT_EQUAL : NOT);
                break;
            case '=':
                addToken(match('=') ? EQUAL : ASSIGN);
                break;
            case '<':
                token_t type = LESS;
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
            case ';':
                addToken(SEMICOLON);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
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
                makeString('"');
                break;
            case '\'':
                makeString('\'');
                break;
            default:
                if (isDigit(c)) {
                    makeNumber();
                } else if (isMmDigit(c)) {
                    makeMmNumber();
                } else if (isAlpha(c)) {
                    makeIdentifier();
                } else {
                    errorHandler.reportError("Unexpected character", line, current);
                }
                break;
        }
    }

    /**
     * လက်ရှိ character က expected character ဟုတ် မဟုတ် စစ်မယ်။ ဟုတ်ခဲ့ရင် current 1
     * တိုးမယ်။
     *
     * @param expected စစ်မယ့် character
     * @return မှန်ရင် true ပြန်မယ်။
     */
    private boolean match(char expected) {
        if (isEOF())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    /**
     * လက်ရှိ character ကို ယူမယ်။
     *
     * @return
     */
    private char getCurrentCharacter() {
        if (isEOF())
            return '\0';
        return source.charAt(current);
    }

    // index တို့ 1 မတိုးဘဲ ကြည့်ကြည့်မယ်။
    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    // EOF ဟုတ် မဟုတ် စစ်မယ့် method
    private boolean isEOF() {
        return current >= source.length();
    }

    // index ကို 1 တိုးမယ် ပြီးရင် index -1 ကို ပြန်မယ်။
    // အဲ့တော့ current character ကို return လုပ်တာနဲ့တူတူပဲ။
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * string token တစ်ခုလုပ်ဖို့။
     *
     * @param type  token type
     * @param value string value
     */
    private void addToken(token_t type, String value) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, value, line, current - 1));
    }

    /**
     * number token တစ်ခုလုပ်ဖို့။
     *
     * @param type  token type
     * @param value double value
     */
    private void addToken(token_t type, double value) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, value, line, current - 1));
    }

    /**
     * boolean token တစ်ခုလုပ်ဖို့။
     *
     * @param type  token type
     * @param value boolean value
     */
    private void addToken(token_t type, boolean value) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, value, line, current - 1));
    }

    // /**
    // * general object token တစ်ခုလုပ်ဖို့။
    // *
    // * @param type token type
    // * @param value Object value
    // */
    // private void addToken(token_t type, Object value) {
    // String lexeme = source.substring(start, current);
    // tokens.add(new Token(type, lexeme, value, line, current - 1));
    // }

    /**
     * value မပါတဲ့ token တစ်ခုလုပ်ဖို့။
     *
     * @param type token type
     */
    private void addToken(token_t type) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, line, current - 1));
    }

    // source string တွေကနေ string token တစ်ခု ဆောက်မယ်။
    private void makeString(char wrappingChar) {
        boolean isEscaped = false;
        StringBuilder stringString = new StringBuilder();

        Map<Character, Character> escapedValue = new HashMap<>();
        escapedValue.put('n', '\n');
        escapedValue.put('t', '\t');
        escapedValue.put('"', '"');
        escapedValue.put('\'', '\'');
        escapedValue.put('\\', '\\');

        while ((getCurrentCharacter() != wrappingChar || isEscaped) && !isEOF()) {
            if (getCurrentCharacter() == '\n')
                line++;
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

        if (isEOF()) {
            errorHandler.reportError("Unterminated string.", line, current);
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        // String value = source.substring(start + 1, current - 1);
        addToken(STRING_LITERAL, stringString.toString());
    }

    // source string တွေကနေ number token တစ်ခု ဆောက်မယ်။
    private void makeNumber() {
        while (isDigit(getCurrentCharacter()))
            advance();

        // Look for a fractional part.
        if (getCurrentCharacter() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(getCurrentCharacter()))
                advance();
        }

        addToken(NUMBER_LITERAL, Double.parseDouble(source.substring(start, current)));
    }

    // source string တွေကနေ number token တစ်ခု ဆောက်မယ်။
    private void makeMmNumber() {
        while (isMmDigit(getCurrentCharacter()))
            advance();
        // Look for a fractional part.
        if (getCurrentCharacter() == '.' && isMmDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isMmDigit(getCurrentCharacter()))
                advance();
        }

        // I Don't know whether efficient or not?
        String number = source.substring(start, current).replace("၀", "0").replace("၁", "1").replace("၂", "2")
                .replace("၃", "3").replace("၄", "4").replace("၅", "5").replace("၆", "6").replace("၇", "7")
                .replace("၈", "8").replace("၉", "9");

        addToken(NUMBER_LITERAL, Double.parseDouble(number));
    }

    // source string ကနေ identifier တစ်ခုဆောက်မယ်။
    private void makeIdentifier() {
        while (isAlphaNumeric(getCurrentCharacter()))
            advance();

        String text = source.substring(start, current);
        token_t type = reserved.get(text);

        if (type == BOOLEAN_LITERAL) {
            addToken(BOOLEAN_LITERAL, "true".equals(text) ? true : false);
        } else {
            if (type == null)
                type = IDENTIFIER;
            addToken(type);
        }
    }

    /**
     * character က alpha ဖြစ်မဖြစ် စစ်မယ်။
     *
     * @param c character to test
     * @return true if character is in [a-zA-Z_]
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    /**
     * character က number သို့ alpha ဖြစ်မဖြစ် စစ်မယ်။
     *
     * @param c character to test
     * @return true if character is alphabet or number
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * character က number ဖြစ်မဖြစ် စစ်မယ်။
     *
     * @param c character to test
     * @return true if character is number
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // same as isDigit but in Myanmar
    private boolean isMmDigit(char c) {
        return "၀၁၂၃၄၅၆၇၈၉".contains(String.valueOf(c));
    }
}
