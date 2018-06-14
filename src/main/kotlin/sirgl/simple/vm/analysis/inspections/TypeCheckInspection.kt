package sirgl.simple.vm.analysis.inspections

import sirgl.simple.vm.analysis.LangInspection
import sirgl.simple.vm.analysis.ProblemHolder
import sirgl.simple.vm.ast.BinaryOperatorType.*
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.stmt.LangIfStmt
import sirgl.simple.vm.ast.stmt.LangWhileStmt
import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.resolve.symbols.MethodSymbol
import sirgl.simple.vm.resolve.symbols.constructor
import sirgl.simple.vm.type.*

class TypeCheckInspection(override val problemHolder: ProblemHolder) :
    LangInspection {
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
                problemHolder.registerProblem(
                    expr,
                    "Expected class type, but found type ${type.name}",
                    expr.getSymbolSource()
                )
            }
        }


        override fun visitCallExpr(expr: LangCallExpr) {
            super.visitCallExpr(expr)
            val callerType = expr.caller.type
            when (callerType) {
                is MethodReferenceType -> checkMethodCall(callerType, expr)
                is ClassType -> checkConstructorCall(callerType, expr)
                else -> problemHolder.registerProblem(
                    expr,
                    "Expected method reference type but found type ${callerType.name}",
                    expr.getSymbolSource()
                )
            }
        }

        override fun visitElementAccessExpr(expr: LangElementAccessExpr) {
            val arrayExpr = expr.arrayExpr
            val arrayExprType = arrayExpr.type
            if (arrayExprType !is ArrayType) {
                problemHolder.registerProblem(
                    arrayExpr,
                    "Expected array type, but found ${arrayExprType.name}",
                    expr.getSymbolSource()
                )
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
                problemHolder.registerProblem(
                    this,
                    "Expected type ${type.name}, but found ${exprType.name}",
                    getSymbolSource()
                )
            }
        }
    }

    private fun checkMethodCall(callerType: MethodReferenceType, expr: LangCallExpr) {
        val methodSymbol = callerType.methodSymbol
        checkFunction(expr, methodSymbol)
        return
    }

    private fun checkConstructorCall(callerType: ClassType, expr: LangCallExpr) {
        val constructor = callerType.classSymbol.constructor
        if (constructor == null) {
            problemHolder.registerProblem(
                expr,
                "Class ${callerType.classSymbol.qualifiedName} has no constructor",
                expr.getSymbolSource()
            )
        } else {
            checkFunction(expr, constructor)
        }
    }


    private fun checkFunction(
        expr: LangCallExpr,
        methodSymbol: MethodSymbol
    ) {
        val arguments = expr.arguments
        val parametersCount = methodSymbol.parameters.size
        val argumentsCount = arguments.size
        if (parametersCount != argumentsCount) {
            val parameterTypes = methodSymbol.parameters.joinToString(", ") { "${it.name}: ${it.type.name}" }
            val description = buildString {
                append("Method must have $parametersCount arguments ($parameterTypes), but found $argumentsCount")
                if (argumentsCount != 0) {
                    append("(")
                    append(arguments.joinToString(", ") { it.type.name })
                    append(")")
                }
            }
            problemHolder.registerProblem(expr, description, expr.getSymbolSource())
            return
        }
        for ((index, parameterSignature) in methodSymbol.parameters.withIndex()) {
            val argument = arguments[index]
            if (!argument.type.isAssignableTo(parameterSignature.type)) {
                val description =
                    "Argument type expected to be ${parameterSignature.type.name} but was ${argument.type.name}"
                problemHolder.registerProblem(argument, description, expr.getSymbolSource())
            }
        }
        return
    }
}