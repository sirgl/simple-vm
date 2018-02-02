package sirgl.simple.vm.ast.expr

interface LangStringLiteralExpr : LangLiteralExpr {
    override val value: String
}