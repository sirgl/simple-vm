package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.BinaryOperatorType.*
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.stmt.LangIfStmt
import sirgl.simple.vm.ast.stmt.LangWhileStmt
import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.scope.Scope
import sirgl.simple.vm.type.*
import kotlin.math.exp

// TODO check all scopes
class SemanticAnalysisInspection(override val errorHolder: ErrorHolder) : LangInspection {
    override val visitor: LangVisitor = object : LangVisitor() {
        override fun visitIfStmt(stmt: LangIfStmt) {
            expectType(stmt.condition, BoolType)
        }

        override fun visitWhileStmt(stmt: LangWhileStmt) {
            expectType(stmt.condition, BoolType)
        }

        override fun visitBinaryExpr(expr: LangBinaryExpr) {
            when (expr.opTypeBinary) {
                Eq, Lt, Le, Gt, Ge -> {
                    expectType(expr.left, BoolType)
                    expectType(expr.right, BoolType)
                }
                else -> {
                    expectI32OrPromote(expr.left)
                    expectI32OrPromote(expr.right)
                }
            }
        }

        override fun visitPrefixExpr(expr: LangPrefixExpr) {
            when (expr.prefixOperatorType) {
                PrefixOperatorType.Inversion -> expectType(expr.expr, BoolType)
                else -> expectI32OrPromote(expr.expr)
            }
        }

        override fun visitCallExpr(expr: LangCallExpr) {
            super.visitCallExpr(expr)
        }

        override fun visitCastExpr(expr: LangCastExpr) {
            val type = expr.targetType
            if (type !is ClassType) { // TODO arrays also
                errorHolder.registerProblem(expr, "Expected class type but found type ${type.name}")
            }
        }

        override fun visitElementAccessExpr(expr: LangElementAccessExpr) {
            expectArrayType(expr.arrayExpr)
            expectI32OrPromote(expr.indexExpr)
        }

        override fun visitVarDecl(varDecl: LangVarDecl) {
            // TODO compatibility of initializer and type
            super.visitVarDecl(varDecl)
        }

        override fun visitMethod(method: LangMethod) {

        }

        override fun visitAstNode(element: AstNode) {
            if (element is Scope) {
                for (name: String in element.getMultipleDeclarations()) {
                    errorHolder.registerProblem(element, "Multiple definitions of $name name")
                }
            }
        }

        override fun visitReferenceExpr(expr: LangReferenceExpr) {
            if (expr.resolve() == null) {
                errorHolder.registerProblem(expr, "Unresolved reference $expr")
            }
        }
    }

    fun expectI32OrPromote(expr: LangExpr) {
        if (expr.type == I8Type) {
            expr.promoteToType = I32Type
        } else  {
            expectType(expr, I32Type)
        }
    }

    fun expectArrayType(expression: LangExpr) {
        if (expression.type !is ArrayType) {
            errorHolder.registerProblem(expression, "Expected array type but found type ${expression.type.name}")
        }
    }

    fun expectClassType(expression: LangExpr) {
        if (expression.type !is ClassType) {
            errorHolder.registerProblem(expression, "Expected class type but found type ${expression.type.name}")
        }
    }

    fun expectType(expression: LangExpr, expectedType: LangType) {
        if (expression.type != expectedType) {
            errorHolder.registerProblem(expression, "Expected ${expectedType.name} but found type ${expression.type.name}")
        }
    }
}