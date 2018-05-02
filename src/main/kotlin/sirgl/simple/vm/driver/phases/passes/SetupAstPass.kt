package sirgl.simple.vm.driver.phases.passes

import sirgl.simple.vm.ast.LangField
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.driver.phases.AstPass

class SetupAstPass(context: CompilerContext) : AstPass(context) {
    override val visitors: List<LangVisitor> = listOf(
        object: LangVisitor() {
            override fun visitVarDeclStmt(stmt: LangVarDeclStmt) {
                super.visitVarDeclStmt(stmt)
                stmt.getScope().register(stmt.symbol, stmt)
            }

            override fun visitParameter(parameter: LangParameter) {
                super.visitParameter(parameter)
                parameter.getScope().register(parameter.symbol, parameter)
            }

            override fun visitField(field: LangField) {
                super.visitField(field)
                field.getScope().register(field.symbol, field)
            }

            override fun visitReferenceExpr(expr: LangReferenceExpr) {
                super.visitReferenceExpr(expr)
                expr.resolve()
            }

            // TODO resolve reference also
        }
    )
}