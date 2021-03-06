package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.bypass.AstWalker
import sirgl.simple.vm.ast.visitor.LangVisitor

abstract class AstPass {
    abstract val visitors: List<LangVisitor>

    fun doPostPass(file: LangFile, walker: AstWalker) {
        walker.postpassRecursive(file) {
            for (visitor in visitors) {
                it.accept(visitor)
            }
        }
    }

    fun doPass(file: LangFile, walker: AstWalker) {
        walker.prepassRecursive(file) {
            for (visitor in visitors) {
                it.accept(visitor)
            }
        }
    }

    abstract val name: String
}