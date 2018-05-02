package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangImport
import sirgl.simple.vm.ast.LangReferenceElement
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangImportImpl(
    startLexeme: Lexeme,
    endLexeme: Lexeme,
    override val referenceElement: LangReferenceElement
) : AstNodeImpl(startLexeme, endLexeme), LangImport {
    override lateinit var parent: LangFile

    override fun accept(visitor: LangVisitor) {
        visitor.visitImport(this)
    }

    override val debugName: String = "Import"

    override val children: List<AstNode> = listOf(referenceElement)
}