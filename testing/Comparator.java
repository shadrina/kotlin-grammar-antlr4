/**
 * Comparing structures of ANTLR and PSI trees
 * "Test passed" means similar structure
 * "Test failed" means different structure
 */

package testing;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Comparator {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) throws Exception {
        testFile("test");
    }

    public static boolean testFile(String fileName) throws Exception {
        ParseTree ANTLRtree = ANTLR_createTree(fileName + ".kt");
        ParserTree PSItree = PSI_createTree(fileName + ".txt");
        boolean result = compareTrees(ANTLRtree, PSItree);
        if (result) System.out.println(ANSI_GREEN + "Test passed" + ANSI_RESET);
        else System.out.println("Test failed");
        return result;
    }

    private static ParseTree ANTLR_createTree(String fileName) throws Exception {
        KotlinLexer lexer = new KotlinLexer(new ANTLRFileStream(fileName));
        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));
        return parser.kotlinFile();
    }

    private static ParserTree PSI_createTree(String fileName) throws Exception {
        return Deserializer.createTree(fileName);
    }

    private static boolean compareTrees(ParseTree ANTLRtree, ParserTree PSItree) throws Exception {
        boolean result = true;
        int ANTLRtreeChildCount = ANTLR_getRelevantChildCount(ANTLRtree);
        int PSItreeChildCount = PSI_getRelevantChildCount(PSItree);

        if (ANTLRtreeChildCount == PSItreeChildCount) {
            int ANTLRnext = 0;
            int PSInext = 0;
            int count = 0;
            while (count != ANTLRtreeChildCount) {
                while (ANTLRnext != ANTLRtree.getChildCount() && ANTLR_isList(ANTLRtree.getChild(ANTLRnext))) ANTLRnext++;
                while (PSInext != PSItree.getChildCount() && PSI_isList(PSItree.getChild(PSInext))) PSInext++;

                if (ANTLRnext == ANTLRtree.getChildCount() && PSInext == PSItree.getChildCount()) return result;
                if (ANTLRnext == ANTLRtree.getChildCount() || PSInext == PSItree.getChildCount()) return false;

                ParseTree ANTLRchild = ANTLRtree.getChild(ANTLRnext);
                while (ANTLR_getRelevantChildCount(ANTLRchild) == 1) {
                    int i = 0;
                    while (!ANTLR_isRelevantRule(ANTLRchild.getChild(i))) i++;
                    ANTLRchild = ANTLRchild.getChild(i);
                }
                ParserTree PSIchild = PSItree.getChild(PSInext);
                while (PSI_getRelevantChildCount(PSIchild) == 1) {
                    int i = 0;
                    while (!PSI_isRelevantRule(PSIchild.getChild(i))) i++;
                    PSIchild = PSIchild.getChild(i);
                }

                result = result && compareTrees(ANTLRchild, PSIchild);
                ANTLRnext++;PSInext++;
                count++;
            }
            return result;
        }
        return false;
    }

    private static boolean ANTLR_isList(ParseTree tree) {
        int childCount = ANTLR_getRelevantChildCount(tree);
        if (childCount == 0) return true;
        if (childCount == 1) {
            tree = tree.getChild(0);
            return ANTLR_isList(tree);
        }
        return false;
    }

    private static int ANTLR_getRelevantChildCount(ParseTree tree) {
        int childCount = tree.getChildCount();
        for (int i = 0; i < tree.getChildCount(); i++)
            if (!ANTLR_isRelevantRule(tree.getChild(i))) childCount--;
        return childCount;
    }

    private static boolean ANTLR_isRelevantRule(ParseTree tree) {
        if (tree.getChildCount() == 0) {
            TerminalNodeImpl node = (TerminalNodeImpl) tree;
            return     !node.getText().contains("\r\n")
                    && !node.getText().contains("\n")
                    && !node.getText().contains("<EOF>");
        }
        return !tree.getClass().getSimpleName().contains("Semi");
    }

    private static boolean PSI_isList(ParserTree tree) {
        if (tree.getRuleName().startsWith("KDoc")) return true;
        int childCount = PSI_getRelevantChildCount(tree);
        if (childCount == 0) return true;
        if (childCount == 1) {
            tree = tree.getChild(0);
            return PSI_isList(tree);
        }
        return false;
    }

    private static int PSI_getRelevantChildCount(ParserTree tree) {
        int childCount = tree.getChildCount();
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (!PSI_isRelevantRule(tree.getChild(i))) childCount--;
            if (i < (tree.getChildCount() - 1)
                    && tree.getChild(i).getToken().getTokenType().contains("AT")
                    && tree.getChild(i + 1).getRuleName().contains("ANNOTATION_TARGET"))
                childCount--;
        }
        return childCount;
    }

    private static boolean PSI_isRelevantRule(ParserTree tree) {
        return     !tree.getRuleName().startsWith("PsiWhiteSpace")
                && !tree.getRuleName().startsWith("PsiComment")
                && !tree.getRuleName().startsWith("KDoc")
                && !(tree.getChildCount() == 1 && tree.getChild(0).getRuleName().contains("<empty list>"));
    }

}