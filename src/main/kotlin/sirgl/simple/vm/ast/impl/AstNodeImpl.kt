package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode

abstract class AstNodeImpl(override val startOffset: Int, override val endOffset: Int) : AstNode {
    override val parent: AstNode? = null
    override lateinit var children: List<AstNode>
}