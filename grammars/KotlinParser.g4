/**
 * Kotlin Grammar for ANTLR v4
 *
 * Based on:
 * http://jetbrains.github.io/kotlin-spec/#_grammars_and_parsing
 * and
 * http://kotlinlang.org/docs/reference/grammar.html
 *
 * Tested on
 * https://github.com/JetBrains/kotlin/tree/master/compiler/testData/psi
 */

parser grammar KotlinParser;

options { tokenVocab = KotlinLexer; }

file
    : (kotlinFile
    | script) EOF
    ;

kotlinFile
    : preamble topLevelObject*
    ;

script
    : preamble (expression semi?)*
    ;

preamble
    : NL* (fileAnnotation semi?)* (packageHeader semi?)* (importHeader semi?)*
    ;

fileAnnotation
    : FILE COLON (LSQUARE unescapedAnnotation+ RSQUARE | unescapedAnnotation)
    ;

packageHeader
    : PACKAGE identifier
    ;                                                           

importHeader
    : IMPORT identifier (DOT MULT | AS simpleIdentifier)?
    ;

topLevelObject
    : (topClassDeclaration
    | topFunctionDeclaration
    | topObjectDeclaration
    | topPropertyDeclaration
    | typeAlias) semi?
    ;

topClassDeclaration
    : (annotations | visibilityModifier | inheritanceModifier | classModifier)* classDeclaration
    | ENUM NL* (annotations | visibilityModifier | FINAL NL*)* enumClassDeclaration
    | (annotations | visibilityModifier | FINAL NL*)* ENUM NL* enumClassDeclaration
    ;

topFunctionDeclaration
    : (annotations | visibilityModifier | functionModifier)* functionDeclaration
    ;

topObjectDeclaration
    : (annotations | visibilityModifier | FINAL NL*)* objectDeclaration
    ;

topPropertyDeclaration
    : (annotations | visibilityModifier | CONST NL*)* propertyDeclaration
    ;

typeAlias
    : (annotations | visibilityModifier)* TYPE_ALIAS NL* simpleIdentifier NL* typeParameters? NL* ASSIGNMENT NL* type
    ;

classDeclaration
    : (CLASS | INTERFACE) NL* simpleIdentifier NL* typeParameters? NL* primaryConstructor? NL* delegationSpecifiers? NL*
    typeConstraints? NL* classBody?
    ;

enumClassDeclaration
    : CLASS NL* simpleIdentifier NL* typeParameters? NL* primaryConstructor? NL* delegationSpecifiers? NL*
    typeConstraints? NL* enumClassBody?
    ;

primaryConstructor
    : ((annotations | visibilityModifier | PROTECTED NL*)* CONSTRUCTOR NL*)? LPAREN classParameters? RPAREN
    ;

classParameters
    : classParameter (COMMA classParameter)*
;

classParameter
    : (annotations | parameterModifier | OVERRIDE NL*)* (VAL | VAR)? parameter (ASSIGNMENT expression)?
    ;

delegationSpecifiers
    : COLON NL* annotations* delegationSpecifier (NL* COMMA NL* delegationSpecifier)*
    ;

delegationSpecifier
    : constructorInvocation
    | userType
    | explicitDelegation
    ;

constructorInvocation
    : userType callSuffix
    ;

explicitDelegation
    : userType NL* BY NL* expression
    ;

classBody
    : LCURL NL* classMemberDeclaration* NL* RCURL
    ;

classMemberDeclaration
    : (nestedClassDeclaration
    | nestedEnumClassDeclaration
    | memberFunctionDeclaration
    | memberObjectDeclaration
    | memberPropertyDeclaration
    | anonymousInitializer
    | secondaryConstructor) semi?
    ;

nestedClassDeclaration
    : (annotations | visibilityModifier | PROTECTED NL* | inheritanceModifier | classModifier | INNER NL*)*
    classDeclaration
    ;

nestedEnumClassDeclaration
    : ENUM NL* (annotations | visibilityModifier |PROTECTED NL* | FINAL NL*)* enumClassDeclaration
    | (annotations | visibilityModifier | PROTECTED NL* | FINAL NL*)* ENUM NL* enumClassDeclaration
    ;

