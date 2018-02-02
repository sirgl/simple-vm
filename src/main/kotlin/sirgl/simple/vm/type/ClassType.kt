package sirgl.simple.vm.type

import sirgl.simple.vm.ast.LangClass

class ClassType(val cls: LangClass) : LangType {
    override val name: String
        get() = cls.qualifiedName
}