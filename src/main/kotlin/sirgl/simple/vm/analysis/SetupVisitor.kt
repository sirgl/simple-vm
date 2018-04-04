package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.impl.LangFileImpl
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.resolve.Scope

/**
 * Visitor used to set up scope, resolve references,
 * set file's SourceFile,
 * set class type's target
 */
class SetupVisitor(
        val globalScope: Scope,
        val sourceFile: SourceFile
) : LangVisitor() {
    override fun visitFile(file: LangFile) {
        super.visitFile(file)
        file as LangFileImpl
        file.scope = globalScope
        file.sourceFile = sourceFile
    }

    override fun visitVarDeclStmt(stmt: LangVarDeclStmt) {
        super.visitVarDeclStmt(stmt)
        val scope = stmt.getScope()
        scope.register(stmt.signature)
    }

    override fun visitParameter(parameter: LangParameter) {
        super.visitParameter(parameter)
        val scope = parameter.getScope()
        scope.register(parameter.signature)
    }


    override fun visitAstNode(element: AstNode) {
        super.visitAstNode(element)
        if (element is Scope) {
            element.element = element
        }
    }

    override fun visitReferenceExpr(expr: LangReferenceExpr) {
        super.visitReferenceExpr(expr)
        expr.resolve()
    }

    // TODO resolve class type's target
}