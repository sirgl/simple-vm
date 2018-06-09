package sirgl.simple.vm.codegen

import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.phases.SingleVisitorAstPass

class CodegenPass : SingleVisitorAstPass() {
    override val visitor: LangVisitor = object : LangVisitor() {

    }
}