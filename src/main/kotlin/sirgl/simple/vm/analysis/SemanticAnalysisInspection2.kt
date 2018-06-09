package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.LangBinaryOperator
import sirgl.simple.vm.ast.expr.LangBinaryExpr
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.stmt.LangIfStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.type.BoolType
import sirgl.simple.vm.type.isAssignableTo

class SemanticAnalysisInspection2(override val problemHolder: ProblemHolder) : LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {
        override fun visitIfStmt(stmt: LangIfStmt) {
            super.visitIfStmt(stmt)
            val condition = stmt.condition
            if (!condition.type.isAssignableTo(BoolType)) {
                problemHolder.registerProblem(condition, "Condition must have boolean type", stmt.getSymbolSource()) // TODO generalize
            }
        }
    }
}