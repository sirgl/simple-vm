package sirgl.simple.vm.analysis.inspections

import sirgl.simple.vm.analysis.LangInspection
import sirgl.simple.vm.analysis.ProblemHolder
import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.ext.findParentOfClass
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.stmt.LangBreakStmt
import sirgl.simple.vm.ast.stmt.LangContinueStmt
import sirgl.simple.vm.ast.stmt.LangWhileStmt
import sirgl.simple.vm.ast.visitor.LangVisitor

class SemanticAnalysisInspection(override val problemHolder: ProblemHolder) :
        LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {
        override fun visitMethod(method: LangMethod) {
            super.visitMethod(method)
            if (method.isNative && method.block != null) {
                problemHolder.registerProblem(method, "Method declared as native must have no body", method.getSymbolSource())
            }
            if (!method.isNative && method.block == null) {
                problemHolder.registerProblem(method, "Only native methods can have no body", method.getSymbolSource())
            }
        }

        override fun visitBreakStmt(stmt: LangBreakStmt) {
            val whileStmt = stmt.findParentOfClass<LangWhileStmt>()
            if (whileStmt == null) {
                problemHolder.registerProblem(stmt, "Break statement not allowed outside of while statement", stmt.getSymbolSource())
            }
        }

        override fun visitContinueStmt(stmt: LangContinueStmt) {
            val whileStmt = stmt.findParentOfClass<LangWhileStmt>()
            if (whileStmt == null) {
                problemHolder.registerProblem(stmt, "Continue statement not allowed outside of while statement", stmt.getSymbolSource())
            }
        }
    }
}