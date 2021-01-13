package com.heinthanth.uit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.heinthanth.uit.Interpreter.Interpreter;
import com.heinthanth.uit.Lexer.Lexer;
import com.heinthanth.uit.Parser.Parser;
import com.heinthanth.uit.Lexer.Token;
import com.heinthanth.uit.Runtime.Statement;
import com.heinthanth.uit.Utils.ErrorHandler;

/**
 * interpreter အတွက် အဓိက class ပေါ့။ သူ့ကနေမှ command line argument
 * ပေါ်မူတည်ပြီး ဘာလုပ်မယ် ညာလုပ်မယ်။ အဲ့လို ဆက်စဥ်းစားသွားမယ်။
 *
 * @version 1.0.0-alpha
 * @author (c) 2021 Hein Thant Maung Maung. MIT Licensed
 */
public class Main {
    // ဒါက interpreter version const
    static final String version = "v1.0.0-aplha";

    // interpreter instance
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {
        // argument ဘာမှ မပါဘူးဆိုတာက stdin run ဖို့များတယ်။ အဲ့တော့
        // runFromStandardInput()
        // ကိုခေါ်လိုက်မယ်။
        if (args.length == 0) {
            runFromStandardInput();
            // ဒီမှာဆို argument တစ်ခုပါလာပြီး
        } else if (args.length == 1) {
            // သူ help တောင်းတာလား ? အာ့ဆို usage information ပြမယ်။
            if ("-h".equals(args[0]) || "--help".equals(args[0])) {
                showUsage(0);
                // သူ version ကြည့်ချင်တာလား ? အာ့ဆို version info ပြမယ်။
            } else if ("-v".equals(args[0]) || "--version".equals(args[0])) {
                showInterpreterInfo(true);
                // သူက interactive mode run ချင်တာလား? အာ့ဆို REPL run မယ်။
            } else if ("-i".equals(args[0]) || "--interactive".equals(args[0])) {
                runREPL();
                // အရင်ဆုံး file လား stdin လားလို့ အရင်စစ်မယ်။ (stdin == "-")
            } else if ("-".equals(args[0])) {
                runFromStandardInput();
                // အပေါ်သုံးခုမဟုတ်ဘူးဆိုရင် သေချာတာက file ကို argument အနေနဲ့ပေးတာပဲ။
            } else {
                // ဒါပေမယ့် file တွေက (-, --) နဲ့ စလေ့မရှိဘူး။ ဒီတော့ option flag
                // မှားရေးမိတာဖြစ်မယ်။
                if (args[0].startsWith("-")) {
                    System.err.println("\nError: invalid option '" + args[0] + "'.\n");
                    System.exit(1);
                } else {
                    runFile(args[0]);
                }
            }
        } else {
            // တစ်ခုခုလွဲနေပြီဆိုတော့ usage info ပြမယ်။
            showUsage(1);
        }
    }

