package sirgl.simple.vm.driver.phases.passes

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.impl.LangConstructorImpl
import sirgl.simple.vm.ast.impl.LangFieldImpl
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

class SetupTopLevelPass : SingleVisitorAstPass() {
    override val visitor: LangVisitor = object: LangVisitor() {
        override fun visitField(field: LangField) {
            super.visitField(field)
            (field as LangFieldImpl).symbol = field.toSymbol()
            val classType = field.type as? ClassType ?: return
            val referenceElement = field.typeElement.reference ?: return
            val classSymbol = field.getScope().resolve(referenceElement.name, null) as? ClassSymbol ?: return
            classType.classSymbol = classSymbol // TODO generic way to resolve reference element
        }

        override fun visitConstructor(constructor: LangConstructor) {
            super.visitConstructor(constructor)
            (constructor as LangConstructorImpl).symbol = constructor.toSymbol()
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

        override fun visitMethod(method: LangMethod) {
            val classType = method.returnType as? ClassType ?: return
            val symbol = method.scope.resolve(classType.name, null)
            classType.classSymbol = symbol as? ClassSymbol ?: return
        }

        //        override fun visitExpr(expr: LangExpr) {
//            super.visitExpr(expr)
//            if (expr.parent !is LangCallExpr) return
//            expr as? LangReferenceExpr ?: return
//            val symbol = expr.resolve() as? MethodSymbol ?: return
//            val methodReferenceType = expr.type as? MethodReferenceType
//            if (methodReferenceType != null) {
//                methodReferenceType.methodSymbol = symbol
//            } else {
//                throw UnsupportedOperationException()
//            }
//        }
    }

    override val name: String = "Top level setup"
}