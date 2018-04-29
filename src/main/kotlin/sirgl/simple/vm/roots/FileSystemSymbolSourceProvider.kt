package sirgl.simple.vm.roots

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.coroutines.experimental.buildSequence

typealias PathPredicate = (Path) -> Boolean

class FileSystemSymbolSourceProvider(
    val sourceFilePredicate: PathPredicate,
    val compiledFilePredicate: PathPredicate,
    sourceRoots: List<Path>
) : SymbolSourceProvider {
    val pathStack = ArrayDeque<Path>()
    init {
        for (sourceRoot in sourceRoots.reversed()) {
            pathStack.push(sourceRoot)
        }
    }
    override fun findSources(): Sequence<FileSymbolSource>  = buildSequence {
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