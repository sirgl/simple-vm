package sirgl.simple.vm.parser

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.expr.PrefixOperatorType
import sirgl.simple.vm.ast.ext.parseLiteral
import sirgl.simple.vm.ast.impl.*
import sirgl.simple.vm.ast.impl.expr.*
import sirgl.simple.vm.ast.impl.stmt.LangContinueStmtImpl
import sirgl.simple.vm.ast.impl.stmt.LangExprStmtImpl
import sirgl.simple.vm.ast.impl.stmt.LangReturnStmtImpl
import sirgl.simple.vm.ast.impl.stmt.LangStmtImpl
import sirgl.simple.vm.ast.stmt.LangExprStmt
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.lexer.LexemeKind
import sirgl.simple.vm.lexer.LexemeKind.*
import sirgl.simple.vm.scope.ScopeImpl
import sirgl.simple.vm.type.*

interface LangParser {
    fun parse(lexemes: List<Lexeme>): ParseResult<LangFileImpl>

    fun parseExpr(lexemes: List<Lexeme>): LangExpr
}

class ParseResult<out T: AstNode>(
        val ast: T?,
        val fail: Fail? = null
)

class Fail(
        val lexeme: Lexeme,
        val message: String?,
        val parseException: ParseException? = null
) {
    override fun toString(): String {
        return "Parser error #${lexeme.line}@[${lexeme.startOffset}, ${lexeme.endOffset}) : $message, " +
                "but lexeme was ${lexeme.kind} with text \"${lexeme.text}\""
    }

    fun toStringWithStackTrace(): String {
        val stackTrace = buildString {
            for (el in parseException!!.stackTrace) {
                append("\t").append(el).append("\n")
            }
        }
        return "Parser error #${lexeme.line}@[${lexeme.startOffset}, ${lexeme.endOffset}) : $message, " +
                "but lexeme was ${lexeme.kind} with text \"${lexeme.text}\""  + "\n" + stackTrace
    }
}

class HandwrittenLangParser : LangParser {
    override fun parseExpr(lexemes: List<Lexeme>) = ParserState(lexemes, prefixParsers, infixOperators).expr()

    override fun parse(lexemes: List<Lexeme>) = try {
        ParseResult(ParserState(lexemes, prefixParsers, infixOperators).file())
    } catch (e: ParseException) {
        ParseResult(null, Fail(e.lexeme, e.message, e))
    }
}

private val opPrefixParser = OpPrefixParser()
private val boolLiteralParser = BoolParser()

private val prefixParsers = mapOf(
        IntLiteral to IntLiteralParser(),
        StringLiteral to StringLiteralParser(),
        Identifier to ReferenceExprParser(),
        OpPlus to opPrefixParser,
        OpMinus to opPrefixParser,
        OpExcl to opPrefixParser,
        True to boolLiteralParser,
        False to boolLiteralParser
)

private val infixOperators: Map<LexemeKind, InfixExprParser> = mapOf(
        OpPlus to BinaryExprParser()
)


class ParseException(val lexeme: Lexeme, message: String) : Exception(message)

/**
 * No error recovery provided. Fails on first error.
 */