memberFunctionDeclaration
    : (annotations | visibilityModifier | PROTECTED NL* | inheritanceModifier | functionModifier | OVERRIDE NL* | INFIX NL*)*
    functionDeclaration
    ;

memberObjectDeclaration
    : (annotations | visibilityModifier | PROTECTED NL* | FINAL NL*)* (objectDeclaration | companionObject)
    ;

companionObject
    : COMPANION NL* (annotations | visibilityModifier | PROTECTED NL* | FINAL NL*)*
    OBJECT NL* simpleIdentifier? delegationSpecifiers? classBody?
    ;

memberPropertyDeclaration
    : (annotations | visibilityModifier | PROTECTED NL* | inheritanceModifier | OVERRIDE NL* | LATEINIT NL*)*
    propertyDeclaration
    ;

anonymousInitializer
    : INIT NL* block
    ;
    
secondaryConstructor
    : (annotations | visibilityModifier | PROTECTED NL*)*
    CONSTRUCTOR NL* functionValueParameters (NL* COLON NL* constructorDelegationCall)? NL* block
    ;

constructorDelegationCall
    : THIS NL* valueArguments
    | SUPER NL* valueArguments
    ;

enumClassBody
    : LCURL NL* enumEntries? (NL* SEMICOLON NL* classMemberDeclaration*)? NL* RCURL
    ;

enumEntries
    : enumEntry (NL* COMMA NL* enumEntry)* NL* (COMMA | SEMICOLON)?
    ;

enumEntry
    : simpleIdentifier NL* valueArguments? NL* enumEntryBody?
    ;

enumEntryBody
    : LCURL NL* enumEntryBodyMembers* NL* RCURL
    ;

enumEntryBodyMembers
    : (nestedClassDeclaration
    | nestedEnumClassDeclaration
    | memberFunctionDeclaration
    | memberObjectDeclaration
    | memberPropertyDeclaration
    | anonymousInitializer) semi?
    ;

functionDeclaration
    : FUN NL* typeParameters? NL* (type NL* DOT)? NL* identifier? NL*
    functionValueParameters NL* (COLON NL* type)? NL* typeConstraints? NL* functionBody?
    ;   

functionValueParameters
    : LPAREN (functionValueParameter (COMMA functionValueParameter)*)? RPAREN
    ;

functionValueParameter
    : (annotations | parameterModifier)* parameter (ASSIGNMENT expression)?
    ;

parameter
    : simpleIdentifier NL* COLON type
    ;

functionBody
    : block 
    | ASSIGNMENT NL* (expression | assignment)
    ;

objectDeclaration
    : OBJECT NL* simpleIdentifier? NL* primaryConstructor? NL* delegationSpecifiers? NL* classBody?
    ;

propertyDeclaration
    : (VAL | VAR) NL* typeParameters? (NL* type NL* DOT)? NL*
    (multiVariableDeclaration | variableDeclaration) NL* typeConstraints?
    (NL* (BY | ASSIGNMENT) NL* expression)? semi? (getter? NL* setter? | setter? NL* getter?)
    ;

multiVariableDeclaration
    : LPAREN variableDeclaration (COMMA variableDeclaration)* RPAREN
    ;
 
variableDeclaration
    : annotations* simpleIdentifier (COLON type)?
    ;

getter
    : (annotations | visibilityModifier | PROTECTED NL*)* GETTER NL*
    | (annotations | visibilityModifier | PROTECTED NL*)* GETTER NL* LPAREN RPAREN (NL* COLON NL* type)? NL* functionBody
    ;

setter
    : (annotations | visibilityModifier | PROTECTED NL*)* SETTER NL*
    | (annotations | visibilityModifier | PROTECTED NL*)* SETTER NL* LPAREN (annotations | parameterModifier)*
      (simpleIdentifier | parameter) RPAREN NL* functionBody
    ;

typeParameters
    : LANGLE NL* typeParameter (NL* COMMA NL* typeParameter)* NL* RANGLE
    ;

