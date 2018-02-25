package sirgl.simple.vm.parser

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.expr.PrefixOperatorType
import sirgl.simple.vm.ast.ext.parseCharLiteral
import sirgl.simple.vm.ast.ext.parseStringLiteral
import sirgl.simple.vm.ast.impl.*
import sirgl.simple.vm.ast.impl.expr.*
import sirgl.simple.vm.ast.impl.stmt.*
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.lexer.LexemeKind
import sirgl.simple.vm.lexer.LexemeKind.*
import sirgl.simple.vm.scope.ScopeImpl
import sirgl.simple.vm.type.*

interface LangParser {
    fun parse(lexemes: List<Lexeme>): ParseResult<LangFileImpl>

    fun parseExpr(lexemes: List<Lexeme>): LangExpr
}

class ParseResult<out T : AstNode>(
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
                "but lexeme was ${lexeme.kind} with text \"${lexeme.text}\"" + "\n" + stackTrace
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
private val referenceExprParser = ReferenceExprParser()

private val prefixParsers = mapOf(
        IntLiteral to IntLiteralParser(),
        StringLiteral to StringLiteralParser(),
        Identifier to referenceExprParser,
        Super to referenceExprParser,
        This to referenceExprParser,
        OpPlus to opPrefixParser,
        OpMinus to opPrefixParser,
        OpExcl to opPrefixParser,
        True to boolLiteralParser,
        False to boolLiteralParser,
        LParen to ParenExprParser(),
        Null to NullParser(),
        CharLiteral to CharLiteralParser()
)

private class InfixOperatorInfo(
        val opKind: LexemeKind,
        val precedence: Int, // the more value, the lower precedence
        val isLeft: Boolean
)

private val binOpInfo = arrayOf(
        InfixOperatorInfo(OpAsterisk, 3, false),
        InfixOperatorInfo(OpDiv, 3, false),
        InfixOperatorInfo(OpPercent, 3, false),
        InfixOperatorInfo(OpPlus, 4, false),
        InfixOperatorInfo(OpMinus, 4, false),
        InfixOperatorInfo(OpLt, 6, false),
        InfixOperatorInfo(OpLtEq, 6, false),
        InfixOperatorInfo(OpGt, 6, false),
        InfixOperatorInfo(OpGtEq, 6, false),
        InfixOperatorInfo(OpEqEq, 7, false),
        InfixOperatorInfo(OpNotEq, 7, false),
        InfixOperatorInfo(OpAndAnd, 8, false),
        InfixOperatorInfo(OpOrOr, 9, false),
        InfixOperatorInfo(OpEq, 10, true)
)

private val binOps = binOpInfo.associateBy({ it.opKind }) { BinaryExprParser(it.precedence, it.isLeft) }

private val infixOperators: Map<LexemeKind, InfixExprParser> = buildInfixOperators()

private fun buildInfixOperators(): Map<LexemeKind, InfixExprParser> {
    val infixOperators = mutableMapOf<LexemeKind, InfixExprParser>()
    infixOperators.putAll(binOps)
    infixOperators[OpEq] = AssignExprParser()
    infixOperators[LParen] = CallExprParser()
    infixOperators[Dot] = DotExprParser()
    infixOperators[LBracket] = ElementAccessExprParser()
    infixOperators[OpAs] = CastExprParser()
    infixOperators[OpIs] = TypeCheckExpr()
    return infixOperators
}


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
    val current: Lexeme
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
        expect(*types)
        return advance()
    }

    fun expect(vararg types: LexemeKind) {
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
        val members = mutableListOf<LangMemberImpl>()
        loop@
        while (true) {
            val member: LangMemberImpl = when (current.kind) {
                Fun, Native -> method()
                Constructor -> constructor()
                Var -> field()
                RBrace -> break@loop
                else -> fail("Class member expected")
            }
            members.add(member)
        }
        val rBrace = expectThenAdvance(RBrace)
        val cls = LangClassImpl(ScopeImpl(), clsNameNode.text, members, clsNode, rBrace, parentClsName?.text)
        for (member in members) {
            member.parent = cls
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
                parameters,
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

    fun constructor(): LangConstructorImpl {
        val nativeLexeme = if (match(Native)) {
            advance()
        } else {
            null
        }
        val funLexeme = expectThenAdvance(Constructor)
        val parameters = parameters()
        val block = block()
        val constructor = LangConstructorImpl(
                ScopeImpl(),
                parameters,
                block,
                nativeLexeme ?: funLexeme,
                block.rBrace,
                nativeLexeme != null
        )
        for (parameter in parameters) {
            parameter.parent = constructor
        }
        block.parent = constructor
        return constructor
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

    fun whileStmt(): LangWhileStmtImpl {
        val whileLexeme = expectThenAdvance(While)
        expectThenAdvance(LParen)
        val expr = expr()
        expectThenAdvance(RParen)
        val block = block()
        val whileStmt = LangWhileStmtImpl(whileLexeme, previousLexeme, expr, block)
        expr.parent = whileStmt
        block.parent = whileStmt
        return whileStmt
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
        val rBrace = expectThenAdvance(RBrace)
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
        val field = LangFieldImpl(identifier.text, type, varLexeme, last, initializer)
        initializer?.parent = field
        return field
    }

    fun localVar(): LangVarDeclStmtImpl {
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
        val localVar = LangVarDeclStmtImpl(identifier.text, type, varLexeme, last, initializer)
        initializer?.parent = localVar
        return localVar
    }

    fun stmt() = when (current.kind) {
        Return -> returnStmt()
        Continue -> continueStmt()
        Break -> breakStmt()
        If -> ifStmt()
        Try -> tryStmt()
        While -> whileStmt()
        Var -> localVar()
        else -> exprStmt()
    }

    fun exprStmt(): LangExprStmtImpl {
        val expr = expr()
        val semi = expectThenAdvance(Semicolon)
        val exprStmt = LangExprStmtImpl(expr.startOffset, semi.endOffset, expr.startLine, expr)
        expr.parent = exprStmt
        return exprStmt
    }

    fun returnStmt(): LangReturnStmtImpl {
        val returnLexeme = expectThenAdvance(Return)
        if (matchThenAdvance(Semicolon)) return LangReturnStmtImpl(returnLexeme, returnLexeme)
        val expr = expr()
        val last = expectThenAdvance(Semicolon)
        val returnStmt = LangReturnStmtImpl(returnLexeme, last, expr)
        expr.parent = returnStmt
        return returnStmt
    }

    fun continueStmt(): LangContinueStmtImpl {
        val continueStmt = expectThenAdvance(Continue)
        expectThenAdvance(Semicolon)
        return LangContinueStmtImpl(continueStmt)
    }


    fun breakStmt(): LangBreakStmtImpl {
        val breakStmt = expectThenAdvance(Break)
        expectThenAdvance(Semicolon)
        return LangBreakStmtImpl(breakStmt)
    }

    fun ifStmt(): LangIfStmtImpl {
        val startLexeme = expectThenAdvance(If)
        expectThenAdvance(LParen)
        val condition = expr()
        expectThenAdvance(RParen)
        val thenBlock = block()
        val elseBlock = if (match(Else)) {
            advance()
            block()
        } else {
            null
        }
        val ifStmt = LangIfStmtImpl(startLexeme, previousLexeme, condition, thenBlock, elseBlock)
        condition.parent = ifStmt
        thenBlock.parent = ifStmt
        elseBlock?.parent = ifStmt
        return ifStmt
    }

    fun tryStmt(): LangTryStmtImpl {
        val startLexeme = expectThenAdvance(Try)
        val block = block()
        val catches = catches()
        val tryStmt = LangTryStmtImpl(startLexeme, previousLexeme, block, catches)
        block.parent = tryStmt
        for (catch in catches) {
            catch.parent = tryStmt
        }
        return tryStmt
    }

    fun catches(): List<LangCatchClauseImpl> {
        val catches = mutableListOf<LangCatchClauseImpl>()
        catches.add(catch())
        while (match(Catch)) {
            catches.add(catch())
        }
        return catches
    }

    fun catch(): LangCatchClauseImpl {
        val catchLexeme = expectThenAdvance(Catch)
        expectThenAdvance(LParen)
        val parameter = parameter()
        expectThenAdvance(RParen)
        val block = block()
        val catchClause = LangCatchClauseImpl(catchLexeme, previousLexeme, parameter, block)
        block.parent = catchClause
        parameter.parent = catchClause
        return catchClause
    }

    // won't parse expr with lower value of the precedence
    fun expr(precedence: Int = 10): LangExprImpl {
        val parser = prefixParsers[current.kind] ?: fail("Expected prefix expression here")
        var left = parser.parse(this, current)

        while (precedence > getPrecedence()) {
            left = infixParsers[current.kind]?.parse(this, left, current) ?: fail("Expected binary operator")
        }
        return left
    }

    fun getPrecedence(): Int {
        val precedence = infixParsers[current.kind]?.precedence
        return precedence ?: 100 // Max precedence
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
    fun parse(parser: ParserState, lexeme: Lexeme): LangExprImpl
}

private class IntLiteralParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangIntLiteralExprImpl {
        return LangIntLiteralExprImpl(lexeme.text.toInt(), parser.advance())
    }
}

private class NullParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangNullExprImpl {
        return LangNullExprImpl(parser.advance())
    }
}

private class StringLiteralParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangStringLiteralExprImpl {
        return LangStringLiteralExprImpl(parseStringLiteral(lexeme.text), parser.advance())
    }
}

