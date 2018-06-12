package sirgl.simple.vm.driver.phases.passes

import sirgl.simple.vm.ast.LangField
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.impl.LangFieldImpl
import sirgl.simple.vm.ast.impl.LangParameterImpl
import sirgl.simple.vm.ast.impl.expr.LangCallExprImpl
import sirgl.simple.vm.ast.impl.stmt.LangVarDeclStmtImpl
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.phases.SingleVisitorAstPass
import sirgl.simple.vm.resolve.symbols.MethodSymbol
import sirgl.simple.vm.resolve.symbols.toSymbol
import sirgl.simple.vm.type.MethodReferenceType

class SetupPass : SingleVisitorAstPass() {
    override val visitor: LangVisitor = object: LangVisitor() {
        override fun visitVarDeclStmt(stmt: LangVarDeclStmt) {
            super.visitVarDeclStmt(stmt)
            (stmt as LangVarDeclStmtImpl).symbol = stmt.toSymbol(stmt)
            stmt.getScope().register(stmt.symbol, stmt)
        }

        override fun visitParameter(parameter: LangParameter) {
            super.visitParameter(parameter)
            (parameter as LangParameterImpl).symbol = parameter.toSymbol()
            parameter.getScope().register(parameter.symbol, parameter)
        }

        override fun visitField(field: LangField) {
            super.visitField(field)
            (field as LangFieldImpl).symbol = field.toSymbol()
        }

        override fun visitReferenceExpr(expr: LangReferenceExpr) {
            super.visitReferenceExpr(expr)
            expr.resolve()
        }

        override fun visitCallExpr(expr: LangCallExpr) {
            val caller = expr.caller as? LangReferenceExpr ?: return // TODO make assertion and stop if error occurred in some pass
            val symbol = caller.resolve() as? MethodSymbol ?: return // No return, make error
            (caller.type as? MethodReferenceType)?.methodSymbol = symbol
        }
    }

    override val name: String = "Setup"
}