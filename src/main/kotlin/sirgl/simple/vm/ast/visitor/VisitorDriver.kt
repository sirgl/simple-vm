package sirgl.simple.vm.ast.visitor

import sirgl.simple.vm.ast.AstNode
import java.util.*

class VisitorDriver {
    private var stack: Deque<AstNode> = ArrayDeque()

    fun runVisitor(startNode: AstNode, visitor: LangVisitor) {
        acceptNode(startNode, visitor)
        while (stack.isNotEmpty()) {
            acceptNode(stack.pop(), visitor)
        }

    }

    private fun acceptNode(node: AstNode, visitor: LangVisitor) {
        node.accept(visitor)
        stack.addAll(node.children)
    }
}