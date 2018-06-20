package sirgl.simple.vm.roots

import sirgl.simple.vm.common.defaultCompiledFileExtension
import sirgl.simple.vm.common.defaultSourceFileExtension
import sirgl.simple.vm.ext.extension
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.coroutines.experimental.buildSequence

typealias PathPredicate = (Path) -> Boolean

class FileSystemSymbolSourceProvider(
        sourceRoots: List<Path>,
        val sourceFilePredicate: PathPredicate = { path -> path.extension() == defaultSourceFileExtension },
        val compiledFilePredicate: PathPredicate = { path -> path.extension() == defaultCompiledFileExtension }
) : SymbolSourceProvider {
    val pathStack = ArrayDeque<Path>()

    init {
        for (sourceRoot in sourceRoots.reversed()) {
            pathStack.push(sourceRoot)
        }
    }

    override fun findSources(): Sequence<FileSymbolSource> = buildSequence {
        while (pathStack.isNotEmpty()) {
            val path = pathStack.pop()
            if (Files.isDirectory(path)) {
                Files.newDirectoryStream(path).use {
                    it.forEach {
                        when {
                            Files.isRegularFile(it) -> when {
                                sourceFilePredicate(it) -> yield(FsSourceFileSource(it))
                                compiledFilePredicate(it) -> yield(FsCompiledFileSource(it))
                            }
                            Files.isDirectory(it) -> pathStack.push(it)
                        }
                    }
                }
            }
        }
    }
}