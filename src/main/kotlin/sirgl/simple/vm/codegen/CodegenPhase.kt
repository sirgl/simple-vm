package sirgl.simple.vm.codegen

import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor
import java.io.DataOutputStream

class CodegenPhase : CompilerPhase<CodegenPhase>() {
    override fun run(context: CompilerContext) {
        val walker = SimpleWalker()
        val codegenOutputStrategy = context.codegenOutputStrategy
        for (source in context.astCache.getAllSources()) {
            val outputStream = codegenOutputStrategy.getOutputStream(source.sourceFileSource)
            if (outputStream == null) {
                println("Skipping file ${source.sourceFileSource.path} due to codegen strategy")
                continue
            }
            val pass = CodegenPass()
            pass.doPass(source.file, walker)
            val classRepr = pass.classWriter.build()
            when (codegenOutputStrategy.getOutputType()) {
                OutputType.BINARY -> {
                    val dos = DataOutputStream(outputStream)
                    classRepr.write(dos)
                }
                OutputType.TEXT -> {
                    outputStream.bufferedWriter().use { it.write(classRepr.toString()) }
                }
            }
        }
    }


    override val descriptor = Companion

    companion object : PhaseDescriptor<CodegenPhase>("Codegen")
}


