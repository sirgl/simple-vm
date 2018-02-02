package sirgl.simple.vm.ast.expr

interface LangBoolLiteralExpr : LangLiteralExpr {
    override val value: Boolean
}