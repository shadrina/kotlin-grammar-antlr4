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

lexer grammar KotlinLexer;

import UnicodeClasses;

ShebangLine
    : '#!' ~[\u000A\u000D]*
      -> channel(HIDDEN)
    ;

DelimitedComment
    : '/*' ( DelimitedComment | . )*? '*/'
      -> channel(HIDDEN)
    ;

LineComment
    : '//' ~[\u000A\u000D]*
      -> channel(HIDDEN)
    ;

WS
    : [\u0020\u0009\u000C]
      -> skip
    ;

NL: '\u000A' | '\u000D' '\u000A' ;

//SEPARATORS & OPERATIONS

RESERVED: '...' ;
DOT: '.' ;
COMMA: ',' ;
LPAREN: '(' -> pushMode(Inside) ;
LSQUARE: '[' -> pushMode(Inside) ;
LCURL: '{' ;
RCURL: '}' ;
MULT: '*' ;
MOD: '%' ;
DIV: '/' ;
ADD: '+' ;
SUB: '-' ;
INCR: '++' ;
DECR: '--' ;
CONJ: '&&' ;
DISJ: '||' ;
EXCL: '!' ;
COLON: ':' ;
SEMICOLON: ';' ;
ASSIGNMENT: '=' ;
ADD_ASSIGNMENT: '+=' ;
SUB_ASSIGNMENT: '-=' ;
MULT_ASSIGNMENT: '*=' ;
DIV_ASSIGNMENT: '/=' ;
MOD_ASSIGNMENT: '%=' ;
ARROW: '->' ;
DOUBLE_ARROW: '=>' ;
RANGE: '..' ;
COLONCOLON: '::' ;
Q_COLONCOLON: '?::' ;
DOUBLE_SEMICOLON: ';;' ;
HASH: '#' ;
AT: '@' ;
QUEST: '?' ;
ELVIS: '?:' ;
LANGLE: '<' ;
RANGLE: '>' ;
LE: '<=' ;
GE: '>=' ;
EXCL_EQ: '!=' ;
EXCL_EQEQ: '!==' ;
AS_SAFE: 'as?' ;
EQEQ: '==' ;
EQEQEQ: '===' ;
SINGLE_QUOTE: '\'' ;

//KEYWORDS

RETURN_AT: 'return@' Identifier ;
CONTINUE_AT: 'continue@' Identifier ;
BREAK_AT: 'break@' Identifier ;

FILE: '@file' ;
PACKAGE: 'package' ;
IMPORT: 'import' ;
CLASS: 'class' ;
INTERFACE: 'interface' ;
FUN: 'fun' ;
OBJECT: 'object' ;
VAL: 'val' ;
VAR: 'var' ;
TYPE_ALIAS: 'typealias' ;
CONSTRUCTOR: 'constructor' ;
BY: 'by' ;
COMPANION: 'companion' ;
INIT: 'init'  ;
THIS: 'this' ;
SUPER: 'super' ;
TYPEOF: 'typeof' ;
WHERE: 'where' ;
IF: 'if' ;
ELSE: 'else' ;
WHEN: 'when' ;
TRY: 'try' ;
CATCH: 'catch' ;
FINALLY: 'finally' ;
FOR: 'for' ;
DO: 'do' ;
WHILE: 'while' ;
THROW: 'throw' ;
RETURN: 'return' ;
CONTINUE: 'continue' ;
BREAK: 'break' ;
AS: 'as' ;
IS: 'is' ;
IN: 'in' ;
NOT_IS: '!is' (WS | NL)+ ;
NOT_IN: '!in' (WS | NL)+ ;
OUT: 'out' ;
FIELD: '@field' ;
PROPERTY: '@property' ;
GET: '@get' ;
SET: '@set' ;
GETTER: 'get' ;
SETTER: 'set' ;
RECEIVER: '@receiver' ;
PARAM: '@param' ;
SETPARAM: '@setparam' ;
DELEGATE: '@delegate' ;
DYNAMIC: 'dynamic' ;

