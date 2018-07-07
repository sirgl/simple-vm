package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangConstructor
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.LocalScope
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.symbols.MethodSymbol
import sirgl.simple.vm.type.ClassType
import sirgl.simple.vm.type.LangType

class LangConstructorImpl(
        override val parameters: List<LangParameter>,
        override val block: LangBlock,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val isNative: Boolean
) : LangMemberImpl(startLexeme, endLexeme), LangConstructor {
    override lateinit var symbol: MethodSymbol
    override val scope: Scope = LocalScope(this)

    override val returnType: LangType by lazy { ClassType(enclosingClass.simpleName) }

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