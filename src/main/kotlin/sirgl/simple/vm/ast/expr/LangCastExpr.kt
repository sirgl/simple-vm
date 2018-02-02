package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.type.LangType

interface LangCastExpr : LangExpr {
    val expression: LangExpr
    val targetType: LangType
}