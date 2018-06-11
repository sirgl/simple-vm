package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.driver.phases.AstPass

class SemanticAnalysisPass(private val inspections: MutableList<LangInspection> = mutableListOf()) : AstPass() {
    override val name: String = "Semantic analysis"
    override val visitors: List<LangVisitor> by lazy { inspections.map { it.visitor } }
}