typeParameter
    : (varianceAnnotation | REIFIED)? NL* simpleIdentifier (NL* COLON NL* userType)?
    //varianceAnnotation for classes and interfaces
    //reifiedModifier for inline functions
    ;

type:
    annotations* (parenthesizedType | nullableType | typeReference) (NL* DOT NL* functionType)?
    ;

parenthesizedType
    : LPAREN type RPAREN
    ;

nullableType
    : typeReference NL* QUEST+
    ;

typeReference
    : parenthesizedTypeReference
    | userType
    | functionType
    | DYNAMIC
    ;

parenthesizedTypeReference
    : LPAREN typeReference RPAREN
    ;

userType
    : simpleUserType (NL* DOT NL* simpleUserType)*
    ;

simpleUserType
    : simpleIdentifier (NL* LANGLE NL* simpleUserTypeParameter (NL* COMMA NL* simpleUserTypeParameter)* NL* RANGLE)?
    ;

simpleUserTypeParameter
    :varianceAnnotation? type | MULT
    ;

varianceAnnotation
    : IN | OUT
    ;

functionType
    : LPAREN (parameter | type)? (COMMA (parameter | type))* RPAREN NL* ARROW NL* type?
    ;

typeConstraints
    : WHERE NL* typeConstraint (NL* COMMA NL* typeConstraint)*
    ;

typeConstraint
    : annotations* simpleIdentifier NL* COLON NL* type
    ;

block
    : LCURL NL* (statement semi)* statement? NL* RCURL
    ;

statement
    : declaration
    | assignment
    | expression
    ;

declaration
    : localClassDeclaration
    | localFunctionDeclaration
    | localPropertyDeclaration
    ;  

localClassDeclaration
    : (labelDefinition | annotations | ABSTRACT NL* | OPEN NL* | ANNOTATION NL* | DATA NL*)*
      classDeclaration
    ;

localFunctionDeclaration
    : (labelDefinition | annotations | TAILREC NL* | OPERATOR NL* | EXTERNAL NL*)*
      functionDeclaration
    ;

localPropertyDeclaration
    : (labelDefinition | annotations)* propertyDeclaration
    ;

assignment
    : assignableExpression assignmentOperator NL* disjunction
    ; 

expression
    : disjunction
    ;

disjunction
    : conjunction (NL* DISJ NL* conjunction)*
    ;

conjunction
    : equality (NL* CONJ NL* equality)*
    ;

equality
    : comparison (equalityOperator NL* comparison)*
    ;

comparison
    : infixOperation (comparisonOperator NL* infixOperation)*
    ;

infixOperation
    : elvisExpression (inOperator NL* elvisExpression)*
    | elvisExpression (isOperator NL* type)?
    ; 
    
elvisExpression
    : infixFunctionCall (NL* ELVIS NL* infixFunctionCall)*
    ;

infixFunctionCall
    : rangeExpression (simpleIdentifier NL* rangeExpression)*
    ;

rangeExpression
    : additiveExpression (RANGE NL* additiveExpression)*
    ;

additiveExpression
    : multiplicativeExpression (additiveOperator NL* multiplicativeExpression)*
    ;
           
multiplicativeExpression
    : asExpression (multiplicativeOperator NL* asExpression)*
    ;
 
asExpression
    : prefixUnaryExpression asExpressionTail?
    ;

asExpressionTail
    : NL* asOperator NL* type asExpressionTail?
    ;
 
prefixUnaryExpression
    : prefixUnaryOperator* postfixUnaryExpression
    | annotations* postfixUnaryExpression
    | labelDefinition postfixUnaryExpression
    ;

postfixUnaryExpression
    : assignableExpression postfixUnaryOperator*
    | (LPAREN callableReference RPAREN postfixUnaryOperator+
     | callableReference)
    ;

assignableExpression
    : primaryExpression
    | indexingExpression
    ;

indexingExpression
    : identifier arrayAccess+
    ;

callSuffix
    : typeArguments? valueArguments annotatedLambda?
    | typeArguments annotatedLambda?
    | annotatedLambda
    ;