//MODIFIERS

PUBLIC: 'public' ;
PRIVATE: 'private' ;
PROTECTED: 'protected' ;
INTERNAL: 'internal' ;
ENUM: 'enum' ;
SEALED: 'sealed' ;
ANNOTATION: 'annotation' ;
DATA: 'data' ;
INNER: 'inner' ;
TAILREC: 'tailrec' ;
OPERATOR: 'operator' ;
INLINE: 'inline' ;
INFIX: 'infix' ;
EXTERNAL: 'external' ;
SUSPEND: 'suspend' ;
OVERRIDE: 'override' ;
ABSTRACT: 'abstract' ;
FINAL: 'final' ;
OPEN: 'open' ;
CONST: 'const' ;
LATEINIT: 'lateinit' ;
VARARG: 'vararg' ;
NOINLINE: 'noinline' ;
CROSSINLINE: 'crossinline' ;
REIFIED: 'reified' ;

//

QUOTE_OPEN: '"' -> pushMode(LineString) ;
TRIPLE_QUOTE_OPEN: '"""' -> pushMode(MultiLineString) ;

RealLiteral
    : FloatLiteral
    | DoubleLiteral
    ;

FloatLiteral
    : (DoubleLiteral | IntegerLiteral) [fF]
    ;

DoubleLiteral
    : '-'?
    ( (DecDigitNoZero DecDigit*)? '.'
     | (DecDigitNoZero (DecDigit | '_')* DecDigit)? '.')
    ( DecDigit+
     | DecDigit (DecDigit | '_')+ DecDigit
     | DecDigit+ [eE] ('+' | '-')? DecDigit+
     | DecDigit+ [eE] ('+' | '-')? DecDigit (DecDigit | '_')+ DecDigit
     | DecDigit (DecDigit | '_')+ DecDigit [eE] ('+' | '-')? DecDigit+
     | DecDigit (DecDigit | '_')+ DecDigit [eE] ('+' | '-')? DecDigit (DecDigit | '_')+ DecDigit)
    ;

LongLiteral
    : (IntegerLiteral | HexLiteral | BinLiteral) 'L'
    ;

IntegerLiteral
    : '-'?
    ('0'
    | DecDigitNoZero DecDigit*
    | DecDigitNoZero (DecDigit | '_')+ DecDigit
    | DecDigitNoZero DecDigit* [eE] ('+' | '-')? DecDigit+
    | DecDigitNoZero DecDigit* [eE] ('+' | '-')? DecDigit (DecDigit | '_')+ DecDigit
    | DecDigitNoZero (DecDigit | '_')+ DecDigit [eE] ('+' | '-')? DecDigit+
    | DecDigitNoZero (DecDigit | '_')+ DecDigit [eE] ('+' | '-')? DecDigit (DecDigit | '_')+ DecDigit )
    ;

fragment DecDigit
    : UNICODE_CLASS_ND
    ;

fragment DecDigitNoZero
    : UNICODE_CLASS_ND_NoZeros
    ;

