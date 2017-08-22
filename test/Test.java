/**
 * Parsing files *.kt in the project folder
 */

import java.util.Scanner;
import java.io.File;
import java.util.concurrent.TimeUnit;

import org.antlr.v4.runtime.*;

public class Test {

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a pathname to the project folder");
        String pathName = in.next();
        System.out.println();

        File[] filesList;
        File folder = new File(pathName);
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

    private static void parse(String fileName) throws Exception {
        KotlinLexer lexer = new KotlinLexer(new ANTLRFileStream(fileName));
        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));
        TimeUnit.MILLISECONDS.sleep(15);
        parser.kotlinFile();
        TimeUnit.MILLISECONDS.sleep(15);
        System.out.println("Result: " + parser.getNumberOfSyntaxErrors() + " syntax errors");
    }
}
