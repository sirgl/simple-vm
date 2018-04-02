package sirgl.simple.vm.type

import sirgl.simple.vm.signatures.MethodSignature

class MethodReferenceType(
        override val name: String
) : LangType {
    lateinit var method: MethodSignature
}