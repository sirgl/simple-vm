package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.visitor.LangVisitor

class ResolveInspection(override val problemHolder: ProblemHolder) : LangInspection {
    override val visitor: LangVisitor = object: LangVisitor() {
        override fun visitReferenceExpr(expr: LangReferenceExpr) {
            if (expr.resolve() == null) {
                problemHolder.registerProblem(expr, "Unresolved reference: ${expr.name}", expr.getSymbolSource())
            }
        }
    }
}