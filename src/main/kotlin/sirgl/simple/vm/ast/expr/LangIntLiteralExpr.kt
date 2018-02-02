package sirgl.simple.vm.ast.expr

interface LangIntLiteralExpr : LangLiteralExpr {
    override val value: Int // actually, here may be bigger value, than Int.Max_value
}