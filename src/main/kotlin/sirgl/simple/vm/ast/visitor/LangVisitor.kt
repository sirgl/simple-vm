package sirgl.simple.vm.ast.visitor

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.expr.*

open class LangVisitor {
    fun visitAstNode(element: AstNode) {}

    // Common ast nodes

    fun visitBinaryOperator(operator: LangBinaryOperator) {
        visitAstNode(operator)
    }


    fun visitFile(file: LangFile) {
        visitAstNode(file)
    }

    fun visitVarDecl(varDecl: LangVarDecl) {
        visitAstNode(varDecl)
    }

    // Top level

    fun visitPackageDecl(packageDecl: LangPackageDecl) {
        visitAstNode(packageDecl)
    }

    fun visitClass(cls: LangClass) {
        visitAstNode(cls)
    }

    // Members

    fun visitMember(member: LangMember) {
        visitAstNode(member)
    }

    fun visitMethod(method: LangMethod) {
        visitMember(method)
    }

    fun visitField(field: LangField) {
        visitMember(field)
        visitVarDecl(field) // TODO Note here visitAstNode will be invoked 2 times
    }


    // Other

    fun visitParameter(parameter: LangParameter) {
        visitVarDecl(parameter)
    }

    fun visitBlock(block: LangBlock) {
        visitAstNode(block)
    }

    // Statements

    fun visitStmt(stmt: LangStmt) {
        visitAstNode(stmt)
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