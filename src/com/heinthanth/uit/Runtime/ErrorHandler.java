package com.heinthanth.uit.Runtime;

import com.heinthanth.uit.Lexer.Token;

public class ErrorHandler {
    // handle လုပ်ရမယ့် source code (error snippet တွေပြဖို့)
    private final String source;

    // ဒါက file name။ ဘယ် file ကို error handle လုပ်မလဲေပါ့။
    private final String filename;

    // error ရှိမရှိအတွက် status
    public boolean hadError = false;

    // runtime error ရှိမရှိအတွက် status
    public boolean hadRuntimeError = false;

    /**
     * Error Handler အတွက် constructor
     *
     * @param source   source code
     * @param filename filename
     */
    public ErrorHandler(String source, String filename) {
        this.source = source;
        this.filename = filename;
    }

    /**
     * ဒါက token ကိုသုံးပြီးတော့ error throw ဖို့
     *
     * @param token   error ဖြစ်တဲ့ token
     * @param message error message
     */
    public void reportError(Token token, String message) {
        reportError(message, token.line, token.col);
    }

    /**
     * ဒါက line, col တွေသုံးပြီး error report ဖို့။ user ဆီကို error message
     * တွေပြမယ်။
     *
     * @param message error message
     * @param line    token/literal ရဲ့ line
     * @param col     token/literal ရဲ့ column
     */
    public void reportError(String message, int line, int col) {
        showSource("ERROR", message, line, col);
        hadError = true;
    }

    /**
     * ဒါက line, col တွေသုံးပြီး runtime error report ဖို့။ user ဆီကို error message
     * တွေပြမယ်။
     *
     * @param message error message
     * @param line    token/literal ရဲ့ line
     * @param col     token/literal ရဲ့ column
     */
    public void reportRuntimeError(String message, int line, int col) {
        showSource("RUNTIME_ERROR", message, line, col);
        hadRuntimeError = true;
    }

    /**
     * ဒါက user ကို error တက်တဲ့ code အပိုင်းအစလေးပြမလို့
     *
     * @param message error message
     * @param line    error တက်တဲ့ line
     * @param col     error တက်တဲ့ column
     */
    private void showSource(String level, String message, int line, int col) {
        // source line ကို array ဖြစ်အောင် ခွဲမယ်။
        String[] sourceLines = source.split("\\r?\\n", -1);

        // line က တစ်လိုင်းထက်များရင် arrow အတွက် padding ပြန်တွက်မယ်။
        if (line > 0) {
            for (int i = 0; i < line; i++) {
                col = col - sourceLines[i].length() - 1;
            }
        }

        System.err.printf("\n%s: %s\n\n", level, message);
        System.err.println(filename + ":");
        System.err.printf("    %d | %s\n", line + 1, sourceLines[line]);
        System.err.printf("        %s%s\n\n", " ".repeat(col), "^");
    }
}