package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangStmt
import sirgl.simple.vm.ast.impl.AstNodeImpl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangStmtImpl(startLexeme: Lexeme, endLexeme: Lexeme) : AstNodeImpl(startLexeme, endLexeme), LangStmt {
    override fun accept(visitor: LangVisitor) {
        visitor.visitStmt(this)
    }

    override lateinit var parent: AstNode

}