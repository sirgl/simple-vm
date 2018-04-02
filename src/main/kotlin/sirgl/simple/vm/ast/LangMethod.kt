package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.support.LangFunction
import sirgl.simple.vm.scope.Scope
import sirgl.simple.vm.signatures.MethodSignature

interface LangMethod : LangMember, Scope, LangFunction {
    val name: String
    val signature: MethodSignature
}