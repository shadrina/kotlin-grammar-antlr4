import java.util.Scanner;
import java.io.File;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Test {
    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        System.out.println("Enter pathname to the project folder");
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
        KotlinLexer lexer = new KotlinLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KotlinParser parser = new KotlinParser(tokens);
        ParseTree tree = parser.kotlinFile();
        System.out.println(tree.toStringTree(parser));
    }
}
