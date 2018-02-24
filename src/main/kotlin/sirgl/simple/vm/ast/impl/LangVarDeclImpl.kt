package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangVarDecl
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

abstract class LangVarDeclImpl(
        override val name: String,
        override val type: LangType,
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val initializer: LangExpr?
) : AstNodeImpl(startLexeme.startOffset, endLexeme.endOffset), LangVarDecl