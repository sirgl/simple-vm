package sirgl.simple.vm.scope

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.signatures.Signature

interface Scope {
    val parentScope: Scope?
    var element: AstNode
    /**
     * Single namespace for all Symbols
     */
    fun resolve(reference: LangReferenceExpr): Signature?

    fun register(signature: Signature)

    fun containsName(name: String): Boolean
}