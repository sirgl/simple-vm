package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.resolve.symbols.ClassSymbol


val LANG_BYTECODE_VERSION: Short = 0
val MAGIC_VALUE: Int = 0x0B1B2B3B

class ClassWriter(val classSymbol: ClassSymbol) {
    private val methods = mutableListOf<MethodWithBytecode>()
    private val fields = mutableListOf<CPDescriptor>()
    val constantPool = ConstantPool()

    var parentClassDescriptor: CPDescriptor = 0
    var classDescriptor: CPDescriptor = 0

    init {
        val parentClassSymbol = classSymbol.parentClassSymbol
        if (parentClassSymbol == null) {
            // TODO handle lang.Object
        } else {
            parentClassDescriptor = constantPool.addClass(parentClassSymbol.packageSymbol.name, parentClassSymbol.simpleName)
        }
        classDescriptor = constantPool.addClass(classSymbol.packageSymbol.name, classSymbol.simpleName)
    }

    fun build() = ClassRepr(
            LANG_BYTECODE_VERSION,
            parentClassDescriptor,
            classDescriptor,
            constantPool,
            methods,
            fields
    )

    fun addField(fieldDescr: CPDescriptor) {
        fields.add(fieldDescr)
    }

    fun addMethodInfo(method: MethodWithBytecode) {
        methods.add(method)
    }
}
