package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.resolve.symbols.Symbol

interface LangReferenceExpr : LangExpr {
    val name: String
    val qualifier: LangExpr?
    val isSuper: Boolean
    val isThis: Boolean

    val isQualified: Boolean
        get() = qualifier != null

    fun resolve(): Symbol?
}