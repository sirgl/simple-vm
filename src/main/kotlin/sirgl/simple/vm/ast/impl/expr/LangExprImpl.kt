package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.impl.AstNodeImpl
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

abstract class LangExprImpl(startOffset: Int, endOffset: Int, line: Int) : LangExpr,
    AstNodeImpl(startOffset, endOffset, line) {
    constructor(startLexeme: Lexeme, endLexeme: Lexeme) : this(
        startLexeme.startOffset,
        endLexeme.endOffset,
        startLexeme.line
    )

    override lateinit var parent: AstNode
    override var promoteToType: LangType? = null
}