package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangReferenceElement
import sirgl.simple.vm.ast.LangTypeElement
import sirgl.simple.vm.ast.LangTypeElementSort
import sirgl.simple.vm.ast.LangTypeElementSort.*
import sirgl.simple.vm.ast.LangTypeElementSort.Array
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.*

class LangTypeElementImpl(
    startOffset: Int,
    endOffset: Int,
    startLine: Int,
    override val reference: LangReferenceElement?,
    override val coreType: LangTypeElement?,
    override val sort: LangTypeElementSort
) : AstNodeImpl(
    startOffset,
    endOffset,
    startLine
), LangTypeElement {


    constructor(referenceElement: LangReferenceElement) : this(
        referenceElement.startOffset,
        referenceElement.endOffset,
        referenceElement.startLine,
        referenceElement,
        null,
        Reference
    )

    constructor(baseTypeLexeme: Lexeme, sort: LangTypeElementSort) : this(
        baseTypeLexeme.startOffset,
        baseTypeLexeme.endOffset,
        baseTypeLexeme.line,
        null,
        null,
        sort
    )

    constructor(baseTypeElementImpl: LangTypeElementImpl, rBracket: Lexeme) : this(
        baseTypeElementImpl.startOffset,
        rBracket.endOffset,
        baseTypeElementImpl.startLine,
        null,
        baseTypeElementImpl,
        Array
    )

    override val children: List<AstNode> = makeChildren()

    private fun makeChildren(): List<AstNode> {
        val children = mutableListOf<AstNode>()
        if (reference != null) {
            children.add(reference)
        }
        if (coreType != null) {
            children.add(coreType)
        }
        return children
    }

    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) {
        visitor.visitTypeElement(this)
    }

    override val debugName = "TypeElement"
    override val type: LangType by lazy {
        when (this.sort) {
            I32 -> I32Type
            I8 -> I8Type
            Bool -> BoolType
            Void -> VoidType
            Reference -> ClassType(reference!!.fullName)
            Array -> ArrayType(coreType!!.type)
        }
    }

    override fun toString() = super.toString() + " sort: $sort"


}