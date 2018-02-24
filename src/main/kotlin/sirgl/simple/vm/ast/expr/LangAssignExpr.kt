package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangAssignExpr : LangExpr {
    val leftRef: LangReferenceExpr
    val rightValue: LangExpr
}