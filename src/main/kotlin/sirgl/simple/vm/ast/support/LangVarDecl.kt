package sirgl.simple.vm.ast.support

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.resolve.signatures.VariableSignature
import sirgl.simple.vm.type.LangType

interface LangVarDecl {
    val name: String
    val type: LangType
    val initializer: LangExpr?
    val signature: VariableSignature
}