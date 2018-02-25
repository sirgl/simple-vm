package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangParenExpr : LangExpr {
    val expr: LangExpr
}