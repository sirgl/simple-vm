package sirgl.simple.vm.ast.visitor

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBinaryOperator
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.*

open class LangVisitor {
    fun visitAstNode(element: AstNode) {}

    // Common ast nodes

    fun visitBinaryOperator(operator: LangBinaryOperator) {
        visitAstNode(operator)
    }





    // Expressions

    fun visitExpr(expr: LangExpr) {
        visitAstNode(expr)
    }

    fun visitLiteral(literal: LangLiteralExpr) {
        visitExpr(literal)
    }

    fun visitIntLiteralExpr(intLiteralExpr: LangIntLiteralExpr) {
        visitLiteral(intLiteralExpr)
    }

    fun visitStringLiteralExpr(stringLiteralExpr: LangStringLiteralExpr) {
        visitLiteral(stringLiteralExpr)
    }

    fun visitBoolLiteralExpr(boolLiteralExpr: LangBoolLiteralExpr) {
        visitLiteral(boolLiteralExpr)
    }

    fun visitThisExpr(thisExpr: LangThisExpr) {
        visitExpr(thisExpr)
    }

    fun visitSuperExpr(superExpr: LangSuperExpr) {
        visitExpr(superExpr)
    }

    fun visitNullExpr(nullExpr: LangNullExpr) {
        visitExpr(nullExpr)
    }

    fun visitBinaryExpr(expr: LangBinaryExpr) {
        visitExpr(expr)
    }
    //TODO
}