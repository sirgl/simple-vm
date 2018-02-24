package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.BinaryOperatorType
import sirgl.simple.vm.ast.LangBinaryOperator
import sirgl.simple.vm.ast.LangExpr

interface LangBinaryExpr : LangExpr {
    val left: LangExpr
    val right: LangExpr
    val operator: LangBinaryOperator
    val opTypeBinary: BinaryOperatorType
        get() = operator.typeBinary
}