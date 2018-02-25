package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangCallExpr : LangExpr {
    val caller: LangExpr
    val arguments: List<LangExpr>
}