package sirgl.simple.vm.analysis.inspections

import sirgl.simple.vm.analysis.LangInspection
import sirgl.simple.vm.analysis.ProblemHolder
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.common.CommonClassNames

class ResolveInspection(override val problemHolder: ProblemHolder) :
        LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {
        override fun visitReferenceExpr(expr: LangReferenceExpr) {
            if (expr.resolve() == null) {
                problemHolder.registerProblem(expr, "Unresolved reference: ${expr.name}", expr.getSymbolSource())
            }
        }

        override fun visitClass(cls: LangClass) {
            val parentClassReferenceElement = cls.parentClassReferenceElement ?: return
            if (cls.qualifiedName != CommonClassNames.LANG_OBJECT) {
                if (parentClassReferenceElement.qualifier != null) {
                    TODO()
                }
                if (cls.scope.resolve(parentClassReferenceElement.name, null) == null) {
                    problemHolder.registerProblem(
                            parentClassReferenceElement,
                            "Unresolved reference: ${parentClassReferenceElement.name}",
                            parentClassReferenceElement.getSymbolSource()
                    )
                }
            }
        }
    }
}