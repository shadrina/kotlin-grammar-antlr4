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
        testFile("ExtensionsWithQNReceiver");
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
            int redundantRuleLastCheckedChild = 0;
            int ANTLRnext = 0;
            int PSInext = 0;
            int count = 0;
            while (count != ANTLRtreeChildCount) {
                while (ANTLRnext != ANTLRtree.getChildCount() && ANTLR_isList(ANTLRtree.getChild(ANTLRnext))) ANTLRnext++;
                while (PSInext != PSItree.getChildCount() && PSI_isList(PSItree.getChild(PSInext))) PSInext++;

                if (ANTLRnext == ANTLRtree.getChildCount() && PSInext == PSItree.getChildCount()) return result;

                if (ANTLRnext == ANTLRtree.getChildCount())
                    return false;
                ParseTree ANTLRchild = ANTLRtree.getChild(ANTLRnext);
                if (ANTLR_isRedundantRule(ANTLRchild)) {
                    for (; redundantRuleLastCheckedChild < ANTLRchild.getChildCount(); redundantRuleLastCheckedChild++)
                        if (!ANTLR_isList(ANTLRchild.getChild(redundantRuleLastCheckedChild))) break;
                    if (redundantRuleLastCheckedChild == ANTLRchild.getChildCount()) {
                        redundantRuleLastCheckedChild = 0;
                        ANTLRnext++;
                        continue;
                    }
                    ANTLRchild = ANTLRchild.getChild(redundantRuleLastCheckedChild);
                }
                while (ANTLR_getRelevantChildCount(ANTLRchild) == 1) {
                    int i = 0;
                    while (!ANTLR_isRelevantRule(ANTLRchild.getChild(i))) i++;
                    ANTLRchild = ANTLRchild.getChild(i);
                }

                if (PSInext == PSItree.getChildCount())
                    return false;
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

    private static boolean ANTLR_isList(ParseTree tree) throws Exception {
        int childCount = ANTLR_getRelevantChildCount(tree);
        if (childCount == 0) return true;
        if (childCount == 1) {
            int i = 0;
            while (!ANTLR_isRelevantRule(tree.getChild(i))) i++;
            return ANTLR_isList(tree.getChild(i));
        }
        return false;
    }

    private static int ANTLR_getRelevantChildCount(ParseTree tree) throws Exception {
        int childCount = tree.getChildCount();
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (!ANTLR_isRelevantRule(tree.getChild(i))) childCount--;
            if (ANTLR_isRedundantRule(tree.getChild(i)))
                childCount += -1 + tree.getChild(i).getChildCount();
            if (tree.getChild(i).getChildCount() == 0 && ((TerminalNodeImpl)tree.getChild(i)).getText().contains("?::"))
                childCount++;
        }
        return childCount;
    }

    private static boolean ANTLR_isRelevantRule(ParseTree tree) throws Exception {
        if (tree.getChildCount() == 0) {
            TerminalNodeImpl node = (TerminalNodeImpl) tree;
            return     !node.getText().contains("\r\n")
                    && !node.getText().contains("\n")
                    && !node.getText().contains(";")
                    && !node.getText().contains("<EOF>");
        }
        return !ANTLR_getRuleName(tree).contains("Semi");
    }

    private static boolean ANTLR_isRedundantRule(ParseTree tree) throws Exception {
        String ruleName = ANTLR_getRuleName(tree);
        return     ruleName.startsWith("VariableDeclaration")
                || ruleName.startsWith("Parameter")
                || ruleName.startsWith("FunctionBody")
                || ruleName.startsWith("FunctionType");
    }

    private static String ANTLR_getRuleName(ParseTree tree) throws Exception {
        return tree.getClass().getSimpleName();
    }

    private static boolean PSI_isList(ParserTree tree) throws Exception {
        if (tree.getRuleName().startsWith("KDoc")) return true;
        int childCount = PSI_getRelevantChildCount(tree);
        if (childCount == 0) return true;
        if (childCount == 1) {
            int i = 0;
            while (!PSI_isRelevantRule(tree.getChild(i))) i++;
            return PSI_isList(tree.getChild(i));
        }
        return false;
    }

    private static int PSI_getRelevantChildCount(ParserTree tree) throws Exception {
        int childCount = tree.getChildCount();
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (!PSI_isRelevantRule(tree.getChild(i))) childCount--;
            if (i < (tree.getChildCount() - 1)
                    && tree.getChild(i).getToken().getTokenType().contains("AT")
                    && (tree.getChild(i + 1).getRuleName().startsWith("ANNOTATION_TARGET")
                     || tree.getChild(i + 1).getRuleName().startsWith("CONSTRUCTOR_CALLEE")))
                childCount--;
        }
        return childCount;
    }

    private static boolean PSI_isRelevantRule(ParserTree tree) throws Exception {
        if (tree.getRuleName().startsWith("BLOCK")) {
            int i = 0;
            while (i != tree.getChildCount()) {
                if (PSI_isRelevantRule(tree.getChild(i))) return true;
                i++;
            }
            return false;
        }
        return     !tree.getRuleName().startsWith("PsiWhiteSpace")
                && !tree.getRuleName().startsWith("PsiComment")
                && !tree.getRuleName().startsWith("KDoc")
                && !tree.getRuleName().startsWith("<empty list>")
                && !(tree.getChildCount() == 1 && tree.getChild(0).getRuleName().contains("<empty list>"))
                && !(tree.getChildCount() == 0 && tree.getToken().getTokenType().contains("SEMICOLON"));
    }

}