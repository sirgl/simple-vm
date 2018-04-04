package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.type.LangType

interface LangTypeCheckExpr : LangExpr {
    val expr: LangExpr
    val targetType: LangType
}