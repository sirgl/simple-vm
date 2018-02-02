package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangBinaryOperator
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.BinaryOperatorType

interface LangBinaryExpr : LangExpr {
    val left: LangExpr
    val right: LangExpr
    val operator: LangBinaryOperator
    val opTypeBinary: BinaryOperatorType
        get() = operator.typeBinary
}