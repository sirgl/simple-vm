package sirgl.simple.vm.ast.ext

import sirgl.simple.vm.ast.BinaryOperatorType

private val textToBinOperator = mapOf(
    "+" to BinaryOperatorType.Plus,
    "-" to BinaryOperatorType.Minus,
    "*" to BinaryOperatorType.Asterisk,
    "/" to BinaryOperatorType.Div,
    "%" to BinaryOperatorType.Percent,
    "<" to BinaryOperatorType.Lt,
    ">" to BinaryOperatorType.Gt,
    "<=" to BinaryOperatorType.Le,
    ">=" to BinaryOperatorType.Ge,
    ">=" to BinaryOperatorType.Eq
)

fun getOperatorTypeByText(opText: String): BinaryOperatorType {
    val type = textToBinOperator[opText]
    return type ?: throw IllegalStateException("Bad binary operator type: $type")
}