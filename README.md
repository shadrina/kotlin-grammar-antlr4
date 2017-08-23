ing# Kotlin ANTLR4 grammar

## Project description
ANTLR4 grammar for Kotlin written only in ANTLR's special syntax. Developed to facilitate the implementation of the language in other projects.  

The primary files are in the **grammars** folder:
* **KotlinParser.g4** - parser rules
* **KotlinLexer.g4** - lexer rules
* **UnicodeClasses.g4** - lexer grammar containing made Unicode classes from [the source](http://www.antlr3.org/grammar/1345144569663/AntlrUnicode.txt)  

Lexer and parser grammars are divided for the use of lexer modes.

## Issues and limitations
All issues are described in TODO.txt.

## Testing
After generating lexer and parser with ANTLR4 tool you can test the grammar running **testing\Test.java**. Test data can be taken from the [JetBrains's repo](https://github.com/JetBrains/kotlin/tree/master/compiler/testData/psi).   
Some other usages of generated files are in the **examples** folder.

## Links
* [EBNF Kotlin grammar](http://kotlinlang.org/docs/reference/grammar.html)
* [Kotlin specification](http://jetbrains.github.io/kotlin-spec/)

## License
Licensed under the Apache 2.0

## Contacts
Anastasiya Shadrina a.shadrina5@mail.ru 
