package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.LangTypeElement
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.symbols.ParameterSymbol
import sirgl.simple.vm.type.LangType

class LangParameterImpl(
    override val name: String,
    override val typeElement: LangTypeElement,
    startLexeme: Lexeme,
    endLexeme: Lexeme
) : AstNodeImpl(startLexeme, endLexeme), LangParameter {
    override lateinit var symbol: ParameterSymbol

    override val initializer: LangExpr? = null

    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) {
        visitor.visitParameter(this)
    }

    override val type: LangType
        get() = typeElement.type
    override val debugName = "Parameter"

    override fun toString() = super.toString() + " name: $name"

    override val children = makeChildren()

    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>()
        nodes.add(typeElement)
        return nodes
    }
}