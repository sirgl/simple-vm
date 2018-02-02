package sirgl.simple.vm.parser

import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.lexer.Lexeme

interface LangParser {
    fun parse(lexemes: List<Lexeme>): LangFile
}

class HandwrittenLangParser : LangParser {
    override fun parse(lexemes: List<Lexeme>): LangFile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}