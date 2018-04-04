package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.ext.getSourceFile
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.signatures.MethodSignature
import sirgl.simple.vm.type.LangType

class LangMethodImpl(
        private val scope: Scope,
        override val name: String,
        override val parameters: List<LangParameter>,
        override val block: LangBlock?,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val isNative: Boolean,
        override val returnType: LangType
) : LangMemberImpl(startLexeme, endLexeme), LangMethod, Scope by scope {
    init {
        scope.element = this
    }
    override val signature: MethodSignature by lazy { MethodSignature(getSourceFile(), name, returnType, parameters.map { it.signature }) }

    override fun accept(visitor: LangVisitor) {
        visitor.visitMethod(this)
    }

    override val debugName = "Method"

    override fun toString() = super.toString() + " name: $name, returnType: ${returnType.name}"

    override val children = makeChildren()
    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>()
        nodes.addAll(parameters)
        if (block != null) {
            nodes.add(block)
        }
        return nodes
    }
}