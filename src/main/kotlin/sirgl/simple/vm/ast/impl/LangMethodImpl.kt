package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.scope.Scope

class LangMethodImpl(
        private val scope: Scope,
        override val name: String,
        override val parameters: Array<LangParameter>,
        override val block: LangBlock,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val isNative: Boolean
) : LangMemberImpl(startLexeme, endLexeme), LangMethod, Scope by scope {
    override fun accept(visitor: LangVisitor) {
        visitor.visitMethod(this)
    }

    override val debugName = "Method"

    override fun toString() = super.toString() + " name: $name"

    override val children = listOf(*parameters, block)
}