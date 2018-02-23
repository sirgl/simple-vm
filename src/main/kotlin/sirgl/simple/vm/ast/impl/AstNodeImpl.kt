package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.lexer.Lexeme

abstract class AstNodeImpl(override val startOffset: Int, override val endOffset: Int) : AstNode {
    constructor(startLexeme: Lexeme, endLexeme: Lexeme) : this(startLexeme.startOffset, endLexeme.endOffset)

    override lateinit var children: List<AstNode>
}