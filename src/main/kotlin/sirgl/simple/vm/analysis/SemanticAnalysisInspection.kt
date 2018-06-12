package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.visitor.LangVisitor

class SemanticAnalysisInspection(override val problemHolder: ProblemHolder) : LangInspection {
    override val visitor: LangVisitor = object: LangVisitor() {
        override fun visitMethod(method: LangMethod) {
            super.visitMethod(method)
            if (method.isNative && method.block != null) {
                problemHolder.registerProblem(method, "Method declared as native must have no body", method.getSymbolSource())
            }
            if (!method.isNative && method.block == null) {
                problemHolder.registerProblem(method, "Only native methods can have no body", method.getSymbolSource())
            }
        }
    }
}