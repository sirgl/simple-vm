package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangPackageDecl
import sirgl.simple.vm.ast.LangReferenceElement
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangPackageDeclImpl(
        override val referenceElement: LangReferenceElement,
        startLexeme: Lexeme,
        endLexeme: Lexeme
) : AstNodeImpl(startLexeme, endLexeme), LangPackageDecl {

    override lateinit var parent: LangFile


    override fun accept(visitor: LangVisitor) {
        visitor.visitPackageDecl(this)
    }

    override val debugName = "Package"

    override val children = listOf(referenceElement)
}