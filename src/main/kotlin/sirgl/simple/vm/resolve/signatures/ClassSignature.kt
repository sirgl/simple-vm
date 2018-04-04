package sirgl.simple.vm.resolve.signatures

import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.type.ClassType


class ClassSignature(
        override val sourceFile: SourceFile,
        val simpleName: String,
        val packageStr: String?,
        val fieldSignatures: List<VariableSignature>,
        val methodSignatures: List<MethodSignature>,
        val type: ClassType
) : Signature {
    override val name: String
        get() = simpleName

    val qualifiedName: String
        get() {
            if (packageStr == null) return simpleName
            return "$packageStr.$simpleName"
        }

    fun toType(): ClassType {
        return ClassType(qualifiedName)
    }
}