private class ParserState(
        val lexemes: List<Lexeme>,
        private val prefixParsers: Map<LexemeKind, PrefixParser>,
        private val infixParsers: Map<LexemeKind, InfixExprParser>
) {
    private var position = 0
    private val current: Lexeme
        get() = lexemes[position]

    fun parse(): LangFile = file()

    // Utils

    fun matchThenAdvance(vararg types: LexemeKind): Boolean {
        val matches = match(*types)
        if (matches) {
            advance()
        }
        return matches
    }

    fun advance(): Lexeme {
        if (position > lexemes.size) throw IndexOutOfBoundsException()
        val v = current
        position++
        return v
    }

    fun match(vararg types: LexemeKind) = types.any {
        it == current.kind
    }

    fun expectThenAdvance(vararg types: LexemeKind): Lexeme {
        expect(types)
        return advance()
    }

    fun expect(types: Array<out LexemeKind>) {
        if (!match(*types)) {
            val message = when {
                types.isEmpty() -> throw IllegalStateException()
                types.size == 1 -> "Expected ${types.first()}"
                else -> "Expected any of ${types.joinToString(", ")}"
            }
            fail(message)
        }
    }

    fun fail(message: String): Nothing = throw ParseException(current, message)

    // Rules

    fun file(): LangFileImpl {
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
                RBrace -> break@loop
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

    fun method(): LangMethodImpl {
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

    fun parameters(): List<LangParameterImpl> {
        expectThenAdvance(LParen)
        val parameters = mutableListOf<LangParameterImpl>()
        while (!match(RParen)) {
            parameters.add(parameter())
            matchThenAdvance(Comma)
        }
        advance()
        return parameters
    }

    fun parameter(): LangParameterImpl {
        val identifier = expectThenAdvance(Identifier)
        expectThenAdvance(Colon)
        val type = type()
        return LangParameterImpl(identifier.text, type, identifier, lexemes[position - 1])
    }

    fun type(): LangType {
        val simpleType = when (current.kind) {
            Identifier -> ClassType(current.text)
            I32 -> I32Type
            I8 -> I8Type
            Bool -> BoolType
            Void -> VoidType
            else -> fail("Type expected")
        }
        advance()
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
        val stmts = mutableListOf<LangStmtImpl>()
        while (!match(RBrace)) {
            stmts.add(stmt())
        }
        val rBrace = advance()
        val block = LangBlockImpl(ScopeImpl(), stmts, lBrace, rBrace)
        for (stmt in stmts) {
            stmt.parent = block
        }
        return block
    }

    fun field(): LangFieldImpl {
        val varLexeme = expectThenAdvance(Var)
        val identifier = expectThenAdvance(Identifier)
        expectThenAdvance(Colon)
        val type = type()
        val typeLast = lexemes[position - 1]
        val initializer = if (matchThenAdvance(OpEq)) {
            expr()
        } else {
            null
        }
        expectThenAdvance(Semicolon)
        val last = if (initializer == null) typeLast else current
        return LangFieldImpl(identifier.text, type, varLexeme, last, initializer)
    }

    fun stmt() = when (current.kind) {
        Return -> returnStmt()
        Continue -> continueStmt()
        else -> exprStmt()
    }

    fun exprStmt() : LangExprStmtImpl {
        val expr = expr()
        val semi = expectThenAdvance(Semicolon)
        return LangExprStmtImpl(expr.startOffset, semi.endOffset, expr)
    }

    fun returnStmt(): LangReturnStmtImpl {
        val returnLexeme = expectThenAdvance(Return)
        if (matchThenAdvance(Semicolon)) return LangReturnStmtImpl(returnLexeme, returnLexeme)
        val expr = expr()
        val last = expectThenAdvance(Semicolon)
        return LangReturnStmtImpl(returnLexeme, last, expr)
    }

    fun continueStmt(): LangContinueStmtImpl {
        val returnStmt = expectThenAdvance(Continue)
        expectThenAdvance(Semicolon)
        return LangContinueStmtImpl(returnStmt, returnStmt)
    }

    fun expr(): LangExpr  {
        val parser = prefixParsers[current.kind] ?: fail("Expected prefix expression here")
        val left = parser.parse(this, current)
        val infix = infixParsers[current.kind]?.parse(this, left, current)
        return infix ?: left
    }

    val previousLexeme: Lexeme
        get() = lexemes[position - 1]

    fun parsePackageDecl(): LangPackageDeclImpl {
        val packageLexeme = expectThenAdvance(Package)

        val packageName = separatedList(Identifier, Dot).joinToString(".") { it.text }
        val semicolonLexeme = expectThenAdvance(Semicolon)
        return LangPackageDeclImpl(packageName, packageLexeme, semicolonLexeme)
    }

    fun separatedList(entryKind: LexemeKind, separatorKind: LexemeKind): List<Lexeme> {
        val lexemes = mutableListOf<Lexeme>()
        lexemes.add(expectThenAdvance(entryKind))
        while (match(separatorKind)) {
            advance()
            lexemes.add(expectThenAdvance(entryKind))
        }
        return lexemes
    }
}


/**
 * Expected, that type of parser can be determined only by type of first lexeme
 * parse invoked only when it is clear that this parser matches
 */
private interface PrefixParser {
    fun parse(parser: ParserState, lexeme: Lexeme) : LangExpr
}

private class IntLiteralParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangIntLiteralExprImpl {
        return LangIntLiteralExprImpl(lexeme.text.toInt(), parser.advance())
    }
}

private class StringLiteralParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangStringLiteralExprImpl {
        return LangStringLiteralExprImpl(parseLiteral(lexeme.text), parser.advance())
    }
}

private class CharLiteralParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangStringLiteralExprImpl {
        TODO()
    }
}

private class ReferenceExprParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangReferenceExprImpl {
        parser.advance()
        return LangReferenceExprImpl(lexeme, lexeme.text)
    }
}

private class OpPrefixParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangPrefixExprImpl {
        parser.advance()
        val expr = parser.expr()
        return LangPrefixExprImpl(lexeme, parser.previousLexeme, expr, PrefixOperatorType.from(lexeme.text))
    }
}

private class BoolParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangBoolLiteralExprImpl {
        return LangBoolLiteralExprImpl(lexeme.text.toBoolean(), parser.advance())
    }
}

private interface InfixExprParser {
//    val stickiness: Int
    fun parse(parser: ParserState, left: LangExpr, lexeme: Lexeme): LangExpr
}

private class BinaryExprParser : InfixExprParser {
    override fun parse(parser: ParserState, left: LangExpr, lexeme: Lexeme): LangExpr {
        parser.advance()
        val binOp = LangBinaryOperatorImpl(lexeme.text, lexeme.startOffset, lexeme.endOffset)
        val right = parser.expr()
        return LangBinaryExprImpl(left, right, binOp, left.startOffset, right.endOffset)
    }
}