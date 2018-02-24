package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr

interface LangPrefixExpr : LangExpr {
    val prefixOperatorType: PrefixOperatorType
    val expr: LangExpr
}

enum class PrefixOperatorType(val representation: String) {
    Plus("+"),
    Minus("-"),
    Inversion("!");

    fun from(str: String) {
        values().any { it.representation == str }
    }
}