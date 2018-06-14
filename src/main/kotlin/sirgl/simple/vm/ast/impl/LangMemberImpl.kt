package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangMember
import sirgl.simple.vm.lexer.Lexeme

abstract class LangMemberImpl(startLexeme: Lexeme, endLexeme: Lexeme) :
    AstNodeImpl(startLexeme, endLexeme), LangMember {
    override lateinit var parent: LangClass

    override val enclosingClass: LangClass by lazy { parent }
}