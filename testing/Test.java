/**
 * Parsing files *.kt in the project folder
 */

package testing;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.antlr.v4.runtime.*;

public class Test {

    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a pathname to the project folder");
        File folder = new File(in.next());
        System.out.println();

        File[] filesList;
        filesList = folder.listFiles();

        assert filesList != null;
        for (File file : filesList) {
            String fileName = file.getName();
            if (fileName.endsWith(".kt")) {
                System.out.println("Testing " + fileName + "...");
                parse(fileName);
                System.out.println();
            }
        }
    }

    private static int parse(String fileName) throws Exception{
        KotlinLexer lexer = new KotlinLexer(new ANTLRFileStream(fileName));
        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));
        TimeUnit.MILLISECONDS.sleep(15);
        parser.kotlinFile();
        TimeUnit.MILLISECONDS.sleep(15);
        System.out.println("Result: " + parser.getNumberOfSyntaxErrors() + " syntax errors");
        return parser.getNumberOfSyntaxErrors();
    }
}