package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.ErrorSink

class FirstPassCheckingInspection(override val errorSink: ErrorSink) : LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {
//        override fun visitIfStmt(stmt: LangIfStmt) {
//            stmt.
//        }
    }
}