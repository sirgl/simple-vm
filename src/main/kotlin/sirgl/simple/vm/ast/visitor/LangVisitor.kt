package sirgl.simple.vm.ast.visitor

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.stmt.*

abstract class LangVisitor {
    open fun visitAstNode(element: AstNode) {}

    // Common ast nodes

    open fun visitBinaryOperator(operator: LangBinaryOperator) {
        visitAstNode(operator)
    }


    open fun visitFile(file: LangFile) {
        visitAstNode(file)
    }

    open fun visitVarDecl(varDecl: LangVarDecl) {
        visitAstNode(varDecl)
    }

    // Top level

    open fun visitPackageDecl(packageDecl: LangPackageDecl) {
        visitAstNode(packageDecl)
    }

    open fun visitClass(cls: LangClass) {
        visitAstNode(cls)
    }

    // Members

    open fun visitMember(member: LangMember) {
        visitAstNode(member)
    }

    open fun visitMethod(method: LangMethod) {
        visitMember(method)
    }

    open fun visitField(field: LangField) {
        visitMember(field)
        visitVarDecl(field) // TODO Note here visitAstNode will be invoked 2 times
    }


    // Other

    open fun visitParameter(parameter: LangParameter) {
        visitVarDecl(parameter)
    }

    open fun visitBlock(block: LangBlock) {
        visitAstNode(block)
    }

    open fun visitCatchClause(catchClause: LangCatchClause) {
        visitAstNode(catchClause)
    }

    // Statements

    open fun visitStmt(stmt: LangStmt) {
        visitAstNode(stmt)
    }

    open fun visitExprStmt(stmt: LangExprStmt) {
        visitStmt(stmt)
    }

    open fun visitIfStmt(stmt: LangIfStmt) {
        visitStmt(stmt)
    }

    open fun visitTryStmt(stmt: LangTryStmt) {
        visitStmt(stmt)
    }

    open fun visitReturnStmt(stmt: LangReturnStmt) {
        visitStmt(stmt)
    }

    open fun visitContinueStmt(stmt: LangContinueStmt) {
        visitStmt(stmt)
    }

    open fun visitBreakStmt(stmt: LangBreakStmt) {
        visitStmt(stmt)
    }

    open fun visitWhileStmt(stmt: LangWhileStmt) {
        visitStmt(stmt)
    }


    // Expressions

    open fun visitExpr(expr: LangExpr) {
        visitAstNode(expr)
    }

    open fun visitReferenceExpr(expr: LangReferenceExpr) {
        visitExpr(expr)
    }

    open fun visitPrefixExpr(expr: LangPrefixExpr) {
        visitExpr(expr)
    }

    open fun visitLiteral(literal: LangLiteralExpr) {
        visitExpr(literal)
    }

    open fun visitIntLiteralExpr(intLiteralExpr: LangIntLiteralExpr) {
        visitLiteral(intLiteralExpr)
    }

    open fun visitStringLiteralExpr(stringLiteralExpr: LangStringLiteralExpr) {
        visitLiteral(stringLiteralExpr)
    }

    open fun visitBoolLiteralExpr(boolLiteralExpr: LangBoolLiteralExpr) {
        visitLiteral(boolLiteralExpr)
    }

    open fun visitThisExpr(thisExpr: LangThisExpr) {
        visitExpr(thisExpr)
    }

    open fun visitSuperExpr(superExpr: LangSuperExpr) {
        visitExpr(superExpr)
    }

    open fun visitNullExpr(nullExpr: LangNullExpr) {
        visitExpr(nullExpr)
    }

    open fun visitBinaryExpr(expr: LangBinaryExpr) {
        visitExpr(expr)
    }
    //TODO
}