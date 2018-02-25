package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangMember
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.scope.Scope

class LangClassImpl(
        private val scope: Scope,
        override val simpleName: String,
        override val members: List<LangMember>,
        firstLexeme: Lexeme,
        endLexeme: Lexeme,
        override val parentClassName: String?
) : AstNodeImpl(firstLexeme.startOffset, endLexeme.endOffset), LangClass, Scope by scope {
    override lateinit var qualifiedName: String
    override lateinit var parent: LangFile

    override fun accept(visitor: LangVisitor) {
        visitor.visitClass(this)
    }

    override val debugName = "Class"

    override fun toString() = super.toString() + " name: $simpleName" + if (parentClassName != null) " parent: " + parentClassName else ""

    override val children: List<AstNode> = members

}