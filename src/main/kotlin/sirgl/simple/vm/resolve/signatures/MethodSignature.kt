package sirgl.simple.vm.resolve.signatures

import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.type.LangType

class MethodSignature(
        override val sourceFile: SourceFile,
        override val name: String,
        val returnType: LangType,
        val parameters: List<VariableSignature>
) : Signature {
    override fun toString(): String {
        return "method '$name' returnType=${returnType.name}, parameters=${parameters.joinToString(", ") { it.name + ": " + it.type.name}})"
    }
}