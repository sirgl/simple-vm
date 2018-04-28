package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.symbols.FieldSymbol
import sirgl.simple.vm.type.LangType

class LangFieldImpl(
    override val name: String,
    startLexeme: Lexeme,
    endLexeme: Lexeme,
    override val initializer: LangExpr?,
    override val typeElement: LangTypeElement
) : LangMemberImpl(startLexeme, endLexeme), LangField {
    override lateinit var symbol: FieldSymbol
    override lateinit var parent: LangClass

    override fun accept(visitor: LangVisitor) {
        visitor.visitField(this)
    }

    override val type: LangType
        get() = typeElement.type

    override val debugName = "Field"

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