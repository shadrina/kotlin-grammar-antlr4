/**
 * An example of using generated files
 * Printing out the token stream and the parse tree
 */

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Test {

    public static void main(String[] args) throws Exception{
        CharStream cs = new ANTLRFileStream("input.txt");
        KotlinLexer lexer = new KotlinLexer(cs);
        TokenStream tokens = new CommonTokenStream(lexer);
        KotlinParser parser = new KotlinParser(tokens);
        ParseTree tree = parser.kotlinFile();
        for (int i = 0; i < tokens.size() ; i++) {
            //System.out.println(tokens.get(i).getText());
            System.out.println(tokens.get(i).toString());
        }
        System.out.println(tree.toStringTree(parser));
    }
}
