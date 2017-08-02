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
LSQUARE: '[' -> pushMode(Inside);
LCURL: '{' -> pushMode(DEFAULT_MODE) ;
RCURL: '}' -> popMode ;
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
    : (Letter | '_') (Letter | '_' | IntegerLiteral)*
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

Inside_DelimitedComment
    : '/*' (DelimitedComment| .)*? '*/'
      -> skip
    ;

Inside_LineComment
    : '//' ~[\u000A\u000D]*
      -> skip
    ;

RPAREN: ')' -> popMode ;
RSQUARE: ']' -> popMode;

Inside_LPAREN: '(' -> pushMode(Inside), type(LPAREN) ;
Inside_LSQUARE: '[' -> pushMode(Inside), type(LSQUARE) ;

Inside_LCURL: '{' -> type(LCURL) ;
Inside_RCURL: '}' -> type(RCURL) ;
Inside_DOT: '.' -> type(DOT) ;
Inside_COMMA: ','  -> type(COMMA) ;
Inside_MULT: '*' -> type(MULT) ;
Inside_MOD: '%'  -> type(MOD) ;
Inside_DIV: '/' -> type(DIV) ;
Inside_ADD: '+'  -> type(ADD) ;
Inside_SUB: '-'  -> type(SUB) ;
Inside_INCR: '++'  -> type(INCR) ;
Inside_DECR: '--'  -> type(DECR) ;
Inside_CONJ: '&&'  -> type(CONJ) ;
Inside_DISJ: '||'  -> type(DISJ) ;
Inside_EXCL: '!'  -> type(EXCL) ;
Inside_COLON: ':'  -> type(COLON) ;
Inside_SEMICOLON: ';'  -> type(SEMICOLON) ;
Inside_ASSIGNMENT: '='  -> type(ASSIGNMENT) ;
Inside_ADD_ASSIGNMENT: '+='  -> type(ADD_ASSIGNMENT) ;
Inside_SUB_ASSIGNMENT: '-='  -> type(SUB_ASSIGNMENT) ;
Inside_MULT_ASSIGNMENT: '*='  -> type(MULT_ASSIGNMENT) ;
Inside_DIV_ASSIGNMENT: '/='  -> type(DIV_ASSIGNMENT) ;
Inside_MOD_ASSIGNMENT: '%='  -> type(MOD_ASSIGNMENT) ;
Inside_ARROW: '->'  -> type(ARROW) ;
Inside_DOUBLE_ARROW: '=>'  -> type(DOUBLE_ARROW) ;
Inside_RANGE: '..'  -> type(RANGE) ;
Inside_COLONCOLON: '::'  -> type(COLONCOLON) ;
Inside_Q_COLONCOLON: '?::' -> type(Q_COLONCOLON) ;
Inside_DOUBLE_SEMICOLON: ';;'  -> type(DOUBLE_SEMICOLON) ;
Inside_HASH: '#'  -> type(HASH) ;
Inside_AT: '@'  -> type(AT) ;
Inside_QUEST: '?'  -> type(QUEST) ;
Inside_ELVIS: '?:'  -> type(ELVIS) ;
Inside_LANGLE: '<'  -> type(LANGLE) ;
Inside_RANGLE: '>'  -> type(RANGLE) ;
Inside_LE: '<='  -> type(LE) ;
Inside_GE: '>='  -> type(GE) ;
Inside_EXCL_EQ: '!='  -> type(EXCL_EQ) ;
Inside_EXCL_EQEQ: '!=='  -> type(EXCL_EQEQ) ;
Inside_NOT_IS: '!is' (NL | WS) -> type(NOT_IS) ;
Inside_NOT_IN: '!in' (NL | WS) -> type(NOT_IN) ;
Inside_AS_SAFE: 'as?'  -> type(AS_SAFE) ;
Inside_EQEQ: '=='  -> type(EQEQ) ;
Inside_EQEQEQ: '==='  -> type(EQEQEQ) ;
Inside_SINGLE_QUOTE: '\''  -> type(SINGLE_QUOTE) ;
Inside_QUOTE_OPEN: '"' -> pushMode(LineString), type(QUOTE_OPEN) ;
Inside_TRIPLE_QUOTE_OPEN: '"""' -> pushMode(MultiLineString), type(TRIPLE_QUOTE_OPEN) ;

Inside_VAL: 'val' -> type(VAL) ;
Inside_VAR: 'var' ->type(VAR) ;
Inside_VARARG: 'vararg' -> type(VARARG) ;
Inside_NOINLINE: 'noinline' -> type(NOINLINE) ;
Inside_CROSSINLINE: 'crossinline' -> type(CROSSINLINE) ;
Inside_REIFIED: 'reified' -> type(REIFIED) ;
Inside_OVERRIDE: 'override' -> type(OVERRIDE) ;
Inside_IN: 'in' -> type(IN) ;
Inside_OUT: 'out' -> type(OUT) ;
Inside_FIELD: '@field' -> type(FIELD) ;
Inside_FILE: '@file' -> type(FILE) ;
Inside_PROPERTY: '@property' -> type(PROPERTY) ;
Inside_GET: '@get' -> type(GET) ;
Inside_SET: '@set' -> type(SET) ;
Inside_RECEIVER: '@receiver' -> type(RECEIVER) ;
Inside_PARAM: '@param' -> type(PARAM) ;
Inside_SETPARAM: '@setparam' -> type(SETPARAM) ;
Inside_DELEGATE: '@delegate' -> type(DELEGATE) ;

Inside_Literal
    : BooleanLiteral
    | IntegerLiteral
    | HexLiteral
    | BinLiteral
    | CharacterLiteral
    | RealLiteral
    | NullLiteral
    | LongLiteral
    ;

Inside_Identifier
    : ((RegularIdentifier | EscapedIdentifier)
    | '`' ~('`')+ '`' ) -> type(Identifier)
    ;

Inside_LabelReference
    : '@' Identifier -> type(LabelReference)
    ;

Inside_LabelDefinition
    : Identifier '@' -> type(LabelDefinition)
    ;

Inside_Comment: (LineComment | DelimitedComment) -> channel(HIDDEN) ;
Inside_WS: [\u0020\u0009\u000C] -> skip ;
Inside_NL: ('\u000A' | '\u000D' '\u000A') -> skip ;


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
    : '${' -> pushMode(DEFAULT_MODE)
    ;


mode MultiLineString ;

TRIPLE_QUOTE_CLOSE
    : '"""' -> popMode
    ;

ToLineString
    : '"' -> type(QUOTE_OPEN), pushMode(LineString)
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
    : '${' -> pushMode(DEFAULT_MODE)
    ;

MultiLineNL: ('\u000A' | '\u000D' '\u000A') -> skip ;
