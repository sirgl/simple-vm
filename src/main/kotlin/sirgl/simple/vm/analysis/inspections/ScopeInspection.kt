package sirgl.simple.vm.analysis.inspections

import sirgl.simple.vm.analysis.LangInspection
import sirgl.simple.vm.analysis.ProblemHolder
import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.resolve.Scoped

class ScopeInspection(override val problemHolder: ProblemHolder) :
        LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {
        override fun visitAstNode(element: AstNode) {
            if (element is Scoped) {
                val scope = element.scope
                for ((name, symbols) in scope.getMultipleDeclarations()) {
                    var locationsAvailable = false
                    val locationsText = buildString {
                        append("Available locations: ")
                        for (symbol in symbols) {
                            if (symbol is AstNode) {
                                locationsAvailable = true
                                append(symbol.toString())
                                append(" ")
                            }
                        }
                    }
                    var text = "Multiple definitions of $name name"
                    if (locationsAvailable) {
                        text = "$text. $locationsText"
                    }
                    problemHolder.registerProblem(element, text, element.getSymbolSource())
                }
            }
        }
    }
}