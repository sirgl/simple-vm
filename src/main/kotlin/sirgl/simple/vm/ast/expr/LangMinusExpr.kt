package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangMinusExpr : LangExpr {
    val expression: LangExpr
}