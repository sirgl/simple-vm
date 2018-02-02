package sirgl.simple.vm.ast

interface LangBinaryOperator : AstNode {
    val typeBinary: BinaryOperatorType
}

enum class BinaryOperatorType(val text: String) {
    Plus("+"),
    Minus("-"),
    Asterisk("*"),
    Div("/"),
    Percent("%"),

    Lt("<"),
    Le("<="),
    Gt(">"),
    Ge(">="),
    Eq("=="),
}