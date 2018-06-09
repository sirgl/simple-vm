package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.ast.visitor.LangVisitor

abstract class SingleVisitorAstPass : AstPass() {
    abstract val visitor: LangVisitor
    override val visitors: List<LangVisitor> by lazy { listOf(visitor) }
}