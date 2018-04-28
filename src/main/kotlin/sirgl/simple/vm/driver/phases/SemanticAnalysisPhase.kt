package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.analysis.ErrorHolderImpl
import sirgl.simple.vm.analysis.SemanticAnalysisInspection
import sirgl.simple.vm.ast.bypass.AstWalker
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase

class SemanticAnalysisPhase : CompilerPhase() {
    override val name = "Semantic analysis"

    override fun run(context: CompilerContext) {
//        for (ast in context.asts) {
//            val inspection = SemanticAnalysisInspection(ErrorHolderImpl(context.errorSink, ast.sourceFile))
//            val walker: AstWalker = SimpleWalker()
//            walker.prepassRecursive(ast) {
//                it.accept(inspection.visitor)
//            }
//        }
    }
}