package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.lexer.Lexeme

abstract class LangLeafExprImpl(lexeme: Lexeme) : LangExprImpl(lexeme, lexeme) {
    override var children: List<AstNode> = emptyList()
}