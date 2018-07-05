package sirgl.simple.vm.codegen.assembler

import java.io.DataOutputStream
import java.nio.charset.StandardCharsets

enum class CpLabel(val idx: Byte) {
    Str(0),
    Int(1),
    Method(2),
    Var(3),
    ClassRef(4);
}

sealed class ConstantPoolEntry {
    abstract fun getPresentableContent(pool: ConstantPool): String
    abstract val label: CpLabel
    abstract fun writeContent(stream: DataOutputStream)
}

class IntCPEntry(val num: Int) : ConstantPoolEntry() {
    override val label: CpLabel
        get() = CpLabel.Int

    override fun getPresentableContent(pool: ConstantPool) = num.toString()

    override fun writeContent(stream: DataOutputStream) {
        stream.writeInt(num)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntCPEntry) return false

        if (num != other.num) return false

        return true
    }

    override fun hashCode(): Int {
        return num
    }


}

class StringCPEntry(val str: String) : ConstantPoolEntry() {
    override fun getPresentableContent(pool: ConstantPool): String = str
    override val label: CpLabel
        get() = CpLabel.Str

    override fun writeContent(stream: DataOutputStream) {
        stream.writeString(str)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StringCPEntry) return false

        if (str != other.str) return false

        return true
    }

    override fun hashCode(): Int {
        return str.hashCode()
    }


}

class MethodCPEntry(
        val classDescriptor: CPDescriptor,
        val nameDescriptor: CPDescriptor,
        val returnTypeSignatureDescr: CPDescriptor,
        val parameterVarDescriptors: List<CPDescriptor>
) : ConstantPoolEntry() {
    override fun getPresentableContent(pool: ConstantPool): String {
        val className = resolveClassName(pool)
        return "$className.${toStringWithoutPackage(pool)}"
    }

    fun toStringWithoutPackage(pool: ConstantPool): String {
        val methodName = resolveName(pool)
        val returnTypeSignature = resolveReturnTypeSignature(pool)
        val parameterList = resolveParameterList(pool)
        return "$methodName($parameterList) -> $returnTypeSignature"
    }

    private fun resolveParameterList(pool: ConstantPool): String {
        return parameterVarDescriptors.map {
            (pool.resolve(it) as? VariableCPEntry)?.getPresentableContent(pool)
        }.joinToString(", ")
    }

    private fun resolveReturnTypeSignature(pool: ConstantPool) =
            (pool.tryResolveAsString(returnTypeSignatureDescr)
                    ?: "<Unknown return type signature>")

    private fun resolveName(pool: ConstantPool) =
            pool.tryResolveAsString(nameDescriptor) ?: "<Unknown method name>"

    private fun resolveClassName(pool: ConstantPool) =
            ((pool.resolve(classDescriptor) as? ClassCPEntry)?.getPresentableContent(pool)
                    ?: "<Unknown class name>")

    override val label: CpLabel
        get() = CpLabel.Method

    override fun writeContent(stream: DataOutputStream) {
        stream.writeDescr(classDescriptor)
        stream.writeDescr(nameDescriptor)
        stream.writeDescr(returnTypeSignatureDescr)
        stream.writeInt(parameterVarDescriptors.size)
        for (parameterVarDescriptor in parameterVarDescriptors) {
            stream.writeDescr(parameterVarDescriptor)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MethodCPEntry) return false

        if (classDescriptor != other.classDescriptor) return false
        if (nameDescriptor != other.nameDescriptor) return false
        if (returnTypeSignatureDescr != other.returnTypeSignatureDescr) return false
        if (parameterVarDescriptors != other.parameterVarDescriptors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = classDescriptor.toInt()
        result = 31 * result + nameDescriptor
        result = 31 * result + returnTypeSignatureDescr
        result = 31 * result + parameterVarDescriptors.hashCode()
        return result
    }
}


class ClassCPEntry(
        val simpleNameDescriptor: CPDescriptor,
        val packageDescriptor: CPDescriptor
) : ConstantPoolEntry() {
    override fun getPresentableContent(pool: ConstantPool): String {
        val simpleName = pool.tryResolveAsString(simpleNameDescriptor) ?: "<Unknown class name>"
        val packageName = pool.tryResolveAsString(packageDescriptor) ?: "<Unknown package>"
        return "$packageName.$simpleName"
    }

    override val label: CpLabel
        get() = CpLabel.ClassRef

    override fun writeContent(stream: DataOutputStream) {
        stream.writeDescr(simpleNameDescriptor)
        stream.writeDescr(packageDescriptor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassCPEntry) return false

        if (simpleNameDescriptor != other.simpleNameDescriptor) return false
        if (packageDescriptor != other.packageDescriptor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = simpleNameDescriptor.toInt()
        result = 31 * result + packageDescriptor
        return result
    }


}

class VariableCPEntry(
        val typeSignatureDescriptor: CPDescriptor,
        val nameDescriptor: CPDescriptor
) : ConstantPoolEntry() {
    override fun getPresentableContent(pool: ConstantPool): String {
        val varName = pool.tryResolveAsString(nameDescriptor) ?: "<Unknown var name>"
        val typeSignature = pool.tryResolveAsString(typeSignatureDescriptor) ?: "<Unknown type signature>"
        return "$varName : $typeSignature"
    }

    override val label: CpLabel
        get() = CpLabel.Var

    override fun writeContent(stream: DataOutputStream) {
        stream.writeDescr(typeSignatureDescriptor)
        stream.writeDescr(nameDescriptor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VariableCPEntry) return false

        if (typeSignatureDescriptor != other.typeSignatureDescriptor) return false
        if (nameDescriptor != other.nameDescriptor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = typeSignatureDescriptor.toInt()
        result = 31 * result + nameDescriptor
        return result
    }


}

fun ConstantPool.tryResolveAsString(descr: CPDescriptor): String? {
    return (resolve(descr) as? StringCPEntry)?.str
}

private fun DataOutputStream.writeDescr(desc: CPDescriptor) {
    writeShort(desc.toInt())
}

private fun DataOutputStream.writeString(str: String) {
    val byteArray = str.toByteArray(StandardCharsets.US_ASCII)
    writeInt(byteArray.size)
    write(byteArray)
}