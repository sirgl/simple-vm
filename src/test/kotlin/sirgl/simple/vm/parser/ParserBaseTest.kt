package sirgl.simple.vm.parser

import org.junit.Assert
import sirgl.simple.vm.FileTestCase
import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.ext.prettyText
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.ast.visitor.VisitorDriver
import sirgl.simple.vm.lexer.HandwrittenLangLexer

abstract class ParserBaseTest : FileTestCase<String>() {
    private val parser = HandwrittenLangParser()
    private val lexer = HandwrittenLangLexer()
    private val visitorDriver = VisitorDriver()

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
                val ast = parseResult.ast!!
                visitorDriver.runVisitor(ast, ParentCheckingVisitor())
                val prettyText = ast.prettyText()
                if (failExpected) {
                    Assert.fail("Fail expected, but parsed successfully")
                }
                prettyText
            }
        }
    }

    class ParentCheckingVisitor : LangVisitor() {
        override fun visitAstNode(element: AstNode) {
            if (element !is LangFile) {
                val parent = try {
                    element.parent
                } catch (e: UninitializedPropertyAccessException) {
                    null
                }
                if (parent == null) {
                    Assert.fail("Element $element has no parent")
                }
            }
        }
    }

    abstract val failExpected: Boolean
}