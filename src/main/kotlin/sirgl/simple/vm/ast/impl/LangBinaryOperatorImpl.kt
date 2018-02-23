package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBinaryOperator
import sirgl.simple.vm.ast.BinaryOperatorType
import sirgl.simple.vm.ast.ext.getOperatorTypeByText
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangBinaryOperatorImpl(operatorText: String, startOffset: Int, endOffset: Int) : LangBinaryOperator, AstNodeImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override fun toString() = "BinaryExpr$rangeText()"

    override fun accept(visitor: LangVisitor) = visitor.visitBinaryOperator(this)

    override val typeBinary: BinaryOperatorType = getOperatorTypeByText(operatorText)
}