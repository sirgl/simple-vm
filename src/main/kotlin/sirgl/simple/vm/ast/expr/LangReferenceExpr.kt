package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangReferenceExpr : LangExpr {
    val name: String
    val qualifier: LangExpr?
    val isSuper: Boolean
    val isThis: Boolean
}