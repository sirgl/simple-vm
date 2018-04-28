package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangTypeElement
import sirgl.simple.vm.type.LangType

interface LangCastExpr : LangExpr {
    val expr: LangExpr
    val targetType: LangType
    val targetTypeElement: LangTypeElement
}