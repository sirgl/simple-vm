package sirgl.simple.vm.ast.expr

interface LangCharLiteralExpr : LangLiteralExpr {
    override val value: Byte
}