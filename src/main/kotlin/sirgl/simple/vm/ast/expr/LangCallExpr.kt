package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangCallExpr : LangExpr {
    val referenceExpr: LangReferenceExpr
    val arguments: List<LangExpr>
}