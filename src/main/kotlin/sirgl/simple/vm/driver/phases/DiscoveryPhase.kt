package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.driver.SourceFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DiscoveryPhase : CompilerPhase() {
    override val name = "Discovery"

    override fun run(context: CompilerContext) {
        context.sourceFiles = collectFiles(Paths.get(context.configuration.sourcePath))
    }

    fun collectFiles(path: Path): List<SourceFile> {
        val sourceFiles = mutableListOf<SourceFile>()
        collectFiles(path, sourceFiles)
        return sourceFiles
    }

    fun collectFiles(path: Path, sourceFiles: MutableList<SourceFile>) {
        if (Files.isRegularFile(path)) {
            sourceFiles.add(SourceFile(path.toString(), { Files.newInputStream(path) }))
            return
        }
        if (Files.isDirectory(path)) {
            for (child in Files.newDirectoryStream(path)) {
                collectFiles(child, sourceFiles)
            }
        }
    }
}