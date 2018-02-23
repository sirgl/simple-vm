package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.LangVarDecl
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

abstract class LangVarDeclImpl(
        override val name: String,
        override val type: LangType,
        startLexeme: Lexeme,
        endLexeme: Lexeme
) : AstNodeImpl(startLexeme.startOffset, endLexeme.endOffset), LangVarDecl