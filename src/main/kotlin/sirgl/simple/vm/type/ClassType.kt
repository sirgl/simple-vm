package sirgl.simple.vm.type

import sirgl.simple.vm.resolve.symbols.ClassSymbol


class ClassType(override val name: String) : LangType {
    lateinit var classSymbol: ClassSymbol
}

//fun LangClass.toType(): ClassType {
//    val type = ClassType(simpleName)
//    type.classSignature = this.signature
//    return type
//}