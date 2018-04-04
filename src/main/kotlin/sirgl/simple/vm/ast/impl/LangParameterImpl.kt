package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.ext.getSourceFile
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.signatures.VariableSignature
import sirgl.simple.vm.type.LangType

class LangParameterImpl(
        override val name: String,
        override val type: LangType,
        startLexeme: Lexeme,
        endLexeme: Lexeme
) : AstNodeImpl(startLexeme, endLexeme), LangParameter {
    override val signature: VariableSignature by lazy { VariableSignature(getSourceFile(), type, name) }

    override val initializer: LangExpr? = null

    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) {
        visitor.visitParameter(this)
    }

    override val debugName = "Parameter"

    override fun toString() = super.toString() + " name: $name, type: ${type.name}"

    override val children = emptyList<AstNode>()
}