package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.impl.AstNodeImpl
import sirgl.simple.vm.type.LangType

abstract class LangLeafExprImpl(startOffset: Int, endOffset: Int) : LangExprImpl(startOffset, endOffset) {
    override var children: List<AstNode> = emptyList()
}