package sirgl.simple.vm.parser

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.impl.*
import sirgl.simple.vm.ast.impl.stmt.LangStmtImpl
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.lexer.LexemeKind
import sirgl.simple.vm.lexer.LexemeKind.*
import sirgl.simple.vm.scope.ScopeImpl
import sirgl.simple.vm.type.*

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

    fun parse(): LangFile = parseFile()

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

    private fun fail(message: String): Nothing = throw ParseException(current, message)

    // Rules

    fun parseFile(): LangFileImpl {
        val packageDecl = if (match(Package)) parsePackageDecl() else null
        val cls = parseClass()
        val file = LangFileImpl(packageDecl, cls)
        packageDecl?.parent = file
        cls.parent = file
        return file
    }

    fun parseClass(): LangClassImpl {
        val clsNode = expectThenAdvance(Class)
        val clsNameNode = expectThenAdvance(Identifier)
        val parentClsName = if (match(Colon)) {
            advance()
            expectThenAdvance(Identifier)
        } else {
            null
        }
        expectThenAdvance(LBrace)
        val members = mutableListOf<LangMember>()
        loop@
        while (true) {
            val member: LangMember = when (current.kind) {
                Fun, Native -> method()
                Var -> field()
                RParen -> break@loop
                else -> fail("Class member expected")
            }
            members.add(member)
        }
        val rBrace = expectThenAdvance(RBrace)
        val cls = LangClassImpl(ScopeImpl(), clsNameNode.text, members, clsNode, rBrace, parentClsName?.text)
        for (member in members) {
            when (member) {
                is LangMethodImpl -> member.parent = cls
                is LangFieldImpl -> member.parent = cls
            }
        }
        return cls
    }

    fun method() : LangMethodImpl {
        val nativeLexeme = if (match(Native)) {
            advance()
        } else {
            null
        }
        val funLexeme = expectThenAdvance(Fun)
        val methodNameLexeme = expectThenAdvance(Identifier)
        val parameters = parameters()
        val block = block()
        val method = LangMethodImpl(
                ScopeImpl(),
                methodNameLexeme.text,
                parameters.toTypedArray(),
                block,
                nativeLexeme ?: funLexeme,
                block.rBrace,
                nativeLexeme != null
        )
        for (parameter in parameters) {
            parameter.parent = method
        }
        block.parent = method
        return method
    }

    fun parameters() : List<LangParameterImpl> {
        expectThenAdvance(LParen)
        val parameters = mutableListOf<LangParameterImpl>()
        while (!match(RParen)) {
            parameters.add(parameter())
            matchThenAdvance(Comma)
        }
        advance()
        return parameters
    }

    fun parameter() : LangParameterImpl {
        val identifier = expectThenAdvance(Identifier)
        expectThenAdvance(Colon)
        val type = type()
        return LangParameterImpl(identifier.text, type, identifier, lexemes[position - 1])
    }

    fun type() : LangType {
        val simpleType = when (current.kind) {
            Identifier -> ClassType(current.text)
            I32 -> I32Type
            I8 -> I8Type
            Bool -> BoolType
            Void -> VoidType
            else -> fail("Type expected")
        }
        var currentType = simpleType
        while (match(LBracket)) {
            advance()
            expectThenAdvance(RBracket)
            currentType = ArrayType(currentType)
        }
        return currentType
    }

    fun block(): LangBlockImpl {
        val lBrace = expectThenAdvance(LBrace)
        while (!match(RBrace)) {

        }
        val rBrace = advance()
        TODO()
    }

    fun field(): LangFieldImpl {
        TODO()
    }

    fun stmt(): LangStmtImpl {
        TODO()
    }

    fun expr(): LangExpr = TODO()

    fun parsePackageDecl(): LangPackageDeclImpl {
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