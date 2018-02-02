package sirgl.simple.vm.ast.ext

import sirgl.simple.vm.ast.AstNode
import java.lang.IllegalStateException

inline fun <reified T : AstNode> AstNode.findParentOfClass(parentClass: T)  = findParentOfClass<T>() as? T

inline fun <reified T : AstNode> AstNode.getParentOfClass() : T {
    val current: AstNode? = findParentOfClass<T>()
            ?: throw IllegalStateException("Expected class to be parent of $this, but not found")
    return current as T
}

inline fun <reified T : AstNode> AstNode.findParentOfClass(): AstNode? {
    var current = this.parent
    while (current != null && current !is T) {
        current = current.parent
    }
    return current
}