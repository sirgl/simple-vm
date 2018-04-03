package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.analysis.SetupVisitor
import sirgl.simple.vm.ast.bypass.AstWalker
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.scope.ScopeImpl

class SetupPhase : CompilerPhase() {
    override val name = "Setup"

    // TODO it should be done parallel and it shouldn't be saved such way
    override fun run(context: CompilerContext) {
        for ((sourceFile, ast) in context.resolveCache.getAllFiles()) {
            val visitor = SetupVisitor(ScopeImpl(), sourceFile)
            val walker: AstWalker = SimpleWalker()
            walker.walkRecursive(ast) {
                it.accept(visitor)
            }
            context.asts.add(ast)
        }
    }
}