private class ParenExprParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangParenExprImpl {
        parser.advance()
        val expr = parser.expr()
        val rParen = parser.expectThenAdvance(RParen)
        val parenExpr = LangParenExprImpl(lexeme, rParen, expr)
        expr.parent = parenExpr
        return parenExpr
    }
}

private class CharLiteralParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangCharLiteralExprImpl {
        return LangCharLiteralExprImpl(parseCharLiteral(lexeme.text), parser.advance())
    }
}

private class ReferenceExprParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangReferenceExprImpl {
        var isSuper = false
        var isThis = false
        if (lexeme.kind == Super) isSuper = true
        else if (lexeme.kind == This) isThis = true
        parser.advance()
        return LangReferenceExprImpl(lexeme, lexeme.text, null, isSuper, isThis)
    }
}

private class OpPrefixParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangPrefixExprImpl {
        parser.advance()
        val expr = parser.expr()
        val prefixExpr = LangPrefixExprImpl(lexeme, parser.previousLexeme, expr, PrefixOperatorType.from(lexeme.text))
        expr.parent = prefixExpr
        return prefixExpr
    }
}

private class BoolParser : PrefixParser {
    override fun parse(parser: ParserState, lexeme: Lexeme): LangBoolLiteralExprImpl {
        return LangBoolLiteralExprImpl(lexeme.text.toBoolean(), parser.advance())
    }
}

