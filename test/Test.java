import java.util.Scanner;
import java.io.File;
import java.util.concurrent.TimeUnit;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Test {

    public static void main(String[] args) throws Exception{
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

    private static void parse(String fileName) throws Exception{
        CharStream cs = new ANTLRFileStream(fileName);
        TimeUnit.MILLISECONDS.sleep(10);
        KotlinLexer lexer = new KotlinLexer(cs);
        TimeUnit.MILLISECONDS.sleep(10);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TimeUnit.MILLISECONDS.sleep(10);
        KotlinParser parser = new KotlinParser(tokens);
        TimeUnit.MILLISECONDS.sleep(10);
        ParseTree tree = parser.kotlinFile();
        TimeUnit.MILLISECONDS.sleep(10);
        System.out.println(tree.toStringTree(parser));

    }
}
