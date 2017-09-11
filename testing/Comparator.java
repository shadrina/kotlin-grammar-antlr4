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
        testFile("Enums");
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

        if (ANTLRtreeChildCount == PSItreeChildCount && compareRuleNames(ANTLRtree, PSItree)) {
            int redundantRuleNextChild = 0;
            int ANTLRnext = 0;
            int PSInext = 0;
            int childCount = ANTLRtreeChildCount;
            while (childCount != 0) {
                ANTLRnext = ANTLR_chooseNextChild(ANTLRtree, ANTLRnext);
                PSInext = PSI_chooseNextChild(PSItree, PSInext);
                if (ANTLRnext == ANTLRtree.getChildCount() && PSInext == PSItree.getChildCount()) return result;

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

    private static boolean compareRuleNames(ParseTree ANTLRtree, ParserTree PSItree) throws Exception {
        String ANTLRrule = ANTLR_getRuleName(ANTLRtree);
        String PSIrule = PSItree.getRuleName();
        boolean result = ANTLRrule.equals("KotlinFile") && PSIrule.startsWith("KtFile")
        //        || ANTLRrule.equals("Script") && PSIrule.equals("")
                || ANTLRrule.equals("FileAnnotation") && PSIrule.equals("ANNOTATION")
                || ANTLRrule.equals("PackageHeader") && PSIrule.equals("PACKAGE_DIRECTIVE")
                || ANTLRrule.equals("ImportDirective") && PSIrule.equals("IMPORT_LIST")
                || ANTLRrule.equals("ImportHeader") && PSIrule.equals("IMPORT_DIRECTIVE")
                || ANTLRrule.equals("ImportAlias") && PSIrule.equals("IMPORT_ALIAS")
                || ANTLRrule.equals("TypeAlias") && PSIrule.equals("TYPEALIAS")
                || ANTLRrule.equals("ClassDeclaration") && PSIrule.equals("CLASS")
                || ANTLRrule.equals("InterfaceDeclaration") && PSIrule.equals("INTERFACE")
                || ANTLRrule.equals("PrimaryConstructor") && PSIrule.equals("PRIMARY_CONSTRUCTOR")
                || ANTLRrule.equals("ClassParameters") && PSIrule.equals("VALUE_PARAMETER_LIST")
                || ANTLRrule.equals("ClassParameter") && PSIrule.equals("VALUE_PARAMETER")
                || ANTLRrule.equals("DelegationSpecifiers") && PSIrule.equals("SUPER_TYPE_LIST")
                || ANTLRrule.equals("ConstructorInvocation") && PSIrule.equals("SUPER_TYPE_CALL_ENTRY")
                || ANTLRrule.equals("ExplicitDelegation") && PSIrule.equals("DELEGATED_SUPER_TYPE_ENTRY")
                || ANTLRrule.equals("ClassBody") && PSIrule.equals("CLASS_BODY")
                || ANTLRrule.equals("AnonymousInitializer") && PSIrule.equals("CLASS_INITIALIZER")
        //        || ANTLRrule.equals("SecondaryConstructor") && PSIrule.equals("")
        //        || ANTLRrule.equals("ConstructorDelegationCall") && PSIrule.equals("")
                || ANTLRrule.equals("EnumClassBody") && PSIrule.equals("CLASS_BODY")
                || ANTLRrule.equals("EnumEntry") && PSIrule.equals("ENUM_ENTRY")
                || ANTLRrule.equals("FunctionDeclaration") && PSIrule.equals("FUN")
                || ANTLRrule.equals("FunctionValueParameters") && PSIrule.equals("VALUE_PARAMETER_LIST")
                || ANTLRrule.equals("FunctionValueParameter") && PSIrule.equals("VALUE_PARAMETER")
                || ANTLRrule.equals("Parameter") && PSIrule.equals("VALUE_PARAMETER")
                || ANTLRrule.equals("ObjectDeclaration") && PSIrule.equals("OBJECT_DECLARATION")
                || ANTLRrule.equals("CompanionObject") && PSIrule.equals("OBJECT_DECLARATION")
                || ANTLRrule.equals("PropertyDeclaration") && PSIrule.equals("PROPERTY")
                || ANTLRrule.equals("MultiVariableDeclaration") && PSIrule.equals("DESTRUCTURING_DECLARATION")
                || ANTLRrule.equals("VariableDeclaration") && PSIrule.equals("DESTRUCTURING_DECLARATION_ENTRY")
                || ANTLRrule.equals("Getter") && PSIrule.equals("PROPERTY_ACCESSOR")
                || ANTLRrule.equals("Setter") && PSIrule.equals("PROPERTY_ACCESSOR")
                || ANTLRrule.equals("TypeParameters") && PSIrule.equals("TYPE_PARAMETER_LIST")
                || ANTLRrule.equals("TypeParameter") && PSIrule.equals("TYPE_PARAMETER")
                || ANTLRrule.equals("NullableType") && PSIrule.equals("NULLABLE_TYPE")
                || ANTLRrule.equals("Type") && PSIrule.equals("TYPE_REFERENCE")
                || ANTLRrule.equals("TypeReference") && PSIrule.equals("TYPE_REFERENCE")
                || ANTLRrule.equals("UserType") && PSIrule.equals("USER_TYPE")
        //        || ANTLRrule.equals("SimpleUserType") && PSIrule.equals("")
                || ANTLRrule.equals("FunctionType") && PSIrule.equals("FUNCTION_TYPE")
                || ANTLRrule.equals("FunctionTypeParameters") && PSIrule.equals("VALUE_PARAMETER_LIST")
                || ANTLRrule.equals("TypeConstraint") && PSIrule.equals("TYPE_CONSTRAINT")
                || ANTLRrule.equals("Block") && PSIrule.equals("BLOCK")
                || ANTLRrule.equals("Statements") && PSIrule.equals("BLOCK")
        //        || ANTLRrule.equals("Declaration") && PSIrule.equals("")
                || ANTLRrule.equals("Assignment") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("Disjunction") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("Conjunction") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("Equality") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("Comparison") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("InfixOperation") && PSIrule.equals("IS_EXPRESSION")
                || ANTLRrule.equals("InfixOperation") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("ElvisExpression") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("InfixFunctionCall") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("RangeExpression") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("AdditiveExpression") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("MultiplicativeExpression") && PSIrule.equals("BINARY_EXPRESSION")
                || ANTLRrule.equals("AsExpression") && PSIrule.equals("BINARY_WITH_TYPE")
                || ANTLRrule.equals("PrefixUnaryExpression") && PSIrule.equals("PREFIX_EXPRESSION")
                || ANTLRrule.equals("PostfixUnaryExpression") && PSIrule.equals("POSTFIX_EXPRESSION")
                || ANTLRrule.equals("CallExpression") && PSIrule.equals("CALL_EXPRESSION")
                || ANTLRrule.equals("DotQualifiedExpression") && PSIrule.equals("DOT_QUALIFIED_EXPRESSION")
                || ANTLRrule.equals("DotQualifiedExpression") && PSIrule.equals("SAFE_ACCESS_EXPRESSION")
                || ANTLRrule.equals("IndexingExpression") && PSIrule.equals("ARRAY_ACCESS_EXPRESSION")
        //        || ANTLRrule.equals("CallSuffix") && PSIrule.equals("")
        //        || ANTLRrule.equals("AnnotatedLambda") && PSIrule.equals("")
                || ANTLRrule.equals("ArrayAccess") && PSIrule.equals("INDICES")
                || ANTLRrule.equals("ValueArguments") && PSIrule.equals("VALUE_ARGUMENT_LIST")
                || ANTLRrule.equals("TypeArguments") && PSIrule.equals("TYPE_ARGUMENT_LIST")
                || ANTLRrule.equals("TypeProjection") && PSIrule.equals("TYPE_PROJECTION")
                || ANTLRrule.equals("ValueArgument") && PSIrule.equals("VALUE_ARGUMENT")
                || ANTLRrule.equals("ParenthesizedExpression") && PSIrule.equals("PARENTHESIZED")
                || ANTLRrule.equals("LineStringLiteral") && PSIrule.equals("STRING_TEMPLATE")
                || ANTLRrule.equals("MultiLineStringLiteral") && PSIrule.equals("STRING_TEMPLATE")
                || ANTLRrule.equals("FunctionLiteral") && PSIrule.equals("FUNCTION_LITERAL")
                || ANTLRrule.equals("LambdaParameters") && PSIrule.equals("VALUE_PARAMETER_LIST")
                || ANTLRrule.equals("LambdaParameter") && PSIrule.equals("VALUE_PARAMETER")
                || ANTLRrule.equals("ObjectLiteral") && PSIrule.equals("OBJECT_DECLARATION")
        //        || ANTLRrule.equals("ThisExpression") && PSIrule.equals("")
                || ANTLRrule.equals("SuperExpression") && PSIrule.equals("SUPER_EXPRESSION")
                || ANTLRrule.equals("IfExpression") && PSIrule.equals("IF")
                || ANTLRrule.equals("WhenExpression") && PSIrule.equals("WHEN")
                || ANTLRrule.equals("WhenEntry") && PSIrule.equals("WHEN_ENTRY")
        //        || ANTLRrule.equals("WhenCondition") && PSIrule.equals("")
        //        || ANTLRrule.equals("RangeTest") && PSIrule.equals("")
        //        || ANTLRrule.equals("TypeTest") && PSIrule.equals("")
        //        || ANTLRrule.equals("TryExpression") && PSIrule.equals("")
        //        || ANTLRrule.equals("CatchBlock") && PSIrule.equals("")
        //        || ANTLRrule.equals("FinallyBlock") && PSIrule.equals("")
        //        || ANTLRrule.equals("LoopExpression") && PSIrule.equals("")
                || ANTLRrule.equals("ForExpression") && PSIrule.equals("FOR")
        //        || ANTLRrule.equals("WhileExpression") && PSIrule.equals("")
        //        || ANTLRrule.equals("DoWhileExpression") && PSIrule.equals("")
                || ANTLRrule.equals("JumpExpression") && PSIrule.equals("THROW")
                || ANTLRrule.equals("JumpExpression") && PSIrule.equals("RETURN")
                || ANTLRrule.equals("JumpExpression") && PSIrule.equals("BREAK")
                || ANTLRrule.equals("JumpExpression") && PSIrule.equals("CONTINUE")
                || ANTLRrule.equals("CallableReference") && PSIrule.equals("CALLABLE_REFERENCE_EXPRESSION")
                || ANTLRrule.equals("ModifierList") && PSIrule.equals("MODIFIER_LIST")
                || ANTLRrule.equals("Annotation") && PSIrule.equals("ANNOTATION_ENTRY")
                || ANTLRrule.equals("AnnotationList") && PSIrule.equals("ANNOTATION")
        //        || ANTLRrule.equals("AnnotationUseSiteTarget") && PSIrule.equals("")
                || ANTLRrule.equals("UnescapedAnnotation") && PSIrule.equals("ANNOTATION_ENTRY")
                || ANTLRrule.equals("Identifier") && PSIrule.equals("DOT_QUALIFIED_EXPRESSION");
        return result;
    }

    private static int ANTLR_chooseNextChild(ParseTree tree, int next) throws Exception {
        while (next != tree.getChildCount()
                && (ANTLR_isList(tree.getChild(next)) || !ANTLR_isRelevantRule(tree.getChild(next)))) next++;
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
            //considering label definition and label reference for two tokens
            if (ANTLR_getRuleName(tree.getChild(i)).equals("TerminalNodeImpl") && !tree.getChild(i).getText().equals("@")
                    && (tree.getChild(i).getText().startsWith("@") || tree.getChild(i).getText().endsWith("@")))
                childCount++;
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
                || ruleName.equals("TypeConstraints")
                || ruleName.equals("EnumEntries")
                || ruleName.equals("MemberAccessOperator")
                || ruleName.equals("AsExpressionTail")
                || ruleName.equals("SimpleUserType")
                || ruleName.equals("ParenthesizedType");
    }

    private static String ANTLR_getRuleName(ParseTree tree) throws Exception {
        return tree.getClass().getSimpleName().replace("Context", "");
    }

    private static int PSI_chooseNextChild(ParserTree tree, int next) throws Exception {
        while (next != tree.getChildCount()
                && (PSI_isList(tree.getChild(next)) || !PSI_isRelevantRule(tree.getChild(next)))) next++;
        return next;
    }

    private static boolean PSI_isList(ParserTree tree) throws Exception {
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
                && !tree.getToken().getTokenType().equals("SEMICOLON");
    }

}