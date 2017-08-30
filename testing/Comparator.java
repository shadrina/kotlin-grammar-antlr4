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

        //DOT_QUALIFIED_EXPRESSION and USER_TYPE are left-recursive rules
        //it is impossible to recreate them using ANTLR
        if (ANTLR_getRuleName(ANTLRtree).equals("Identifier") && PSItree.getRuleName().equals("DOT_QUALIFIED_EXPRESSION"))
            return true;
        if (ANTLR_getRuleName(ANTLRtree).equals("UserType") && PSItree.getRuleName().equals("USER_TYPE"))
            return true;

        boolean result = true;
        int ANTLRtreeChildCount = ANTLR_getRelevantChildCount(ANTLRtree);
        int PSItreeChildCount = PSI_getRelevantChildCount(PSItree);

        if (ANTLRtreeChildCount == PSItreeChildCount) {
            int redundantRuleNextChild = 0;
            int ANTLRnext = 0;
            int PSInext = 0;
            int count = 0;
            while (count != ANTLRtreeChildCount) {
                while (ANTLRnext != ANTLRtree.getChildCount() && ANTLR_isList(ANTLRtree.getChild(ANTLRnext))) ANTLRnext++;
                while (PSInext != PSItree.getChildCount() && PSI_isList(PSItree.getChild(PSInext))) PSInext++;

                if (ANTLRnext == ANTLRtree.getChildCount() && PSInext == PSItree.getChildCount()) return result;

                if (ANTLRnext == ANTLRtree.getChildCount()) return false;
                ParseTree ANTLRchild = ANTLRtree.getChild(ANTLRnext);
                if (ANTLR_isRedundantRule(ANTLRchild)) {
                    for (; redundantRuleNextChild < ANTLRchild.getChildCount(); redundantRuleNextChild++)
                        if (!ANTLR_isList(ANTLRchild.getChild(redundantRuleNextChild))) break;
                    if (redundantRuleNextChild == ANTLRchild.getChildCount()) {
                        redundantRuleNextChild = 0;
                        ANTLRnext++;
                        continue;
                    }
                    ANTLRnext--;
                    ANTLRchild = ANTLRchild.getChild(redundantRuleNextChild);
                    redundantRuleNextChild++;
                }
                while (ANTLR_getRelevantChildCount(ANTLRchild) == 1) {
                    int i = 0;
                    while (!ANTLR_isRelevantRule(ANTLRchild.getChild(i))) i++;
                    ANTLRchild = ANTLRchild.getChild(i);
                }

                if (PSInext == PSItree.getChildCount()) return false;
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
                childCount += -1 + ANTLR_getRelevantChildCount(tree.getChild(i));
            if (tree.getChild(i).getChildCount() == 0 && ((TerminalNodeImpl)tree.getChild(i)).getText().equals("?::"))
                childCount++;
            if (i < tree.getChildCount() - 1 && tree.getChild(i).getChildCount() == 0
                    && ((TerminalNodeImpl)tree.getChild(i)).getText().equals("?")
                    && ((TerminalNodeImpl)tree.getChild(i + 1)).getText().equals("."))
                childCount--;
        }
        return childCount;
    }

    private static boolean ANTLR_isRelevantRule(ParseTree tree) throws Exception {
        if (tree.getChildCount() == 0) {
            TerminalNodeImpl node = (TerminalNodeImpl) tree;
            return     !node.getText().equals("\r\n")
                    && !node.getText().equals("\n")
                    && !node.getText().equals(";")
                    && !node.getText().equals("<EOF>")
                    //skipping modifiers
                    && !node.getText().equals("enum")
                    && !node.getText().equals("final")
                    && !node.getText().equals("const")
                    && !node.getText().equals("protected")
                    && !node.getText().equals("inner")
                    && !node.getText().equals("infix")
                    && !node.getText().equals("companion")
                    && !node.getText().equals("lateinit")
                    && !node.getText().equals("reified")
                    && !node.getText().equals("abstract")
                    && !node.getText().equals("open")
                    && !node.getText().equals("annotation")
                    && !node.getText().equals("data")
                    && !node.getText().equals("override")
                    && !node.getText().equals("tailrec")
                    && !node.getText().equals("operator")
                    && !node.getText().equals("external");
        }
        return     !ANTLR_getRuleName(tree).contains("Semi")
                && !ANTLR_getRuleName(tree).contains("Modifier")
                && !ANTLR_getRuleName(tree).equals("Annotations")
                && !ANTLR_getRuleName(tree).equals("AnnotationList");
    }

    private static boolean ANTLR_isRedundantRule(ParseTree tree) throws Exception {
        String ruleName = ANTLR_getRuleName(tree);
        return    (ruleName.equals("VariableDeclaration") && !ANTLR_getRuleName(tree.getParent()).equals("MultiVariableDeclaration"))
                ||(ruleName.equals("Parameter")           && !ANTLR_getRuleName(tree.getParent()).equals("FunctionTypeParameters"))
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

    private static boolean PSI_isList(ParserTree tree) throws Exception {
        if (tree.getRuleName().equals("KDoc")) return true;
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
                    && tree.getChild(i).getToken().getTokenType().equals("AT")
                    && (tree.getChild(i + 1).getRuleName().equals("ANNOTATION_TARGET")
                     || tree.getChild(i + 1).getRuleName().equals("CONSTRUCTOR_CALLEE")))
                childCount--;
        }
        return childCount;
    }

    private static boolean PSI_isRelevantRule(ParserTree tree) throws Exception {
        if (tree.getRuleName().equals("BLOCK")) {
            int i = 0;
            while (i != tree.getChildCount()) {
                if (PSI_isRelevantRule(tree.getChild(i))) return true;
                i++;
            }
            return false;
        }
        if (tree.getRuleName().equals("PsiWhiteSpace") || tree.getRuleName().equals("PsiComment")
                || tree.getRuleName().equals("KDoc") || tree.getRuleName().equals("<empty list>")
                || tree.getRuleName().equals("MODIFIER_LIST"))
            return false;
        if (tree.getChildCount() == 1) return PSI_isRelevantRule(tree.getChild(0));
        return !tree.getToken().getTokenType().equals("SEMICOLON");
    }

}
