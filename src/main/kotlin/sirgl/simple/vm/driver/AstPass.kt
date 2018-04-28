package sirgl.simple.vm.driver

import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.bypass.AstWalker
import sirgl.simple.vm.ast.visitor.LangVisitor

class AstPass(
    private val visitors: List<LangVisitor>,
    private val walker: AstWalker
) {
    fun makePass(file: LangFile) {
        walker.postpassRecursive(file) {
            for (visitor in visitors) {
                it.accept(visitor)
            }
        }
    }
}