fragment UNICODE_CLASS_ND_NoZeros
	: '\u0031'..'\u0039'
	| '\u0661'..'\u0669'
	| '\u06f1'..'\u06f9'
	| '\u07c1'..'\u07c9'
	| '\u0967'..'\u096f'
	| '\u09e7'..'\u09ef'
	| '\u0a67'..'\u0a6f'
	| '\u0ae7'..'\u0aef'
	| '\u0b67'..'\u0b6f'
	| '\u0be7'..'\u0bef'
	| '\u0c67'..'\u0c6f'
	| '\u0ce7'..'\u0cef'
	| '\u0d67'..'\u0d6f'
	| '\u0de7'..'\u0def'
	| '\u0e51'..'\u0e59'
	| '\u0ed1'..'\u0ed9'
	| '\u0f21'..'\u0f29'
	| '\u1041'..'\u1049'
	| '\u1091'..'\u1099'
	| '\u17e1'..'\u17e9'
	| '\u1811'..'\u1819'
	| '\u1947'..'\u194f'
	| '\u19d1'..'\u19d9'
	| '\u1a81'..'\u1a89'
	| '\u1a91'..'\u1a99'
	| '\u1b51'..'\u1b59'
	| '\u1bb1'..'\u1bb9'
	| '\u1c41'..'\u1c49'
	| '\u1c51'..'\u1c59'
	| '\ua621'..'\ua629'
	| '\ua8d1'..'\ua8d9'
	| '\ua901'..'\ua909'
	| '\ua9d1'..'\ua9d9'
	| '\ua9f1'..'\ua9f9'
	| '\uaa51'..'\uaa59'
	| '\uabf1'..'\uabf9'
	| '\uff11'..'\uff19'
	;

HexLiteral
    : '0' [xX] HexDigit (HexDigit | '_')*
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

BinLiteral
    : '0' [bB] BinDigit (BinDigit | '_')*
    ;

fragment BinDigit
    : [01]
    ;

BooleanLiteral
    : 'true'
    | 'false'
    ;

NullLiteral
    : 'null'
    ;

Identifier
    : (RegularIdentifier | EscapedIdentifier)
    | '`' ~('`')+ '`'
    ;

fragment RegularIdentifier
    : (Letter | '_') (Letter | '_' | DecDigit)*
    ;

fragment EscapedIdentifier
    : '\\' ('t' | 'b' | 'r' | 'n' | '\'' | '"' | '\\' | '$')
    ;

LabelReference
    : '@' Identifier
    ;

LabelDefinition
    : Identifier '@'
    ;

FieldIdentifier
    : '$' Identifier
    ;

CharacterLiteral
    : '\'' (EscapeSeq | .) '\''
    ;

fragment EscapeSeq
    : UniCharacterLiteral
    | EscapedIdentifier
    ;

fragment UniCharacterLiteral
    : '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment Letter
    : UNICODE_CLASS_LL
    | UNICODE_CLASS_LM
    | UNICODE_CLASS_LO
    | UNICODE_CLASS_LT
    | UNICODE_CLASS_LU
    | UNICODE_CLASS_NL
    ;


mode Inside ;

Inside_DelimitedComment: DelimitedComment -> skip ;

RPAREN: ')' -> popMode ;
RSQUARE: ']' -> popMode;

Inside_LPAREN: LPAREN -> pushMode(Inside), type(LPAREN) ;
Inside_LSQUARE: LSQUARE -> pushMode(Inside), type(LSQUARE) ;

