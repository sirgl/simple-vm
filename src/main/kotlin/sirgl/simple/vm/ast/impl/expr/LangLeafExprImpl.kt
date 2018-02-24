package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.lexer.Lexeme

abstract class LangLeafExprImpl(startOffset: Int, endOffset: Int) : LangExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    constructor(lexeme: Lexeme) : this(lexeme.startOffset, lexeme.endOffset)

    override var children: List<AstNode> = emptyList()
}