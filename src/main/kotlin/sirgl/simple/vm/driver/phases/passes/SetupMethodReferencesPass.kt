package sirgl.simple.vm.driver.phases.passes

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.phases.SingleVisitorAstPass
import sirgl.simple.vm.resolve.symbols.ClassSymbol
import sirgl.simple.vm.resolve.symbols.MethodSymbol
import sirgl.simple.vm.type.ClassType
import sirgl.simple.vm.type.MethodReferenceType

class SetupMethodReferencesPass : SingleVisitorAstPass() {
    override val visitor: LangVisitor = object : LangVisitor() {
        override fun visitReferenceExpr(expr: LangReferenceExpr) {
//                type.methodSymbol
//            }
            super.visitReferenceExpr(expr)

            val resolve = expr.resolve()
            val symbol = resolve as? ClassSymbol ?: return
            symbol.type.classSymbol = symbol

        }

        override fun visitCallExpr(expr: LangCallExpr) {
            super.visitCallExpr(expr)
            val classType = expr.type as? ClassType ?: return
            val methodReferenceType = expr.caller.type as? MethodReferenceType
            val type = methodReferenceType?.methodSymbol?.returnType as? ClassType ?: return
            classType.classSymbol = type.classSymbol
        }

        override fun visitExpr(expr: LangExpr) {
            super.visitExpr(expr)
            if (expr.parent !is LangCallExpr) return
            expr as? LangReferenceExpr ?: return
            val symbol = expr.resolve()
            when (symbol) {
                is MethodSymbol -> {
                    val methodReferenceType = expr.type as? MethodReferenceType
                    if (methodReferenceType != null) {
                        methodReferenceType.methodSymbol = symbol
                    } else {
                        throw UnsupportedOperationException()
                    }
                }
                is ClassSymbol -> {
                    val classSymbol = symbol as ClassSymbol

                }
            }

        }
    }

    override val name: String = "Setup method references symbols"
}