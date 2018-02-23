package sirgl.simple.vm.ast

interface LangBinaryOperator : AstNode {
    val typeBinary: BinaryOperatorType

    override val parent: AstNode
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