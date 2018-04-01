package sirgl.simple.vm.driver

import sirgl.simple.vm.ast.LangFile
import java.lang.ref.SoftReference

private data class SourceFileComputable(
        val sourceFile: SourceFile,
        val astBuildingTask: AstBuildingTask
)


/**
 * Class designed to be filled completely, after which adding will be not possible
 */
class AstCache  {
    private var completed: Boolean = false
    private val fqnToSource: MutableMap<String, SourceFileComputable> = mutableMapOf()
    private val fqnToAst: MutableMap<String, SoftReference<LangFile>> = mutableMapOf()

    fun addSourceFile(sourceFile: SourceFile, precomputedAst: LangFile, astBuildingTask: AstBuildingTask) {
        if (completed) {
            throw IllegalStateException("It is impossible after completion to add entries to cache")
        }
        val qualifiedName = precomputedAst.classDecl.qualifiedName
        val computable = SourceFileComputable(sourceFile, astBuildingTask)
        fqnToSource[qualifiedName] = computable
        fqnToAst[qualifiedName] = SoftReference(precomputedAst)
    }

    fun getAst(qualifiedName: String) : LangFile? {
        val file = fqnToAst[qualifiedName]
        return file?.get() ?: fqnToSource[qualifiedName]?.astBuildingTask?.parse()
    }

    fun complete() {
        completed = true
    }
}