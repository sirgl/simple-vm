package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangStmt
import sirgl.simple.vm.ast.impl.AstNodeImpl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

abstract class LangStmtImpl(startOffset: Int, endOffset: Int, line: Int) : AstNodeImpl(startOffset, endOffset, line),
    LangStmt {
    constructor(startLexeme: Lexeme, endLexeme: Lexeme) : this(
        startLexeme.startOffset,
        endLexeme.endOffset,
        startLexeme.line
    )

    override fun accept(visitor: LangVisitor) {
        visitor.visitStmt(this)
    }

    override lateinit var parent: AstNode
}