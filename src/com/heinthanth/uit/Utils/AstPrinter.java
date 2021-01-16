// package com.heinthanth.uit.Utils;

// import com.heinthanth.uit.Runtime.Expression;
// import com.heinthanth.uit.Runtime.Expression.BinaryExpression;
// import com.heinthanth.uit.Runtime.Expression.GroupingExpression;
// import com.heinthanth.uit.Runtime.Expression.LiteralExpression;
// import com.heinthanth.uit.Runtime.Expression.UnaryExpression;

// public class AstPrinter implements Expression.Visitor<String> {
//     /**
//      * parser ကရလာတဲ့ expression ကို print လုပ်မယ်။
//      *
//      * @param expression
//      * @return
//      */
//     public String print(Expression expression) {
//         return expression.accept(this);
//     }

//     /**
//      * binary expression ကို string အနေနဲ့ represent လုပ်မယ်။ ( + 1, 3 )
//      */
//     @Override
//     public String visitBinaryExpression(BinaryExpression expression) {
//         return parenthesize(expression.operator.lexeme, expression.left, expression.right);
//     }

//     /**
//      * literal expression ကို string အနေနဲ့ represent လုပ်မယ်။ (1, 2, true, false)
//      */
//     @Override
//     public String visitLiteralExpression(LiteralExpression expression) {
//         return expression.value.getValue().toString();
//     }

//     /**
//      * unary expression ကို string အနေနဲ့ represent လုပ်မယ်။ ( - 3 )
//      */
//     @Override
//     public String visitUnaryExpression(UnaryExpression expression) {
//         return parenthesize(expression.operator.lexeme, expression.right);
//     }

//     /**
//      * grouping expression ကို print မယ်
//      */
//     @Override
//     public String visitGroupingExpression(GroupingExpression expression) {
//         return parenthesize("group", expression.expression);
//     }

//     private String parenthesize(String name, Expression... exprs) {
//         StringBuilder builder = new StringBuilder();

//         builder.append("(").append(name);
//         for (Expression expr : exprs) {
//             builder.append(" ");
//             builder.append(expr.accept(this));
//         }
//         builder.append(")");

//         return builder.toString();
//     }
// }
