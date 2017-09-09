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
        if (checkIncomparableRules(ANTLRtree, PSItree)) return true;
        boolean result = true;
        int ANTLRtreeChildCount = ANTLR_getRelevantChildCount(ANTLRtree);
        int PSItreeChildCount = PSI_getRelevantChildCount(PSItree);

        if (ANTLRtreeChildCount == PSItreeChildCount) {
            int redundantRuleNextChild = 0;
            int ANTLRnext = 0;
            int PSInext = 0;
            int childCount = ANTLRtreeChildCount;
            while (childCount != 0) {
                ANTLRnext = ANTLR_chooseNextChild(ANTLRtree, ANTLRnext);
                PSInext = PSI_chooseNextChild(PSItree, PSInext);
                if (ANTLRnext == ANTLRtree.getChildCount() && PSInext == PSItree.getChildCount()) return true;

                //checking next ANTLRchild
                if (ANTLRnext == ANTLRtree.getChildCount()) return false;
                ParseTree ANTLRchild = ANTLRtree.getChild(ANTLRnext);
                //if the chosen rule is redundant, then the next candidate must be selected from its children
                if (ANTLR_isRedundantRule(ANTLRchild)) {
                    redundantRuleNextChild = ANTLR_chooseNextChild(ANTLRchild, redundantRuleNextChild);
                    if (redundantRuleNextChild == ANTLRchild.getChildCount()) {
                        redundantRuleNextChild = 0;
                        ANTLRnext++;
                        continue;
                    }
                    ANTLRnext--;
                    ANTLRchild = ANTLRchild.getChild(redundantRuleNextChild);
                    redundantRuleNextChild++;
                }
                //skipping degenerated part of a tree
                while (ANTLR_getRelevantChildCount(ANTLRchild) == 1) {
                    int i = 0;
                    while (!ANTLR_isRelevantRule(ANTLRchild.getChild(i))) i++;
                    ANTLRchild = ANTLRchild.getChild(i);
                }

                //checking next PSIchild
                if (PSInext == PSItree.getChildCount()) return false;
                ParserTree PSIchild = PSItree.getChild(PSInext);
                //skipping degenerated part of a tree
                while (PSI_getRelevantChildCount(PSIchild) == 1) {
                    int i = 0;
                    while (!PSI_isRelevantRule(PSIchild.getChild(i))) i++;
                    PSIchild = PSIchild.getChild(i);
                }

                result = result && compareTrees(ANTLRchild, PSIchild);
                ANTLRnext++;PSInext++;
                childCount--;
            }
            return result;
        }
        return false;
    }

    //if the PSI rule is left-recursive, that is impossible to recreate it using ANTLR
    //so we can assume that it match with its ANTLR-alternative
    private static boolean checkIncomparableRules(ParseTree ANTLRtree, ParserTree PSItree) throws Exception {
        return     ANTLR_getRuleName(ANTLRtree).equals("Identifier") && PSItree.getRuleName().equals("DOT_QUALIFIED_EXPRESSION")
                || ANTLR_getRuleName(ANTLRtree).equals("UserType")   && PSItree.getRuleName().equals("USER_TYPE");
    }

    private static int ANTLR_chooseNextChild(ParseTree tree, int next) throws Exception {
        while (next != tree.getChildCount()
                && (ANTLR_isList(tree.getChild(next)) || ANTLR_isRelevantRule(tree.getChild(next)))) next++;
        return next;
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
            if (!ANTLR_isRelevantRule(tree.getChild(i)))  childCount--;
            if (ANTLR_isRedundantRule(tree.getChild(i)))  childCount += -1 + ANTLR_getRelevantChildCount(tree.getChild(i));
            //considering "?::" for two tokens
            if (tree.getChild(i).getText().equals("?::")) childCount++;
            //considering "?" "." for one token
            if (i < tree.getChildCount() - 1 && tree.getChild(i).getText().equals("?") && tree.getChild(i + 1).getText().equals("."))
                childCount--;
        }
        return childCount;
    }

    private static boolean ANTLR_isRelevantRule(ParseTree tree) throws Exception {
        String nodeText = tree.getText();
        return     !nodeText.equals("\r\n")
                && !nodeText.equals("\n")
                && !nodeText.equals(";")
                && !nodeText.equals("<EOF>")
                && !ANTLR_getRuleName(tree).contains("Semi");
    }

    private static boolean ANTLR_isRedundantRule(ParseTree tree) throws Exception {
        String ruleName = ANTLR_getRuleName(tree);
        return     ruleName.equals("VariableDeclaration") && !ANTLR_getRuleName(tree.getParent()).equals("MultiVariableDeclaration")
                || ruleName.equals("Parameter")           && !ANTLR_getRuleName(tree.getParent()).equals("FunctionTypeParameters")
                || ruleName.equals("FunctionBody")
                || ruleName.equals("FunctionType")
                || ruleName.equals("TypeConstraints")
                || ruleName.equals("EnumEntries")
                || ruleName.equals("MemberAccessOperator")
                || ruleName.equals("AsExpressionTail");
    }

    private static String ANTLR_getRuleName(ParseTree tree) throws Exception {
        return tree.getClass().getSimpleName().replace("Context", "");
    }

    private static int PSI_chooseNextChild(ParserTree tree, int next) throws Exception {
        while (next != tree.getChildCount()
                && (PSI_isList(tree.getChild(next)) || PSI_isRelevantRule(tree.getChild(next)))) next++;
        return next;
    }

    private static boolean PSI_isList(ParserTree tree) throws Exception {
        //considering LABEL_QUALIFIER for one token
        if (tree.getRuleName().equals("LABEL_QUALIFIER")) return true;
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
        for (int i = 0; i < tree.getChildCount(); i++)
            if (!PSI_isRelevantRule(tree.getChild(i))) childCount--;
        return childCount;
    }

    private static boolean PSI_isRelevantRule(ParserTree tree) throws Exception {
        String ruleName = tree.getRuleName();
        return     !ruleName.equals("PsiWhiteSpace")
                && !ruleName.equals("PsiComment")
                && !ruleName.equals("KDoc")
                //considering empty block for irrelevant rule
                && !(ruleName.equals("BLOCK") && tree.getChild(0).getRuleName().equals("<empty list>"))
                && !tree.getToken().getTokenType().equals("SEMICOLON");
    }

}