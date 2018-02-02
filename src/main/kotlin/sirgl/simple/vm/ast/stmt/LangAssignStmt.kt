package sirgl.simple.vm.ast.stmt

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangNameElement
import sirgl.simple.vm.ast.LangStmt

interface LangAssignStmt : LangStmt {
    val nameElement: LangNameElement
    val expression: LangExpr
}