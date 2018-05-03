package sirgl.simple.vm.common

import sirgl.simple.vm.type.ClassType

const val defaultSourceFileExtension = "lang"
const val defaultCompiledFileExtension = "clang"

object CommonClassNames {
    val LANG_STRING = "lang.String"
    val LANG_I32 = "lang.I32"
    val LANG_I8 = "lang.I8"
    val LANG_BOOLEAN = "lang.Boolean"
}

// TODO resolve Common Class Types
object CommonClassTypes {
    val LANG_STRING = ClassType(CommonClassNames.LANG_STRING)
    val LANG_I32 = ClassType(CommonClassNames.LANG_I32)
    val LANG_I8 = ClassType(CommonClassNames.LANG_I8)
    val LANG_BOOLEAN = ClassType(CommonClassNames.LANG_BOOLEAN)
}