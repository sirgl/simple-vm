package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangConstructor
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.scope.Scope
import sirgl.simple.vm.type.LangType
import sirgl.simple.vm.type.toType

class LangConstructorImpl(
        private val scope: Scope,
        override val parameters: List<LangParameter>,
        override val block: LangBlock,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val isNative: Boolean
) : LangMemberImpl(startLexeme, endLexeme), LangConstructor, Scope by scope {
    init {
        scope.element = this
    }

    override val returnType: LangType by lazy { enclosingClass.toType() }

    override fun accept(visitor: LangVisitor) {
        visitor.visitConstructor(this)
    }

    override val debugName = "Constructor"

    override val children = makeChildren()
    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>()
        nodes.addAll(parameters)
        nodes.add(block)
        return nodes
    }
}