Inside_LCURL: LCURL -> type(LCURL) ;
Inside_RCURL: RCURL -> type(RCURL) ;
Inside_DOT: DOT -> type(DOT) ;
Inside_COMMA: COMMA  -> type(COMMA) ;
Inside_MULT: MULT -> type(MULT) ;
Inside_MOD: MOD  -> type(MOD) ;
Inside_DIV: DIV -> type(DIV) ;
Inside_ADD: ADD  -> type(ADD) ;
Inside_SUB: SUB  -> type(SUB) ;
Inside_INCR: INCR  -> type(INCR) ;
Inside_DECR: DECR  -> type(DECR) ;
Inside_CONJ: CONJ  -> type(CONJ) ;
Inside_DISJ: DISJ  -> type(DISJ) ;
Inside_EXCL: EXCL  -> type(EXCL) ;
Inside_COLON: COLON  -> type(COLON) ;
Inside_SEMICOLON: SEMICOLON  -> type(SEMICOLON) ;
Inside_ASSIGNMENT: ASSIGNMENT  -> type(ASSIGNMENT) ;
Inside_ADD_ASSIGNMENT: ADD_ASSIGNMENT  -> type(ADD_ASSIGNMENT) ;
Inside_SUB_ASSIGNMENT: SUB_ASSIGNMENT  -> type(SUB_ASSIGNMENT) ;
Inside_MULT_ASSIGNMENT: MULT_ASSIGNMENT  -> type(MULT_ASSIGNMENT) ;
Inside_DIV_ASSIGNMENT: DIV_ASSIGNMENT  -> type(DIV_ASSIGNMENT) ;
Inside_MOD_ASSIGNMENT: MOD_ASSIGNMENT  -> type(MOD_ASSIGNMENT) ;
Inside_ARROW: ARROW  -> type(ARROW) ;
Inside_DOUBLE_ARROW: DOUBLE_ARROW  -> type(DOUBLE_ARROW) ;
Inside_RANGE: RANGE  -> type(RANGE) ;
Inside_COLONCOLON: COLONCOLON  -> type(COLONCOLON) ;
Inside_Q_COLONCOLON: Q_COLONCOLON -> type(Q_COLONCOLON) ;
Inside_DOUBLE_SEMICOLON: DOUBLE_SEMICOLON  -> type(DOUBLE_SEMICOLON) ;
Inside_HASH: HASH  -> type(HASH) ;
Inside_AT: AT  -> type(AT) ;
Inside_QUEST: QUEST  -> type(QUEST) ;
Inside_ELVIS: ELVIS  -> type(ELVIS) ;
Inside_LANGLE: LANGLE  -> type(LANGLE) ;
Inside_RANGLE: RANGLE  -> type(RANGLE) ;
Inside_LE: LE  -> type(LE) ;
Inside_GE: GE  -> type(GE) ;
Inside_EXCL_EQ: EXCL_EQ  -> type(EXCL_EQ) ;
Inside_EXCL_EQEQ: EXCL_EQEQ  -> type(EXCL_EQEQ) ;
Inside_NOT_IS: NOT_IS -> type(NOT_IS) ;
Inside_NOT_IN: NOT_IN -> type(NOT_IN) ;
Inside_AS_SAFE: AS_SAFE  -> type(AS_SAFE) ;
Inside_EQEQ: EQEQ  -> type(EQEQ) ;
Inside_EQEQEQ: EQEQEQ  -> type(EQEQEQ) ;
Inside_SINGLE_QUOTE: SINGLE_QUOTE  -> type(SINGLE_QUOTE) ;
Inside_QUOTE_OPEN: QUOTE_OPEN -> pushMode(LineString), type(QUOTE_OPEN) ;
Inside_TRIPLE_QUOTE_OPEN: TRIPLE_QUOTE_OPEN -> pushMode(MultiLineString), type(TRIPLE_QUOTE_OPEN) ;

Inside_VAL: VAL -> type(VAL) ;
Inside_VAR: VAR ->type(VAR) ;
Inside_VARARG: VARARG -> type(VARARG) ;
Inside_NOINLINE: NOINLINE -> type(NOINLINE) ;
Inside_CROSSINLINE: CROSSINLINE -> type(CROSSINLINE) ;
Inside_REIFIED: REIFIED -> type(REIFIED) ;
Inside_OVERRIDE: OVERRIDE -> type(OVERRIDE) ;
Inside_IN: IN -> type(IN) ;
Inside_OUT: OUT -> type(OUT) ;
Inside_FIELD: FIELD -> type(FIELD) ;
Inside_FILE: FILE -> type(FILE) ;
Inside_PROPERTY: PROPERTY -> type(PROPERTY) ;
Inside_GET: GET -> type(GET) ;
Inside_SET: SET -> type(SET) ;
Inside_RECEIVER: RECEIVER -> type(RECEIVER) ;
Inside_PARAM: PARAM -> type(PARAM) ;
Inside_SETPARAM: SETPARAM -> type(SETPARAM) ;
Inside_DELEGATE: DELEGATE -> type(DELEGATE) ;

