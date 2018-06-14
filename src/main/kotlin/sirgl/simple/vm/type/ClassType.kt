package sirgl.simple.vm.type

import sirgl.simple.vm.resolve.symbols.ClassSymbol


class ClassType(override val name: String) : LangType {
    lateinit var classSymbol: ClassSymbol

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassType) return false

        if (classSymbol != other.classSymbol) return false

        return true
    }

    override fun hashCode(): Int {
        return classSymbol.hashCode()
    }
}

//fun LangClass.toType(): ClassType {
//    val type = ClassType(simpleName)
//    type.classSignature = this.signature
//    return type
//}