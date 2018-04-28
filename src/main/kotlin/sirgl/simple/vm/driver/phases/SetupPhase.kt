package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase

class SetupPhase : CompilerPhase() {
    override val name = "Setup"

    // TODO it should be done parallel and it shouldn't be saved such way
    override fun run(context: CompilerContext) {
//        val globalScope = GlobalScope()
//        for ((sourceFile, ast) in context.resolveCache.getAllFiles()) {
//            val visitor = SetupVisitor(globalScope, sourceFile)
//            val walker: AstWalker = SimpleWalker()
//            val fileImpl = ast as LangFileImpl
//            fileImpl.sourceFile = sourceFile
//            fileImpl.scope = globalScope
//            val cls = fileImpl.classDecl
//            val signature = cls.signature
//            for (fieldSignature in signature.fieldSignatures) {
//                cls.register(fieldSignature)
//            }
//            for (methodSignature in signature.methodSignatures) {
//                cls.register(methodSignature)
//            }
//            walker.postpassRecursive(ast) {
//                it.accept(visitor)
//            }
//            context.asts.add(ast)
//        }
    }
}