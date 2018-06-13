package sirgl.simple.vm.common

import sirgl.simple.vm.type.ClassType

const val defaultSourceFileExtension = "lang"
const val defaultCompiledFileExtension = "clang"

object CommonClassNames {
    val LANG_OBJECT = "lang.Object"
    val LANG_STRING = "lang.String"
    val LANG_I32 = "lang.I32"
    val LANG_I8 = "lang.I8"
    val LANG_BOOL = "lang.Bool"
}

// TODO resolve Common Class Types
object CommonClassTypes {
    val types = mutableListOf<ClassType>()

    val LANG_OBJECT = register(CommonClassNames.LANG_OBJECT)
    val LANG_STRING = register(CommonClassNames.LANG_STRING)
    val LANG_I32 = register(CommonClassNames.LANG_I32)
    val LANG_I8 = register(CommonClassNames.LANG_I8)
    val LANG_BOOLEAN = register(CommonClassNames.LANG_BOOL)

    // Needed in type injection phase to register all these types

    private fun CommonClassTypes.register(classType: String) : ClassType {
        val clsType = ClassType(classType)
        types.add(clsType)
        return clsType
    }
}