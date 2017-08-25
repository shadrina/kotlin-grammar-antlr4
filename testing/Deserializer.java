/**
 * Deserializer of kotlin PSI
 * Creating PSI tree to compare with ANTLR tree
 */

package testing;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Deserializer {

    public static final String INDENT = "  ";

    public static void main(String[] args) throws Exception {
        createTree("test.txt");
    }

    public static ParserTree createTree(String fileName) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        ParserTree tree = new ParserTree();
        tree.setRuleName(lines.get(0));

        int prevIndent = 0;
        int currIndent;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            ParserTree newChild = new ParserTree();

            currIndent = 0;
            while (line.startsWith(INDENT)) {
                line = line.replaceFirst(INDENT, "");
                currIndent++;
            }

            newChild.setRuleName(getRuleName(line));
            ParserToken token = new ParserToken();
            token.setTokenType(getTokenType(line));
            token.setTokenText(getTokenText(line));
            newChild.setToken(token);

            int indentDifference = currIndent - prevIndent;
            if (indentDifference <= 0)
                for (int j = indentDifference; j <= 0; j++)
                    tree = tree.getParent();
            tree.addChild(newChild);
            newChild.setParent(tree);
            tree = tree.getLastChild();
            prevIndent = currIndent;
        }
        while (tree.getParent() != null) tree = tree.getParent();
        return tree;
    }

    private static String getRuleName(String line) throws Exception {
        String[] info = line.split("[(]");
        return info[0];
    }

    private static String getTokenType(String line) throws Exception {
        if (line.startsWith("PsiWhiteSpace")) return "PsiWhiteSpace";
        if (line.startsWith("PsiErrorElement")) return "PsiErrorElement";
        String[] info = line.split("[(]");
        if (info.length == 1) return "";
        return info[1].replace(")", "");
    }

    private static String getTokenText(String line) throws Exception {
        line = line.replace(")", "");
        String[] info = line.split("[(]");
        if (line.startsWith("PsiWhiteSpace")) return info[1];
        if (line.startsWith("PsiErrorElement")) return "";
        if (info.length == 1) return "";
        if (info[1].contains("LPAR")) return "(";
        if (info[1].contains("RPAR")) return ")";
        return info[2];
    }
}