package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.support.LangFunction
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.signatures.MethodSignature

interface LangMethod : LangMember, Scope, LangFunction {
    val name: String
    val signature: MethodSignature
}