package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.impl.AstNodeImpl
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

abstract class LangExprImpl(startOffset: Int, endOffset: Int) : LangExpr, AstNodeImpl(startOffset, endOffset) {
    constructor(startLexeme: Lexeme, endLexeme: Lexeme) : this(startLexeme.startOffset, endLexeme.endOffset)

    override var promoteToType: LangType? = null
    override lateinit var type: LangType
}