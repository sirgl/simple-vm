package sirgl.simple.vm.driver.phases.passes

import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangField
import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getParentOfClass
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.impl.LangFieldImpl
import sirgl.simple.vm.ast.impl.LangParameterImpl
import sirgl.simple.vm.ast.impl.stmt.LangVarDeclStmtImpl
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.common.CommonClassNames
import sirgl.simple.vm.common.CommonClassTypes
import sirgl.simple.vm.driver.phases.SingleVisitorAstPass
import sirgl.simple.vm.resolve.symbols.ClassSymbol
import sirgl.simple.vm.resolve.symbols.ClassSymbolImpl
import sirgl.simple.vm.resolve.symbols.MethodSymbol
import sirgl.simple.vm.resolve.symbols.toSymbol
import sirgl.simple.vm.type.ClassType
import sirgl.simple.vm.type.MethodReferenceType

class SetupPass : SingleVisitorAstPass() {
    override val visitor: LangVisitor = object : LangVisitor() {
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

        override fun visitField(field: LangField) {
            super.visitField(field)
            (field as LangFieldImpl).symbol = field.toSymbol()
            val classType = field.type as? ClassType ?: return
            val referenceElement = field.typeElement.reference ?: return
            val classSymbol = field.getScope().resolve(referenceElement.name, null) as? ClassSymbol ?: return
            classType.classSymbol = classSymbol // TODO generic way to resolve reference element
        }

        override fun visitReferenceExpr(expr: LangReferenceExpr) {
            super.visitReferenceExpr(expr)
            val symbol = expr.resolve() as? ClassSymbol ?: return
            symbol.type.classSymbol = symbol
        }

        override fun visitCallExpr(expr: LangCallExpr) {
            super.visitCallExpr(expr)
            val caller = expr.caller as? LangReferenceExpr ?: return
            val symbol = caller.resolve() as? MethodSymbol ?: return // No return, make error
            (caller.type as? MethodReferenceType)?.methodSymbol = symbol
        }

        override fun visitClass(cls: LangClass) {
            super.visitClass(cls)
            val parentClassReferenceElement = cls.parentClassReferenceElement
            val currentClassSymbol = cls.symbol
            val classSymbolImpl = currentClassSymbol as ClassSymbolImpl
            if (parentClassReferenceElement != null) {
                if (parentClassReferenceElement.qualifier != null) {
                    TODO()
                }
                val symbol = cls.symbol.packageSymbol.resolve(parentClassReferenceElement.name, null)
                val parentClassSymbol = symbol as? ClassSymbol ?: return
                classSymbolImpl.parentClassSymbol = parentClassSymbol
            } else {
                if (classSymbolImpl.qualifiedName != CommonClassNames.LANG_OBJECT) {
                    classSymbolImpl.parentClassSymbol = CommonClassTypes.LANG_OBJECT.classSymbol
                }
            }
        }
    }

    override val name: String = "Setup"
}