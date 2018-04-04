package sirgl.simple.vm.resolve

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.resolve.signatures.Signature

class GlobalScope() : Scope {
    override lateinit var element: AstNode
    override val parentScope: Scope? = null


    override fun getMultipleDeclarations(): List<String> {
        return listOf()
    }

    override fun resolve(reference: LangReferenceExpr): Signature? {
        return null
    }

    override fun register(signature: Signature) {
    }

    override fun containsName(name: String): Boolean {
        return false
    }

}