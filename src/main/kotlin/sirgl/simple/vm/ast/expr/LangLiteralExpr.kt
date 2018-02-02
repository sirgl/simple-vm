package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangLiteralExpr : LangExpr {
    val value: Any
}