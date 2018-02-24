package sirgl.simple.vm.ast.stmt

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangStmt
import sirgl.simple.vm.ast.expr.LangReferenceExpr

interface LangAssignStmt : LangStmt {
    val referenceExpr: LangReferenceExpr
    val expression: LangExpr
}