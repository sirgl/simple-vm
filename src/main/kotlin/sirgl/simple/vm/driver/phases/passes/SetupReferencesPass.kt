package sirgl.simple.vm.driver.phases.passes

import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.impl.stmt.LangVarDeclStmtImpl
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.phases.SingleVisitorAstPass
import sirgl.simple.vm.resolve.symbols.ClassSymbol
import sirgl.simple.vm.resolve.symbols.MethodSymbol
import sirgl.simple.vm.resolve.symbols.toSymbol
import sirgl.simple.vm.type.ClassType
import sirgl.simple.vm.type.MethodReferenceType

class SetupReferencesPass : SingleVisitorAstPass() {
    override val visitor: LangVisitor = object: LangVisitor() {
        override fun visitVarDeclStmt(stmt: LangVarDeclStmt) {
            super.visitVarDeclStmt(stmt)
            (stmt as LangVarDeclStmtImpl).symbol = stmt.toSymbol(stmt)
            stmt.getScope().register(stmt.symbol, stmt)
            val classType = stmt.type as? ClassType ?: return
            val referenceElement = stmt.typeElement.reference ?: return
            val classSymbol = stmt.getScope().resolve(referenceElement.name, null) as? ClassSymbol ?: return
            classType.classSymbol = classSymbol // TODO generic way to resolve reference element
        }

        override fun visitParameter(parameter: LangParameter) {
            super.visitParameter(parameter)
            parameter.getScope().register(parameter.symbol, parameter)
        }

//        override fun visitReferenceExpr(expr: LangReferenceExpr) {
//            super.visitReferenceExpr(expr)
//            val symbol = expr.resolve() as? ClassSymbol ?: return
//            symbol.type.classSymbol = symbol
//        }



//        override fun visitCallExpr(expr: LangCallExpr) {
//            super.visitCallExpr(expr)
//            val caller = expr.caller as? LangReferenceExpr ?:
//                throw UnsupportedOperationException()
//
//            // TODO dirty hack! Replace with proper bypass
//            // bypass caller
//
//
//            val symbol = caller.resolve() as? MethodSymbol ?: return // need for error recovery
//
//            val methodReferenceType = caller.type as? MethodReferenceType
//            if (methodReferenceType != null) {
//                methodReferenceType.methodSymbol = symbol
//            } else {
//                throw UnsupportedOperationException()
//            }
//        }
    }

    override val name: String = "References setup"
}