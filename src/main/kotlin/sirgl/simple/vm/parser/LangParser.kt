package sirgl.simple.vm.parser

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangPackageDecl
import sirgl.simple.vm.ast.impl.LangPackageDeclImpl
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.lexer.LexemeKind
import sirgl.simple.vm.lexer.LexemeKind.*

interface LangParser {
    fun parse(lexemes: List<Lexeme>): LangFile

    fun parseExpr(lexemes: List<Lexeme>): LangExpr
}

class HandwrittenLangParser : LangParser {
    override fun parseExpr(lexemes: List<Lexeme>): LangExpr {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parse(lexemes: List<Lexeme>): LangFile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


class ParseException(val lexeme: Lexeme, message: String) : Exception(message)

/**
 * No error recovery provided. Fails on first error.
 */
private class ParserState(val lexemes: List<Lexeme>) {
    private var position = 0
    private val current: Lexeme
        get() = lexemes[position]

    fun parse(): LangFile = TODO()

    // Utils

    private fun matchThenAdvance(vararg types: LexemeKind) : Boolean {
        val matches = match()
        if (matches) {
            advance()
        }
        return matches
    }

    private fun advance(): Lexeme {
        if (position > lexemes.size) throw IndexOutOfBoundsException()
        val v = current
        position++
        return v
    }

    private fun match(vararg types: LexemeKind) = types.any {
        it == current.kind
    }

    private fun expectThenAdvance(vararg types: LexemeKind): Lexeme {
        if(!match()) {
            val message = when {
                types.isEmpty() -> throw IllegalStateException()
                types.size == 1 -> "Expected ${types.first()}"
                else -> "Expected any of ${types.joinToString(", ")}"
            }
            fail(message)
        }
        return advance()
    }

    private fun fail(message: String): Unit = throw ParseException(current, message)

    // Rules

    fun parseFile(): LangFile {
        val packageDecl = parsePackageDecl()
        TODO()
    }

    fun parseExpr(): LangExpr = TODO()

    fun parsePackageDecl(): LangPackageDecl {
        val packageLexeme = expectThenAdvance(Package)

        val packageName = separatedList(Identifier, Dot).joinToString(".") { it.text }
        val semicolonLexeme = expectThenAdvance(Semicolon)
        return LangPackageDeclImpl(packageName, packageLexeme, semicolonLexeme)
    }

    fun separatedList(entryKind: LexemeKind, separatorKind: LexemeKind) : List<Lexeme> {
        val lexemes = mutableListOf<Lexeme>()
        lexemes.add(expectThenAdvance(entryKind))
        while (!match(separatorKind)) {
            advance()
            lexemes.add(expectThenAdvance(entryKind))
        }
        return lexemes
    }
}