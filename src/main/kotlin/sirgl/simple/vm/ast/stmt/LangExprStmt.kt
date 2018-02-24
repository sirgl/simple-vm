package sirgl.simple.vm.ast.stmt

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangStmt

interface LangExprStmt : LangStmt {
    val expr: LangExpr
}