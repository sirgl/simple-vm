package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.impl.LangFileImpl
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.scope.Scope

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
        file as LangFileImpl
        file.scope = globalScope
        file.sourceFile = sourceFile
    }

    override fun visitClass(cls: LangClass) {
        val signature = cls.signature
        for (fieldSignature in signature.fieldSignatures) {
            cls.register(fieldSignature)
        }
        for (methodSignature in signature.methodSignatures) {
            cls.register(methodSignature)
        }
    }

    override fun visitVarDeclStmt(stmt: LangVarDeclStmt) {
        stmt.getScope().register(stmt.signature)
    }

    // TODO resolve class type's target
}