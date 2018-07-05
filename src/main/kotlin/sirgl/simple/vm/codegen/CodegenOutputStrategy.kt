package sirgl.simple.vm.codegen

import sirgl.simple.vm.common.defaultCompiledFileExtension
import sirgl.simple.vm.roots.SourceFileSource
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths

interface CodegenOutputStrategy {
    fun getOutputType(): OutputType

    /**
     * Returns null if generation for file is not necessary
     */
    fun getOutputStream(sourceFileSource: SourceFileSource): OutputStream?
}

enum class OutputType {
    BINARY,
    TEXT
}

class DefaultCodegenStrategy : CodegenOutputStrategy {
    override fun getOutputType() = OutputType.BINARY

    override fun getOutputStream(sourceFileSource: SourceFileSource): OutputStream? {
        val path = sourceFileSource.path ?: return null
        val outputFile = stripExtension(path.toString()) + "." + defaultCompiledFileExtension
        val file = Files.createFile(Paths.get(outputFile))
        return file.toFile().outputStream()
    }
}

fun stripExtension(str: String): String {
    val pos = str.lastIndexOf(".")
    return if (pos == -1) str else str.substring(0, pos)
}