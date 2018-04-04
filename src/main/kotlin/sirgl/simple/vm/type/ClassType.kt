package sirgl.simple.vm.type

import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.resolve.signatures.ClassSignature

class ClassType(override val name: String) : LangType {
    lateinit var classSignature: ClassSignature
}

fun LangClass.toType(): ClassType {
    val type = ClassType(simpleName)
    type.classSignature = this.signature
    return type
}