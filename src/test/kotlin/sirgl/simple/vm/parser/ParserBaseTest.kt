package sirgl.simple.vm.parser

import sirgl.simple.vm.FileTestCase
import sirgl.simple.vm.ast.ext.prettyText
import sirgl.simple.vm.lexer.HandwrittenLangLexer

abstract class ParserBaseTest : FileTestCase<String>() {
    private val parser = HandwrittenLangParser()
    private val lexer = HandwrittenLangLexer()

    override fun applyAction(text: String): String {
        val parseResult = parser.parse(lexer.tokenize(text))
        return when {
            parseResult.fail != null -> parseResult.fail!!.toString()
            else -> parseResult.ast!!.prettyText()
        }
    }
}