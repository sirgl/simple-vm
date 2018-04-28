package sirgl.simple.vm.ast.support

import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.type.LangType

interface LangFunction : Scoped {
    val parameters: List<LangParameter>
    val block: LangBlock?
    val isNative: Boolean
    val returnType: LangType
}