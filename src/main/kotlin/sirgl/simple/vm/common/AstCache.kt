package sirgl.simple.vm.common

import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.driver.AstBuildingTask
import sirgl.simple.vm.roots.SourceFileSource
import java.lang.ref.SoftReference
import kotlin.coroutines.experimental.buildSequence


data class SourceFileComputable(
        val sourceFile: SourceFileSource,
        val astBuildingTask: AstBuildingTask
)

data class SourceFileInfo(
        val sourceFileSource: SourceFileSource,
        val file: LangFile
)

/**
 * Class designed to be filled completely before use
 */
class AstCache {
    private val fqnToSource: MutableMap<String, SourceFileComputable> = mutableMapOf()
    private val fqnToAst: MutableMap<String, SoftReference<LangFile>> = mutableMapOf()
    private var completed = false

    fun addSourceFile(file: LangFile, sourceFileSource: SourceFileSource, astBuildingTask: AstBuildingTask) {
        if (completed) {
            throw IllegalStateException("It is impossible after completion to add entries to cache")
        }
        val qualifiedName = file.classDecl.qualifiedName
        fqnToAst[qualifiedName] = SoftReference(file)
        fqnToSource[qualifiedName] = SourceFileComputable(sourceFileSource, astBuildingTask)
    }

    fun getAllSources(): Sequence<SourceFileInfo> = buildSequence {
        for ((fqn, fileReference) in fqnToAst) {
            val (sourceFileSource, astBuildingTask) = fqnToSource[fqn]!!
            val file = fileReference.get() ?: astBuildingTask.parse()!!
            yield(SourceFileInfo(sourceFileSource, file))
        }
    }

    fun complete() {
        completed = true
    }
}