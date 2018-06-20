package sirgl.simple.vm.ast.impl.resolve

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangReferenceElement
import sirgl.simple.vm.ast.impl.AstNodeImpl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangReferenceElementImpl(
        override val qualifier: LangReferenceElement?,
        override val name: String,
        startLexeme: Lexeme,
        endLexeme: Lexeme
) : AstNodeImpl(startLexeme, endLexeme), LangReferenceElement {
    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) {
        visitor.visitReference(this)
    }

    override val debugName = "Reference"

    override fun toString() = super.toString() + " name: $name"

    override val fullName: String
        get() = (qualifier?.fullName?.plus(".") ?: "") + name
    override val children: List<AstNode> = if (qualifier == null) emptyList() else listOf(qualifier)
}