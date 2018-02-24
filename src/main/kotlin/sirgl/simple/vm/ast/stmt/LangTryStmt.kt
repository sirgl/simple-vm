package sirgl.simple.vm.ast.stmt

import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangCatchClause
import sirgl.simple.vm.ast.LangStmt

interface LangTryStmt : LangStmt {
    val tryBlock: LangBlock
    val catchBlocks: List<LangCatchClause>
}