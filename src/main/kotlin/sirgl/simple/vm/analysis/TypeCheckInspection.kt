package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.BinaryOperatorType.*
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.stmt.LangIfStmt
import sirgl.simple.vm.ast.stmt.LangWhileStmt
import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.type.*

class TypeCheckInspection(override val problemHolder: ProblemHolder) : LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {
        override fun visitIfStmt(stmt: LangIfStmt) {
            stmt.condition.mustBeAssignableTo(BoolType)
        }

        override fun visitWhileStmt(stmt: LangWhileStmt) {
            stmt.condition.mustBeAssignableTo(BoolType)
        }

        override fun visitBinaryExpr(expr: LangBinaryExpr) {
            when (expr.opTypeBinary) {
                Eq, Lt, Le, Gt, Ge -> {
                    expr.left.mustBeAssignableTo(BoolType)
                    expr.right.mustBeAssignableTo(BoolType)
                }
                else -> {
                    expr.left.mustBeAssignableTo(I32Type)
                    expr.right.mustBeAssignableTo(I32Type)
                }
            }
        }

        override fun visitPrefixExpr(expr: LangPrefixExpr) {
            when (expr.prefixOperatorType) {
                PrefixOperatorType.Inversion -> expr.expr.mustBeAssignableTo(BoolType)
                else -> expr.expr.mustBeAssignableTo(I32Type)
            }
        }

        override fun visitCastExpr(expr: LangCastExpr) {
            val type = expr.targetType
            if (type !is ClassType) {
                problemHolder.registerProblem(expr, "Expected class type, but found type ${type.name}", expr.getSymbolSource())
            }
        }

        override fun visitCallExpr(expr: LangCallExpr) {
//            TODO
        }

        override fun visitElementAccessExpr(expr: LangElementAccessExpr) {
            val arrayExpr = expr.arrayExpr
            val arrayExprType = arrayExpr.type
            if (arrayExprType !is ArrayType) {
                problemHolder.registerProblem(arrayExpr, "Expected array type, but found ${arrayExprType.name}", expr.getSymbolSource())
            }
            expr.indexExpr.mustBeAssignableTo(I32Type)
        }

        override fun visitAssignExpr(expr: LangAssignExpr) {
            val leftType = expr.leftRef.type
            if (leftType === UnknownType) return // it is already should be reported somewhere
            expr.rightValue.mustBeAssignableTo(leftType)
        }

        override fun visitVarDecl(varDecl: LangVarDecl) {
            varDecl.initializer?.mustBeAssignableTo(varDecl.type)
        }

        private fun LangExpr.mustBeAssignableTo(type: LangType) {
            val exprType = this.type
            if (!exprType.isAssignableTo(type)) {
                problemHolder.registerProblem(this, "Expected type ${type.name}, but found ${exprType.name}", getSymbolSource())
            }
        }
    }
}