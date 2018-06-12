package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.visitor.LangVisitor

class SemanticAnalysisInspection(override val problemHolder: ProblemHolder) : LangInspection {
    override val visitor: LangVisitor = object: LangVisitor() {

    }


    //    override val visitor: LangVisitor = object : LangVisitor() {
//        override fun visitIfStmt(stmt: LangIfStmt) {
//            super.visitIfStmt(stmt)
//            expectType(stmt.condition, BoolType)
//        }
//
//        override fun visitWhileStmt(stmt: LangWhileStmt) {
//            super.visitWhileStmt(stmt)
//            expectType(stmt.condition, BoolType)
//        }
//
//        override fun visitBinaryExpr(expr: LangBinaryExpr) {
//            super.visitBinaryExpr(expr)
//            when (expr.opTypeBinary) {
//                Eq, Lt, Le, Gt, Ge -> {
//                    expectType(expr.left, BoolType)
//                    expectType(expr.right, BoolType)
//                }
//                else -> {
//                    expectI32OrPromote(expr.left)
//                    expectI32OrPromote(expr.right)
//                }
//            }
//        }
//
//        override fun visitPrefixExpr(expr: LangPrefixExpr) {
//            super.visitPrefixExpr(expr)
//            when (expr.prefixOperatorType) {
//                PrefixOperatorType.Inversion -> expectType(expr.expr, BoolType)
//                else -> expectI32OrPromote(expr.expr)
//            }
//        }
//

//
//        override fun visitCastExpr(expr: LangCastExpr) {
//            super.visitCastExpr(expr)
//            val type = expr.targetType
//            if (type !is ClassType) { // TODO arrays also
//                errorHolder.registerProblem(expr, "Expected class type but found type ${type.name}")
//            }
//
//        }
//
//        override fun visitElementAccessExpr(expr: LangElementAccessExpr) {
//            super.visitElementAccessExpr(expr)
//            expectArrayType(expr.arrayExpr)
//            expectI32OrPromote(expr.indexExpr)
//        }
//
//        override fun visitVarDecl(varDecl: LangVarDecl) {
//            // TODO compatibility of initializer and type, also assignment
//            super.visitVarDecl(varDecl)
//        }
//
//        override fun visitMethod(method: LangMethod) {
//            super.visitMethod(method)
//            if (method.isNative && method.block != null) {
//                errorHolder.registerProblem(method, "Method declared as native must have no body")
//            }
//            if (!method.isNative && method.block == null) {
//                errorHolder.registerProblem(method, "Only native methods can have no body")
//            }
//        }
//
//        override fun visitAstNode(element: AstNode) {
//            super.visitAstNode(element)
//            if (element is Scope) {
//                for (name: String in element.getMultipleDeclarations()) {
//                    errorHolder.registerProblem(element, "Multiple definitions of $name name")
//                }
//            }
//        }
//
//    }
//
//    fun expectI32OrPromote(expr: LangExpr) {
//        if (expr.type == I8Type) {
//            expr.promoteToType = I32Type
//        } else  {
//            expectType(expr, I32Type)
//        }
//    }
//
//    fun expectArrayType(expression: LangExpr) {
//        if (expression.type !is ArrayType) {
//            errorHolder.registerProblem(expression, "Expected array type but found type ${expression.type.name}")
//        }
//    }
//
//    fun expectClassType(expression: LangExpr) {
//        if (expression.type !is ClassType) {
//            errorHolder.registerProblem(expression, "Expected class type but found type ${expression.type.name}")
//        }
//    }
//
//    fun expectType(expression: LangExpr, expectedType: LangType) {
//        if (expression.type != expectedType) {
//            errorHolder.registerProblem(expression, "Expected ${expectedType.name} but found type ${expression.type.name}")
//        }
//    }
}