package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangElementAccessExpr : LangExpr {
    val arrayExpr: LangExpr
    val indexExpr: LangExpr
}