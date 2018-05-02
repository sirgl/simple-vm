package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.visitor.LangVisitor

class SemanticAnalysisInspection2(override val problemHolder: ProblemHolder) : LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {

    }
}