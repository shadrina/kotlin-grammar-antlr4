/**
 * Comparing syntax trees of the correct test data from https://github.com/JetBrains/kotlin/tree/master/compiler/testData/psi
 * "Test passed" means similar trees
 * "Test failed" means different trees
 */

package testing;

public class TreesTest {

    public static void main(String[] args) throws Exception {
        String[] correctFiles = {
                "AnonymousInitializer.kt",
                "BabySteps.kt",
                "ByClauses.kt",
                "CallsInWhen.kt",
                "CallWithManyClosures.kt",
                "CommentsBindingInLambda.kt",
                "CommentsBindingInStatementBlock.kt",
                "Constructors.kt",
                "destructuringInLambdas.kt",
                "DocCommentAfterFileAnnotations.kt",
                "DocCommentForFirstDeclaration.kt",
                "DocCommentOnPackageDirectiveLine.kt",
                "DocCommentsBinding.kt",
                "DoubleColonWhitespaces.kt",
                "DynamicReceiver.kt",
                "DynamicTypes.kt",
                "EmptyFile.kt",
                "EnumCommas.kt",
                "EnumEntrySemicolonInlineMember.kt",
                "EnumEntrySemicolonMember.kt",
                "EnumIn.kt",
                "EnumInline.kt",
                "Enums.kt",
                "EnumShortCommas.kt",
                "EnumShortWithOverload.kt",
                "EOLsInComments.kt",
                "EOLsOnRollback.kt",
                "ExtensionsWithQNReceiver.kt",
                "FloatingPointLiteral.kt",
                "FunctionCalls.kt",
                "FunctionLiterals.kt",
                "FunctionsWithoutName.kt",
                "FunctionTypes.kt",
                "IfWithPropery.kt",
                "Imports.kt",
                "ImportSoftKW.kt",
                "Inner.kt",
                "Interface.kt",
                "LineCommentAfterFileAnnotations.kt",
                "LineCommentForFirstDeclaration.kt",
                "LineCommentsInBlock.kt",
                "LocalDeclarations",
                "LongPackageName.kt",
                "ModifierAsSelector.kt",
                "NamedClassObject.kt",
                "NestedComments.kt",
                "NewLinesValidOperations.kt",
                "NotIsAndNotIn.kt",
                "ObjectLiteralAsStatement.kt",
                "PropertyInvokes.kt",
                "QuotedIdentifiers.kt",
                "SemicolonAfterIf.kt",
                "SimpleClassMembers.kt",
                "SimpleExpressions.kt",
                "SimpleModifiers.kt",
                "SoftKeywords.kt",
                "SoftKeywordsInTypeArguments.kt",
                "TraitConstructor.kt",
                "TypeAlias.kt",
                "TypeConstraints.kt"
        };

        int passedTestsCount = 0;
        for (String fileName : correctFiles) {
                System.out.format("%-38s", (fileName + ":"));
                if (Comparator.testFile(fileName.replace(".kt", "")))
                    passedTestsCount++;
        }
        System.out.println("\nNumber of passed tests: " + passedTestsCount);
    }
}