    /**
     * Read - Eval - Print - Loop. user input ယူပြီးတော့ evaluate လုပ်မယ်။
     *
     * @throws IOException
     */
    private static void runREPL() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        // loop နဲ့ evaluate လုပ်မယ်။
        while (true) {
            System.out.print("uit > ");
            String line = reader.readLine();
            if (line == null)
                break;
            interpretREPL(line);
        }
        reader.close();
        input.close();
    }

    /**
     * ဒါက တစ်ခု REPL ရဲ့ builtin command တွေကို interpret လုပ်ဖို့ ( clear တို့
     * quit တို့ ... )
     *
     * @param input REPL ကနေလာတဲ့ string input ပေါ့။
     */
    private static void interpretREPL(String input) throws IOException {
        if (".clear".equals(input)) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            // သူက editor လိုချင်တာလား ?
        } else if (".editor".equals(input)) {
            runFromString(getStringFromStandardInput(), true, "repl");
            // exit မှာလား
        } else if (".exit".equals(input) || ".quit".equals(input)) {
            System.exit(0);
        } else {
            // ဘာမှန်းမသိလို့ ဒီအတိုင်း run လိုက်မယ်။
            runFromString(input, true, "repl");
        }
    }

    /**
     * ဒါက standard input က data တွေကို capture ဖို့ EOF (Ctrl-D) ဖြစ်တဲ့အထိပေါ့။
     *
     * @return stdin ကရတဲ့ string
     * @throws IOException
     */
    private static String getStringFromStandardInput() throws IOException {
        // reader class တွေ ဆောက်မယ်။
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // အဓိကကတော့ shell redirection အတွက်ရည်ရွယ်ပြီး ထည့်ထားတာပါ။
        List<String> lines = new ArrayList<>();
        String[] code = new String[] {};
        String line = null;

        // EOF ြဖစ်တဲ့အထိ readline ပေါ့။
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        reader.close();
        input.close();

        return String.join("\n", lines.toArray(code));
    }

    /**
     * standard input ကနေ code string ကို လက်ခံပြီး evaluate လုပ်မယ်။
     *
     * @throws IOException
     */
    private static void runFromStandardInput() throws IOException {
        // ထုံးစံအတိုင်း code string ရပြီးဆိုတော့ interpret မယ်။
        runFromString(getStringFromStandardInput(), false, "stdin");
    }

    /**
     * file content ကိုဖတ်ပြီး evaluate လုပ်မယ်။ file path ကို လက်ခံမယ်။
     *
     * @param path ဒါကတော့ interpret မယ့် file ရဲ့ path ပေါ့။
     * @throws IOException
     */
    private static void runFile(String path) throws IOException {
        // file class တစ်ခု create တယ်။
        File script = new File(path);
        if (script.canRead()) {
            // file content ကို ဖတ်ပြီး run မယ်။
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            runFromString(new String(bytes, Charset.defaultCharset()), false, script.getName());
            // အဲ့တော့ file content ကို ဖတ်လို့မရရင် error ပြမယ်။
        } else {
            // error မို့လို့ standard error ထဲကို write မယ်။
            System.err.println("\nError: cannot READ '" + script.getName() + "'. Possible error: ENOENT, EACCES.\n");
            // ref: https://man7.org/linux/man-pages/man3/errno.3.html
            System.exit(2);
        }
    }

    /**
     * uit source code ကို interpret လုပ်ပေးတဲ့ function ပေါ့။ token လုပ်မယ်။ parse
     * မယ်။ ရလာတဲ့ ast ကို interpret မယ်။
     *
     * @param code     code to evaluate
     * @param fromREPL ဒါက REPL ကလာတာလား file က လာတာလား ခွဲချင်လို့ပါ။ REPL ကဆိုရင်
     *                 error တက်လည်း script exit မဖြစ်အောင်ပေါ့။
     */
    private static void runFromString(String code, boolean fromREPL, String filename) {
        // error handler ကို initialize လုပ်မယ်။
        ErrorHandler errorHandler = new ErrorHandler(code, filename);

        // code string ကို token ပြောင်းမယ်။
        Lexer lexer = new Lexer(code, errorHandler);
        List<Token> tokens = lexer.tokenize();
        if (!handleError(errorHandler, fromREPL))
            return;

        // for (Token token : tokens) {
        // System.out.println(token);
        // }
        // System.exit(0);

        // parser နဲ့ parse မယ်။
        Parser parser = new Parser(tokens, errorHandler);
        List<Statement> statements = parser.parse();
        if (!handleError(errorHandler, fromREPL))
            return;

        // AstPrinter printer = new AstPrinter();
        // System.out.println(printer.print(expression));
        interpreter.interpret(statements, errorHandler, fromREPL);
    }

    private static boolean handleError(ErrorHandler errorHandler, boolean fromREPL) {
        if (errorHandler.hadError) {
            if (fromREPL) {
                return false;
            } else {
                System.exit(65);
            }
        }
        if (errorHandler.hadRuntimeError) {
            if (fromREPL) {
                return false;
            } else {
                System.exit(70);
            }
        }
        return true;
    }

    /**
     * ဒါက help လေးပေါ့။ example နညး်နညး်ပြောမယ် ဘာညာ။
     *
     * @param exitStatus info ပြပြီးရင် ဘာ code နဲ့ exit မလဲ
     */
    private static void showUsage(int exitStatus) {
        if (exitStatus == 0) {
            showInterpreterInfo(false);
            System.out.println("usage:\tuit [script?]");
            System.out.println("\tscript: path to .uit script to interpret.\n");
            System.out.println("examples:");
            System.out.println("\tuit hello.uit\t[ run 'hello.uit' script' ]");
            System.out.println("\tuit\t\t[ run REPL ]");
            System.out.println("\tuit -\t\t[ run from STDIN ]\n");
        } else {
            System.err.println("\nusage:\tuit [script?]");
            System.err.println("\tscript: path to .uit script to interpret.\n");
            System.err.println("examples:");
            System.err.println("\tuit hello.uit\t[ run 'hello.uit' script' ]");
            System.err.println("\tuit\t\t[ run REPL ]");
            System.err.println("\tuit -\t\t[ run from STDIN ]\n");
        }
        System.exit(exitStatus);
    }

    /**
     * interpreter ရဲ့ version တို့ developer information တွေပြမယ်။
     *
     * @param shouldExit info ပြပြီးရင် exit လုပ်သင့်လား မလုပ်သင့်လား ဒီ paramter
     *                   နဲ့ဆုံးဖြတ်မယ်။
     */
    private static void showInterpreterInfo(boolean shouldExit) {
        System.out.println("\nuit-lang - " + version);
        System.out.println("(c) Hein Thant Maung Maung. MIT Licensed.\n");
        if (shouldExit)
            System.exit(0);
    }
}