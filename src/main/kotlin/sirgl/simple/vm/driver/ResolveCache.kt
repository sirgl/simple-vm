package sirgl.simple.vm.driver

import sirgl.simple.vm.ast.LangFile

private data class SourceFileComputable(
    val sourceFile: SourceFile,
    val astBuildingTask: AstBuildingTask
)

data class SourceFileInfo(
    val sourceFile: SourceFile,
    val ast: LangFile
)

/**
 * Class designed to be filled completely, after which adding will be not possible
 */
class ResolveCache {
//    private var completed: Boolean = false
//    private val fqnToSource: MutableMap<String, SourceFileComputable> = mutableMapOf()
//    private val fqnToAst: MutableMap<String, SoftReference<LangFile>> = mutableMapOf()
//    private val fqnToSignature: MutableMap<String, ClassSignature> = mutableMapOf()
//
//    fun addSourceFile(sourceFile: SourceFile, precomputedAst: LangFile, astBuildingTask: AstBuildingTask) {
//        if (completed) {
//            throw IllegalStateException("It is impossible after completion to add entries to cache")
//        }
//        val qualifiedName = precomputedAst.classDecl.qualifiedName
//        val computable = SourceFileComputable(sourceFile, astBuildingTask)
//        fqnToSource[qualifiedName] = computable
//        fqnToAst[qualifiedName] = SoftReference(precomputedAst)
//        val classDecl = precomputedAst.classDecl
//        val signature = classDecl.toSignature(sourceFile)
//        fqnToSignature[qualifiedName] = signature
//    }
//
//    fun getAst(qualifiedName: String): LangFile? {
//        val file = fqnToAst[qualifiedName]
//        return file?.get() ?: fqnToSource[qualifiedName]?.astBuildingTask?.parse()
//    }
//
//    fun resolveClass(qualifiedName: String): ClassSignature? {
//        return fqnToSignature[qualifiedName]
//    }
//
//    fun complete() {
//        completed = true
//    }
//
//    fun getAllFiles()  = buildSequence {
//        for ((qualifiedName, fileReference) in fqnToAst) {
//            val computable = fqnToSource[qualifiedName]!!
//            val file = fileReference.get() ?: computable.astBuildingTask.parse()!!
//            yield(SourceFileInfo(computable.sourceFile, file))
//        }
//    }
}