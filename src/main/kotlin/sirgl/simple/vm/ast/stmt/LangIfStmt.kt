package sirgl.simple.vm.ast.stmt

import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangStmt

interface LangIfStmt : LangStmt {
    val condition: LangExpr
    val thenBlock: LangBlock
    val elseBlock: LangBlock?
}