annotatedLambda
    : unescapedAnnotation* LabelDefinition? NL* functionLiteral
    ;

arrayAccess
    : LSQUARE (expression (COMMA expression)*)? RSQUARE
    ;

valueArguments
    : LPAREN valueArgument? (COMMA valueArgument)* RPAREN
    ;

typeArguments
    : LANGLE NL* type (NL* COMMA type)* NL* RANGLE
    ;

valueArgument
    : (simpleIdentifier NL* ASSIGNMENT NL*)? MULT? NL* expression
    ;

primaryExpression
    : parenthesizedExpression
    | literalConstant
    | stringLiteral
    | identifier
    | functionLiteral
    | objectLiteral
    | thisExpression    
    | superExpression 
    | conditionalExpression
    | tryExpression
    | loopExpression
    | jumpExpression
    ;

parenthesizedExpression
    : LPAREN expression RPAREN
    ;

literalConstant
    : BooleanLiteral
    | IntegerLiteral
    | HexLiteral
    | BinLiteral
    | CharacterLiteral
    | RealLiteral
    | NullLiteral
    | LongLiteral
    ;

stringLiteral
    : lineStringLiteral
    | multiLineStringLiteral
    ;

lineStringLiteral
    : QUOTE_OPEN (lineStringContent | lineStringExpression)* QUOTE_CLOSE
    ;

multiLineStringLiteral
    : TRIPLE_QUOTE_OPEN (multiLineStringContent | multiLineStringExpression | lineStringLiteral | MultiLineStringQuote)* TRIPLE_QUOTE_CLOSE
    ;

lineStringContent
    : LineStrText 
    | LineStrEscapedChar 
    | LineStrRef 
    ;

lineStringExpression
    : LineStrExprStart expression RCURL
    ;

multiLineStringContent
    : MultiLineStrText 
    | MultiLineStrEscapedChar 
    | MultiLineStrRef
    ;

multiLineStringExpression
    : MultiLineStrExprStart expression RCURL
    ;

functionLiteral
    : annotations*
    ( LCURL NL* (statement semi)* statement? NL* RCURL
    | LCURL NL* lambdaParameter (NL* COMMA NL* lambdaParameter)* NL* ARROW NL* (statement semi)* statement? NL* RCURL )
    ;

lambdaParameter
    : variableDeclaration 
    | multiVariableDeclaration (NL* COLON NL* type)?
    ;

objectLiteral
    : OBJECT (NL* delegationSpecifiers)? NL* classBody
    ;

thisExpression
    : THIS LabelReference?
    ;
  
superExpression
    : SUPER (LANGLE NL* type NL* RANGLE)? LabelReference?
    ;

conditionalExpression
    : ifExpression 
    | whenExpression
    ;

ifExpression
    : IF NL* LPAREN expression RPAREN NL* controlStructureBody? semi?
    (ELSE NL* controlStructureBody?)?
    ;

controlStructureBody
    : block 
    | expression
    ;

whenExpression
    : WHEN NL* (LPAREN expression RPAREN)? NL* LCURL NL* (whenEntry NL*)* NL* RCURL
    ;
 
whenEntry
    : whenCondition (NL* COMMA NL* whenCondition)* NL* ARROW NL* controlStructureBody semi?
    | ELSE NL* ARROW NL* controlStructureBody
    ;
 
whenCondition
    : expression
    | rangeTest
    | typeTest
    ; 

rangeTest
    : inOperator NL* expression
    ; 

typeTest
    : isOperator NL* type
    ; 

tryExpression
    : TRY NL* block NL* (catchBlock NL*)* finallyBlock?
    ; 
 
catchBlock
    : CATCH NL* LPAREN annotations* simpleIdentifier COLON userType RPAREN NL* block
    ; 
 
finallyBlock
    : FINALLY NL* block
    ; 
 
loopExpression
    : forExpression 
    | whileExpression 
    | doWhileExpression
    ;
 
forExpression
    : FOR NL* LPAREN annotations* (variableDeclaration | multiVariableDeclaration) IN expression RPAREN NL* controlStructureBody?
    ;