Inside_BooleanLiteral: BooleanLiteral -> type(BooleanLiteral) ;
Inside_IntegerLiteral: IntegerLiteral -> type(IntegerLiteral) ;
Inside_HexLiteral: HexLiteral -> type(HexLiteral) ;
Inside_BinLiteral: BinLiteral -> type(BinLiteral) ;
Inside_CharacterLiteral: CharacterLiteral -> type(CharacterLiteral) ;
Inside_RealLiteral: RealLiteral -> type(RealLiteral) ;
Inside_NullLiteral: NullLiteral -> type(NullLiteral) ;
Inside_LongLiteral: LongLiteral -> type(LongLiteral) ;

Inside_Identifier: Identifier -> type(Identifier) ;
Inside_LabelReference: LabelReference -> type(LabelReference) ;
Inside_LabelDefinition: LabelDefinition -> type(LabelDefinition) ;
Inside_Comment: (LineComment | DelimitedComment) -> channel(HIDDEN) ;
Inside_WS: WS -> skip ;
Inside_NL: NL -> skip ;


mode LineString ;

QUOTE_CLOSE
    : '"' -> popMode
    ;

LineStrRef
    : FieldIdentifier
    ;

LineStrText
    : ~('\\' | '"' | '$')+ | '$'
    ;

LineStrEscapedChar
    : '\\' .
    | UniCharacterLiteral
    ;

LineStrExprStart
    : '${' -> pushMode(StringExpression)
    ;


mode MultiLineString ;

TRIPLE_QUOTE_CLOSE
    : MultiLineStringQuote? '"""' -> popMode
    ;

MultiLineStringQuote
    : '"'+
    ;

MultiLineStrRef
    : FieldIdentifier
    ;

MultiLineStrText
    :  ~('\\' | '"' | '$')+ | '$'
    ;

MultiLineStrEscapedChar
    : '\\' .
    ;

MultiLineStrExprStart
    : '${' -> pushMode(StringExpression)
    ;

MultiLineNL: NL -> skip ;


mode StringExpression ;

StrExpr_RCURL: RCURL -> popMode, type(RCURL) ;

StrExpr_DelimitedComment: DelimitedComment -> skip ;

StrExpr_LPAREN: LPAREN -> pushMode(Inside), type(LPAREN) ;
StrExpr_LSQUARE: LSQUARE -> pushMode(Inside), type(LSQUARE) ;

