package sirgl.simple.vm.parser

import org.junit.Assert
import sirgl.simple.vm.FileTestCase
import sirgl.simple.vm.ast.ext.prettyText
import sirgl.simple.vm.lexer.HandwrittenLangLexer

abstract class ParserBaseTest : FileTestCase<String>() {
    private val parser = HandwrittenLangParser()
    private val lexer = HandwrittenLangLexer()

    override fun applyAction(text: String): String {
        val parseResult = parser.parse(lexer.tokenize(text))
        return when {
            parseResult.fail != null -> {
                val fail = parseResult.fail!!
                if (!failExpected) {
                    fail.parseException?.printStackTrace()
                }
                fail.toString()
            }
            else -> {
                val prettyText = parseResult.ast!!.prettyText()
                if (failExpected) {
                    Assert.fail("Fail expected, but parsed successfully")
                }
                prettyText
            }
        }
    }

    abstract val failExpected: Boolean
}