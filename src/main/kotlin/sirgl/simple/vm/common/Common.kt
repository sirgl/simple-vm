package sirgl.simple.vm.common

import sirgl.simple.vm.type.ClassType

const val defaultSourceFileExtension = "lang"
const val defaultCompiledFileExtension = "clang"

object CommonClassNames {
    val LANG_STRING = "lang.String"
}

object CommonClassTypes {
    val LANG_STRING = ClassType(CommonClassNames.LANG_STRING)
}