StrExpr_LCURL: LCURL -> type(LCURL) ;
StrExpr_DOT: DOT -> type(DOT) ;
StrExpr_COMMA: COMMA  -> type(COMMA) ;
StrExpr_MULT: MULT -> type(MULT) ;
StrExpr_MOD: MOD  -> type(MOD) ;
StrExpr_DIV: DIV -> type(DIV) ;
StrExpr_ADD: ADD  -> type(ADD) ;
StrExpr_SUB: SUB  -> type(SUB) ;
StrExpr_INCR: INCR  -> type(INCR) ;
StrExpr_DECR: DECR  -> type(DECR) ;
StrExpr_CONJ: CONJ  -> type(CONJ) ;
StrExpr_DISJ: DISJ  -> type(DISJ) ;
StrExpr_EXCL: EXCL  -> type(EXCL) ;
StrExpr_COLON: COLON  -> type(COLON) ;
StrExpr_SEMICOLON: SEMICOLON  -> type(SEMICOLON) ;
StrExpr_ASSIGNMENT: ASSIGNMENT  -> type(ASSIGNMENT) ;
StrExpr_ADD_ASSIGNMENT: ADD_ASSIGNMENT  -> type(ADD_ASSIGNMENT) ;
StrExpr_SUB_ASSIGNMENT: SUB_ASSIGNMENT  -> type(SUB_ASSIGNMENT) ;
StrExpr_MULT_ASSIGNMENT: MULT_ASSIGNMENT  -> type(MULT_ASSIGNMENT) ;
StrExpr_DIV_ASSIGNMENT: DIV_ASSIGNMENT  -> type(DIV_ASSIGNMENT) ;
StrExpr_MOD_ASSIGNMENT: MOD_ASSIGNMENT  -> type(MOD_ASSIGNMENT) ;
StrExpr_ARROW: ARROW  -> type(ARROW) ;
StrExpr_DOUBLE_ARROW: DOUBLE_ARROW  -> type(DOUBLE_ARROW) ;
StrExpr_RANGE: RANGE  -> type(RANGE) ;
StrExpr_COLONCOLON: COLONCOLON  -> type(COLONCOLON) ;
StrExpr_Q_COLONCOLON: Q_COLONCOLON -> type(Q_COLONCOLON) ;
StrExpr_DOUBLE_SEMICOLON: DOUBLE_SEMICOLON  -> type(DOUBLE_SEMICOLON) ;
StrExpr_HASH: HASH  -> type(HASH) ;
StrExpr_AT: AT  -> type(AT) ;
StrExpr_QUEST: QUEST  -> type(QUEST) ;
StrExpr_ELVIS: ELVIS  -> type(ELVIS) ;
StrExpr_LANGLE: LANGLE  -> type(LANGLE) ;
StrExpr_RANGLE: RANGLE  -> type(RANGLE) ;
StrExpr_LE: LE  -> type(LE) ;
StrExpr_GE: GE  -> type(GE) ;
StrExpr_EXCL_EQ: EXCL_EQ  -> type(EXCL_EQ) ;
StrExpr_EXCL_EQEQ: EXCL_EQEQ  -> type(EXCL_EQEQ) ;
StrExpr_AS: AS -> type(IS) ;
StrExpr_IS: IS -> type(IN) ;
StrExpr_IN: IN ;
StrExpr_NOT_IS: NOT_IS -> type(NOT_IS) ;
StrExpr_NOT_IN: NOT_IN -> type(NOT_IN) ;
StrExpr_AS_SAFE: AS_SAFE  -> type(AS_SAFE) ;
StrExpr_EQEQ: EQEQ  -> type(EQEQ) ;
StrExpr_EQEQEQ: EQEQEQ  -> type(EQEQEQ) ;
StrExpr_SINGLE_QUOTE: SINGLE_QUOTE  -> type(SINGLE_QUOTE) ;
StrExpr_QUOTE_OPEN: QUOTE_OPEN -> pushMode(LineString), type(QUOTE_OPEN) ;
StrExpr_TRIPLE_QUOTE_OPEN: TRIPLE_QUOTE_OPEN -> pushMode(MultiLineString), type(TRIPLE_QUOTE_OPEN) ;

StrExpr_BooleanLiteral: BooleanLiteral -> type(BooleanLiteral) ;
StrExpr_IntegerLiteral: IntegerLiteral -> type(IntegerLiteral) ;
StrExpr_HexLiteral: HexLiteral -> type(HexLiteral) ;
StrExpr_BinLiteral: BinLiteral -> type(BinLiteral) ;
StrExpr_CharacterLiteral: CharacterLiteral -> type(CharacterLiteral) ;
StrExpr_RealLiteral: RealLiteral -> type(RealLiteral) ;
StrExpr_NullLiteral: NullLiteral -> type(NullLiteral) ;
StrExpr_LongLiteral: LongLiteral -> type(LongLiteral) ;

StrExpr_Identifier: Identifier -> type(Identifier) ;
StrExpr_LabelReference: LabelReference -> type(LabelReference) ;
StrExpr_LabelDefinition: LabelDefinition -> type(LabelDefinition) ;
StrExpr_Comment: (LineComment | DelimitedComment) -> channel(HIDDEN) ;
StrExpr_WS: WS -> skip ;
StrExpr_NL: NL -> skip ;
