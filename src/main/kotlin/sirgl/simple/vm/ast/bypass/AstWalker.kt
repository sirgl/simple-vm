package sirgl.simple.vm.ast.bypass

import sirgl.simple.vm.ast.AstNode

interface AstWalker {
    /**
     * Implement tree prepass
     */
    fun walkRecursive(node: AstNode, callback: (AstNode) -> Unit)
}

class SimpleWalker : AstWalker {
    override fun walkRecursive(node: AstNode, callback: (AstNode) -> Unit) {
        callback(node)
        for (child in node.children) {
            walkRecursive(child, callback)
        }
    }
}