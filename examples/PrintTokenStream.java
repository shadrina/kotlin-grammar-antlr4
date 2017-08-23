/**
 * An example of using generated files
 * Printing out the token stream and the parse tree
 */

import org.antlr.v4.runtime.*;

public class PrintTokenStream {

    public static void main(String[] args) throws Exception{
        KotlinLexer lexer = new KotlinLexer(new ANTLRFileStream("test.kt"));
        TokenStream tokens = new CommonTokenStream(lexer);
        KotlinParser parser = new KotlinParser(tokens);
        parser.kotlinFile();
        for (int i = 0; i < tokens.size() ; i++) {
            String tokenText = tokens.get(i).getText();
            String lexerRule = lexer.getVocabulary().getSymbolicName(tokens.get(i).getType());
            if (!lexerRule.equals("NL")) //skipping NewLine tokens (for beauty)
                System.out.println(tokenText + "\t\t" + lexerRule);
            
            //Simple way
            //System.out.println(tokens.get(i).toString());
        }
    }
}
