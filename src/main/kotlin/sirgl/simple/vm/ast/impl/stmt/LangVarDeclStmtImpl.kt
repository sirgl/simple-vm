package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

class LangVarDeclStmtImpl(
        override val name: String,
        override val type: LangType,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val initializer: LangExpr?
) : LangStmtImpl(startLexeme, endLexeme), LangVarDeclStmt {
    override fun accept(visitor: LangVisitor) {
        visitor.visitVarDeclStmt(this)
    }

    override val debugName = "VarDeclStmt"

    override fun toString() = super.toString() + " name: $name, type: ${type.name}"

    override val children = makeChildren()

    private fun makeChildren(): List<AstNode> {
        return listOf(this.initializer ?: return emptyList())
    }
}