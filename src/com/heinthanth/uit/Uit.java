package com.heinthanth.uit;

import com.heinthanth.uit.Interpreter.Interpreter;
import com.heinthanth.uit.Interpreter.RuntimeError;
import com.heinthanth.uit.Lexer.Lexer;
import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Lexer.TokenType;
import com.heinthanth.uit.Node.Statement;
import com.heinthanth.uit.Parser.Parser;
import com.heinthanth.uit.Interpreter.Resolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Uit {
    /**
     * status for some sort of Lexer, Parser, Interpreter error
     */
    static boolean hadError = false;

    /**
     * status for runtime error
     */
    static boolean hadRuntimeError = false;

    private static String sourceString = "";

    /**
     * Interpreter instance to interpret
     */
    private static final Interpreter interpreter = new Interpreter();

    /**
     * Entry point of Interpreter
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            runREPL();
        } else if (args.length == 1) {
            if (args[0].equals("-h") || args[0].equals("--help")) {
                version();
                System.out.println();
                showHelp();
                System.exit(0);
            } else if (args[0].equals("-v") || args[0].equals("--version")) {
                version();
                System.exit(0);
            } else {
                runFile(args[0]);
            }
        } else {
            version();
            System.out.println();
            showHelp();
            System.exit(1);
        }
    }

    /**
     * run from script file
     *
     * @param path path to .uit script file
     */
    private static void runFile(String path) throws IOException {
        File scriptFile = new File(path);
        if (scriptFile.exists()) {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            runFromString(new String(bytes, Charset.defaultCharset()));
            // exit if error occur
            if (hadError) System.exit(65);
            if (hadRuntimeError) System.exit(70);
        } else {
            System.out.println("Error: file '" + path + "' not exists.");
            System.exit(1);
        }
    }

    /**
     * run REPL loop
     */
    private static void runREPL() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print("uit > ");
            String line = reader.readLine();
            if (line == null) break;
            runFromString(line);
            // reset error status for next loop
            hadError = false;
            hadRuntimeError = false;
        }
    }

    /**
     * Run from code string
     *
     * @param code code string to execute
     */
    private static void runFromString(String code) {
        sourceString = code;
        List<Token> tokens = new Lexer(code).tokenize();
//        for (Token token : tokens) {
//            System.out.print(token);
//        }
        if (hadError) return;
        List<Statement> statements = new Parser(tokens).parse();
        if (hadError) return;
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if (hadError) return;
        interpreter.interpret(statements);
    }

    /**
     * show usage information
     */
    private static void showHelp() {
        System.out.println("usage: uit [script?]");
    }

    /**
     * show version info
     */
    private static void version() {
        System.out.println("UIT Interpreter for Techie - v1.0.0");
        System.out.println("(c) Hein Thant Maung Maung 2020. MIT Licensed");
    }

    /**
     * report error
     *
     * @param line    Error causing Line
     * @param message error message
     */
    public static void error(int line, int index, String message) {
        report(line, index, "", message);
    }

    /**
     * Overload token error
     *
     * @param token   error causing token
     * @param message error message
     */
    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, token.index, " at end", message);
        } else {
            report(token.line, token.index, " at '" + token.sourceString + "'", message);
        }
    }

    /**
     * handle runtime error
     *
     * @param error runtime error instance
     */
    public static void runtimeError(RuntimeError error) {
//        System.err.println(error.getMessage() +
//                "\n[line " + error.token.line + "]");
        report(error.token.line, error.token.index, "", error.getMessage());
        hadRuntimeError = true;
    }

    /**
     * show error message to console
     *
     * @param line    Error causing line number
     * @param where   piece of code snippet where error occur
     * @param message Error message.
     */
    private static void report(int line, int index, String where, String message) {
        sourceError(line, index, message);
//        System.err.println(
//                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    /**
     * show source code error snippet
     */
    private static void sourceError(int line, int index, String message) {
        String[] sourceLines = sourceString.split("\\r?\\n", -1);
        if (line > 0) {
            for (int i = 0; i < line; i++) {
                index = index - sourceLines[i].length() - 1;
            }
        }
        if (line == 0) {
            System.out.println("  | ");
        } else {
            System.err.println(line + " | " + sourceLines[line - 1]);
        }
        System.err.println((line + 1) + " | " + sourceLines[line]);
        System.err.println("  | " + " ".repeat(index) + "^ " + message);
        if (line >= sourceLines.length - 1) {
            System.out.println("  | ");
        } else {
            System.err.println((line + 2) + " | " + sourceLines[line + 1]);
        }
    }
}
