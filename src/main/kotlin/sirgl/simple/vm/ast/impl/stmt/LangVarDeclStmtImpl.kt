package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangTypeElement
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.symbols.LocalVarSymbol
import sirgl.simple.vm.type.LangType

class LangVarDeclStmtImpl(
        override val name: String,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val initializer: LangExpr?,
        override val typeElement: LangTypeElement
) : LangStmtImpl(startLexeme, endLexeme), LangVarDeclStmt {
    override val type: LangType
        get() = typeElement.type
    override var slot: Short = -1
    override lateinit var symbol: LocalVarSymbol

    override fun accept(visitor: LangVisitor) {
        visitor.visitVarDeclStmt(this)
    }

    override val debugName = "VarDeclStmt"

    override fun toString() = super.toString() + " name: $name"

    override val children = makeChildren()

    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>()
        nodes.add(typeElement)
        if (initializer != null) {
            nodes.add(initializer)
        }
        return nodes
    }
}