whileExpression
    : WHILE NL* LPAREN expression RPAREN NL* controlStructureBody?
    ;
 
doWhileExpression
    : DO NL* controlStructureBody? NL* WHILE NL* LPAREN expression RPAREN
    ;
 
jumpExpression
    : THROW NL* expression
    | (RETURN | RETURN_AT) expression?
    | CONTINUE | CONTINUE_AT
    | BREAK | BREAK_AT
    ;

callableReference
    : (userType (QUEST NL*)*)? NL* (COLONCOLON | Q_COLONCOLON) NL* (identifier | CLASS)
    ;

assignmentOperator
    : ASSIGNMENT
    | ADD_ASSIGNMENT
    | SUB_ASSIGNMENT
    | MULT_ASSIGNMENT
    | DIV_ASSIGNMENT
    | MOD_ASSIGNMENT
    ;

equalityOperator
    : EXCL_EQ
    | EXCL_EQEQ
    | EQEQ
    | EQEQEQ
    ;

comparisonOperator
    : LANGLE
    | RANGLE
    | LE
    | GE
    ;

inOperator
    : IN | NOT_IN
    ; 

isOperator
    : IS | NOT_IS
    ; 

additiveOperator
    : ADD | SUB
    ;

multiplicativeOperator
    : MULT
    | DIV
    | MOD
    ;

asOperator
    : AS
    | AS_SAFE
    | COLON
    ;

prefixUnaryOperator
    : INCR
    | DECR
    | ADD
    | SUB
    | EXCL
    ;

postfixUnaryOperator
    : INCR | DECR | EXCL EXCL
    | NL* memberAccessOperator postfixUnaryExpression
    | callSuffix
    | arrayAccess
    ;

memberAccessOperator
    : DOT | QUEST DOT
    ;

visibilityModifier
    : (PUBLIC
    | PRIVATE
    | INTERNAL) NL*
    ;

classModifier
    : (SEALED
    | ANNOTATION
    | DATA) NL*
    ;

functionModifier
    : (TAILREC
    | OPERATOR
    | INLINE
    | EXTERNAL
    | SUSPEND) NL*
    ;

inheritanceModifier
    : (ABSTRACT
    | FINAL
    | OPEN) NL*
    ;
 
parameterModifier
    : (VARARG
    | NOINLINE
    | CROSSINLINE) NL*
    ;

labelDefinition
    : LabelDefinition NL*
    ;

annotations
    : (annotation | annotationList) NL*
    ;

annotation
    : annotationUseSiteTarget NL* COLON NL* unescapedAnnotation
    | LabelReference (NL* typeArguments)? (NL* valueArguments)?
    ;

annotationList
    : annotationUseSiteTarget COLON LSQUARE unescapedAnnotation+ RSQUARE
    | AT LSQUARE unescapedAnnotation+ RSQUARE
    ;
 
annotationUseSiteTarget
    : FIELD
    | FILE
    | PROPERTY
    | GET
    | SET
    | RECEIVER
    | PARAM
    | SETPARAM
    | DELEGATE
    ;

unescapedAnnotation
    : identifier typeArguments? valueArguments?
    ;

identifier
    : simpleIdentifier (NL* DOT simpleIdentifier)*
    ;

simpleIdentifier
    : Identifier
    //soft keywords:
    | ABSTRACT
    | ANNOTATION
    | BY
    | CATCH
    | COMPANION
    | CONSTRUCTOR
    | CROSSINLINE
    | DATA
    | DYNAMIC
    | ENUM
    | EXTERNAL
    | FINAL
    | FINALLY
    | GETTER
    | IMPORT
    | INFIX
    | INIT
    | INLINE
    | INNER
    | INTERNAL
    | LATEINIT
    | NOINLINE
    | OPEN
    | OPERATOR
    | OUT
    | OVERRIDE
    | PRIVATE
    | PROTECTED
    | PUBLIC
    | REIFIED
    | SEALED
    | TAILREC
    | SETTER
    | VARARG
    | WHERE
    ;

semi: NL+ | SEMICOLON | SEMICOLON NL+;
