package sirgl.simple.vm.ast.bypass

import sirgl.simple.vm.ast.AstNode

interface AstWalker {
    /**
     * Implement tree prepass
     */
    fun prepassRecursive(node: AstNode, callback: (AstNode) -> Unit)

    fun postpassRecursive(node: AstNode, callback: (AstNode) -> Unit)
    fun pass(node: AstNode, prePassCallback: (AstNode) -> Unit, postPassCallback: (AstNode) -> Unit)
}

class SimpleWalker : AstWalker {
    override fun pass(node: AstNode, prePassCallback: (AstNode) -> Unit, postPassCallback: (AstNode) -> Unit) {
        prePassCallback(node)
        for (child in node.children) {
            pass(child, prePassCallback, postPassCallback)
        }
        postPassCallback(node)
    }

    override fun postpassRecursive(node: AstNode, callback: (AstNode) -> Unit) {
        for (child in node.children) {
            postpassRecursive(child, callback)
        }
        callback(node)
    }

    override fun prepassRecursive(node: AstNode, callback: (AstNode) -> Unit) {
        callback(node)
        for (child in node.children) {
            prepassRecursive(child, callback)
        }
    }
}