package sirgl.simple.vm.ast.ext

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.scope.Scope
import java.lang.IllegalStateException

inline fun <reified T> AstNode.findParentOfClass(parentClass: T) = findParentOfClass<T>()

inline fun <reified T> AstNode.getParentOfClass(): T {
    val current: T? = findParentOfClass<T>()
            ?: throw IllegalStateException("Expected class to be parent of $this, but not found")
    return current as T
}

inline fun <reified T> AstNode.findParentOfClass(): T? {
    var current = this.parent
    while (current != null && current !is T) {
        current = current.parent
    }
    return current as T
}

fun AstNode.getScope() = getParentOfClass<Scope>()
fun AstNode.getFile() = getParentOfClass<LangFile>()
fun AstNode.getSourceFile() = getParentOfClass<LangFile>().sourceFile
fun AstNode.getClass() = getParentOfClass<LangClass>()