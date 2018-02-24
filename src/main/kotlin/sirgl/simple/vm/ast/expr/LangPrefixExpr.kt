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

    companion object {
        fun from(str: String) = values().first { it.representation == str }
    }
}