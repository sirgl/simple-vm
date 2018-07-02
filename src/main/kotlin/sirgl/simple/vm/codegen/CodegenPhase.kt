package sirgl.simple.vm.codegen

import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor
import sirgl.simple.vm.common.defaultCompiledFileExtension
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class CodegenPhase : CompilerPhase<CodegenPhase>() {
    override fun run(context: CompilerContext) {
        val walker = SimpleWalker()
        for (source in context.astCache.getAllSources()) {
            val path = source.sourceFileSource.path
            if (path != null) {
                println("Codegen for file $path")
            }
            if (path == null) {
                println("Failed to generate code for file, no path supplied")
                return
            }
            val pass = CodegenPass()
            pass.doPass(source.file, walker)
            val classRepr = pass.classWriter.build()
            val outputFile = stripExtension(path.toString()) + "." + defaultCompiledFileExtension
            val file = Files.createFile(Paths.get(outputFile))
            val os = file.toFile().outputStream()
            val dos = DataOutputStream(os)
            classRepr.write(dos)
            println("Successful codegen, output file: $file")
        }
    }


    override val descriptor = Companion

    companion object : PhaseDescriptor<CodegenPhase>("Codegen")
}


fun stripExtension(str: String): String {
    val pos = str.lastIndexOf(".")
    return if (pos == -1) str else str.substring(0, pos)
}