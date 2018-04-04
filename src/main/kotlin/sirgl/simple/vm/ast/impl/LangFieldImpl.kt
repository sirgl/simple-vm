package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangField
import sirgl.simple.vm.ast.ext.getSourceFile
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.signatures.VariableSignature
import sirgl.simple.vm.type.LangType

class LangFieldImpl(
        override val name: String,
        override val type: LangType,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val initializer: LangExpr?
) : LangMemberImpl(startLexeme, endLexeme), LangField {
    override val signature: VariableSignature by lazy { VariableSignature(getSourceFile(), type, name) }
    override lateinit var parent: LangClass

    override fun accept(visitor: LangVisitor) {
        visitor.visitField(this)
    }

    override val debugName = "Field"

    override fun toString() = super.toString() + " name: $name, type: ${type.name}"

    override val children = makeChildren()

    private fun makeChildren(): List<AstNode> {
        return listOf(this.initializer ?: return emptyList())
    }
}