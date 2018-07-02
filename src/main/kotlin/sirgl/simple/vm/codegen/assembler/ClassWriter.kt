package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.resolve.symbols.ClassSymbol


val LANG_BYTECODE_VERSION: Short = 0
val MAGIC_VALUE: Int = 0x0B1B2B3B

class ClassWriter(val classSymbol: ClassSymbol) {
    private val methods = mutableListOf<MethodWithBytecode>()
    private val fields = mutableListOf<CPDescriptor>()
    val cp = ConstantPool()

    var parentPackageDescriptor: CPDescriptor = 0
    var parentClassDescriptor: CPDescriptor = 0
    var classDescriptor: CPDescriptor = 0
    var packageDescriptor: CPDescriptor = 0

    init {
        val parentClassSymbol = classSymbol.parentClassSymbol
        if (parentClassSymbol == null) {
            // TODO handle lang.Object
        } else {
            val parentPackage = parentClassSymbol.packageSymbol
            parentPackageDescriptor = cp.addPackage(parentPackage.name)
            parentClassDescriptor = cp.addClass(parentPackageDescriptor, parentPackage.name)
        }
        packageDescriptor = cp.addPackage(classSymbol.packageSymbol.name)
        classDescriptor = cp.addClass(packageDescriptor, classSymbol.simpleName)
    }

    fun build() = ClassRepr(
            LANG_BYTECODE_VERSION,
            parentClassDescriptor,
            classDescriptor,
            cp,
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
