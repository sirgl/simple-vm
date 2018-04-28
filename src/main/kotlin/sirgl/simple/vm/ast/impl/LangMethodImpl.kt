package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.LocalScope
import sirgl.simple.vm.resolve.symbols.MethodSymbol
import sirgl.simple.vm.type.LangType
import sirgl.simple.vm.type.VoidType

class LangMethodImpl(
    override val name: String,
    override val parameters: List<LangParameter>,
    override val block: LangBlock?,
    startLexeme: Lexeme,
    endLexeme: Lexeme,
    override val isNative: Boolean,
    override val returnTypeElement: LangTypeElement?
) : LangMemberImpl(startLexeme, endLexeme), LangMethod {
    override lateinit var symbol: MethodSymbol
    override val scope = LocalScope(this)


    override fun accept(visitor: LangVisitor) {
        visitor.visitMethod(this)
    }

    override val returnType: LangType
        get() = returnTypeElement?.type ?: VoidType

    override val debugName = "Method"

    override fun toString() = super.toString() + " name: $name"

    override val children = makeChildren()
    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>()
        nodes.addAll(parameters)
        if (returnTypeElement != null) {
            nodes.add(returnTypeElement)
        }
        if (block != null) {
            nodes.add(block)
        }
        return nodes
    }
}