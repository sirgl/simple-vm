package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.lexer.Lexeme

abstract class AstNodeImpl(override val startOffset: Int, override val endOffset: Int, override val startLine: Int) : AstNode {
    constructor(startLexeme: Lexeme, endLexeme: Lexeme) : this(startLexeme.startOffset, endLexeme.endOffset, startLexeme.line)

    override fun toString() = "$debugName#$startLine@[$startOffset, $endOffset)"
}