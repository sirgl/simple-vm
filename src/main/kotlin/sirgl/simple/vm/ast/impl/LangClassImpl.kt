package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.GlobalScope
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.symbols.ClassSymbol
import sirgl.simple.vm.resolve.symbols.toSymbol

class LangClassImpl(
    override val simpleName: String,
    override val members: List<LangMember>,
    firstLexeme: Lexeme,
    endLexeme: Lexeme,
    override val parentClassReferenceElement: LangReferenceElement?
) : AstNodeImpl(firstLexeme, endLexeme), LangClass {
    override lateinit var symbol: ClassSymbol
    override val scope: Scope by lazy { symbol }
    override lateinit var parent: LangFile

    fun setupSymbol(globalScope: GlobalScope) {
        symbol = toSymbol(globalScope)
    }

    override val qualifiedName: String by lazy { findFqn() }
    override val parentClassName: String?
        get() = parentClassReferenceElement?.fullName

    private fun findFqn(): String {
        val declaredPackage = parent.packageDeclaration?.referenceElement?.getFullName() ?: return simpleName
        return "$declaredPackage.$simpleName"
    }

    override fun accept(visitor: LangVisitor) {
        visitor.visitClass(this)
    }

    override val debugName = "Class"

    override fun toString() =
        super.toString() + " name: $simpleName" + if (parentClassName != null) ", parent: $parentClassName" else ""

    override val children: List<AstNode> = members
}