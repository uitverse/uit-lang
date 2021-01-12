package com.heinthanth.uit.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateNode {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("\nUsage: GenerateNode <output folder>");
            System.exit(64);
        }
        String output = args[0];

        generateAst(output, "Expression",
            Arrays.asList(
                "BinaryExpression   : Expression left, Token operator, Expression right",
                "GroupingExpression : Expression expression",
                "LiteralExpression  : Token value",
                "UnaryExpression    : Token operator, Expression right",
                "VariableAccessExpression : Token identifier"
            ),
            Arrays.asList("com.heinthanth.uit.Lexer.Token")
        );
        generateAst(output, "Statement",
            Arrays.asList(
                "ExpressionStatement: Expression expression",
                "OutputStatement    : Expression expression",
                "VariableDeclarationStatement : Token type, Token identifier, Expression initializer"
            ),
            Arrays.asList("com.heinthanth.uit.Lexer.Token")
        );
    }

    /**
     * ပေးလာတဲ့ list ကနေ Expression Node တွေထုတ်မယ်။
     *
     * @param output
     * @param baseName
     * @param properties
     * @throws IOException
     */
    private static void generateAst(String output, String baseName, List<String> properties, List<String> imports)
            throws IOException {
        String path = output + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.heinthanth.uit.Runtime;\n");

        for (String imp : imports) {
            writer.println("import " + imp + ";");
        }
        writer.println();

        writer.println("public abstract class " + baseName + " {\n");

        writeVisitor(writer, baseName, properties);

        // The base accept() method.
        writer.println("    public abstract <R> R accept(Visitor<R> visitor);");
        writer.println();

        for (String prop : properties) {
            String className = prop.split(":")[0].trim();
            String fields = prop.split(":")[1].trim();
            writeProps(writer, baseName, className, fields);
            writer.println();
        }

        writer.println("}");
        writer.close();
    }

    /**
     * class ရေးဖို့
     *
     * @param writer
     * @param baseName
     * @param className
     * @param fieldList
     */
    private static void writeProps(PrintWriter writer, String baseName, String className, String fieldList) {
        // static class $name extends $baseName {
        writer.println("    public static class " + className + " extends " + baseName + " {");

        // property တွေကို define မယ်။
        writer.println();

        String[] fields = fieldList.split(", ");

        for (String field : fields) {
            writer.println("        public final " + field + ";");
        }

        writer.println();

        // constructor ရေးမယ်။
        writer.println("        public " + className + "(" + fieldList + ") {");

        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        // visitor ကို accept မယ်။
        writer.println();
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + "(this);");
        writer.println("        }");

        // }
        writer.println("    }");
    }

    /**
     * visitor ရေးဖို့
     */
    private static void writeVisitor(PrintWriter writer, String baseName, List<String> properties) throws IOException {
        writer.println("    public interface Visitor<R> {");
        for (String prop : properties) {
            String typeName = prop.split(":")[0].trim();
            writer.println("        R visit" + typeName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");
        writer.println();
    }
}