/**
 * When parse invoked current lexeme is parameter lexeme right after left expr
 */
private interface InfixExprParser {
    val precedence: Int
    fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl
}

private class DotExprParser : InfixExprParser {
    override val precedence = 1

    override fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl {
        parser.advance()
        var isSuper = false
        var isThis = false
        if (parser.current.kind == Super) isSuper = true
        else if (parser.current.kind == This) isThis = true
        val right = parser.advance()
        val refExpr = LangReferenceExprImpl(right, right.text, left, isSuper, isThis)
        left.parent = refExpr
        return refExpr
    }
}

private class BinaryExprParser(
        override val precedence: Int,
        val isLeft: Boolean
) : InfixExprParser {
    override fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl {
        parser.advance()
        val binOp = LangBinaryOperatorImpl(lexeme.text, lexeme)
        val right = parser.expr(precedence + if (isLeft) 0 else 1)
        val binExpr = LangBinaryExprImpl(left, right, binOp, left.startOffset, right.endOffset, left.startLine)
        left.parent = binExpr
        binOp.parent = binExpr
        right.parent = binExpr
        return binExpr
    }
}

private class AssignExprParser : InfixExprParser {
    override val precedence = 9

    override fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl {
        if (left !is LangReferenceExpr) parser.fail("Left part of assignment expression must be reference")
        parser.advance()
        val right = parser.expr(precedence + 1)
        val assignExpr = LangAssignExprImpl(left.startOffset, right.endOffset, left.startLine, left, right)
        left.parent = assignExpr
        right.parent = assignExpr
        return assignExpr
    }
}

private class CallExprParser : InfixExprParser {
    override val precedence = 1

    override fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl {
        parser.advance()
        val arguments = mutableListOf<LangExprImpl>()
        while (!parser.match(RParen)) {
            arguments.add(parser.expr())
            if (!parser.matchThenAdvance(Comma)) {
                break
            }
        }
        val rParen = parser.expectThenAdvance(RParen)
        val callExpr = LangCallExprImpl(rParen, left, arguments)
        left.parent = callExpr
        for (arg in arguments) {
            arg.parent = callExpr
        }
        return callExpr
    }
}

private class ElementAccessExprParser : InfixExprParser {
    override val precedence = 1

    override fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl {
        parser.advance()
        val indexExpr = parser.expr()
        val last = parser.expectThenAdvance(RBracket)
        val accessExpr = LangElementAccessExprImpl(last, left, indexExpr)
        indexExpr.parent = accessExpr
        left.parent = accessExpr
        return accessExpr
    }
}

private class CastExprParser : InfixExprParser {
    override val precedence = 2

    override fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl {
        parser.advance()
        val type = parser.type()
        val castExpr = LangCastExprImpl(parser.previousLexeme, left, type)
        left.parent = castExpr
        return castExpr
    }
}

private class TypeCheckExpr : InfixExprParser {
    override val precedence = 5

    override fun parse(parser: ParserState, left: LangExprImpl, lexeme: Lexeme): LangExprImpl {
        parser.advance()
        val type = parser.type()
        val castExpr = LangTypeCheckExprImpl(parser.previousLexeme, left, type)
        left.parent = castExpr
        return castExpr
    }
}