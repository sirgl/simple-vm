package sirgl.simple.vm.type

import sirgl.simple.vm.ast.LangClass

class ClassType(override val name: String) : LangType {
    lateinit var cls: LangClass
}