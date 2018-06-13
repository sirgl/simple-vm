package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.common.CommonClassTypes
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor
import sirgl.simple.vm.driver.GlobalScope
import sirgl.simple.vm.resolve.symbols.ClassSymbol

class CommonTypesSetupPhase : CompilerPhase<CommonTypesSetupPhase>() {
    override fun run(context: CompilerContext) {
        val globalScope: GlobalScope = context.globalScope
        val types = CommonClassTypes.types
        for (type in types) {
            val typeName = type.name
            val lastDotIndex = typeName.lastIndexOf('.')
            val packageName = typeName.substring(0, lastDotIndex)
            val className = typeName.substring(lastDotIndex + 1)
            val packageSymbol = globalScope.findOrCreatePackageSymbol(packageName)
            val symbol = packageSymbol.resolve(className, null)
            val classSymbol = symbol as? ClassSymbol
                    ?: throw IllegalStateException("Failed to init common class: ${type.name}, not found in package")
            type.classSymbol = classSymbol
        }
    }

    override val descriptor: PhaseDescriptor<CommonTypesSetupPhase> = Companion
    companion object : PhaseDescriptor<CommonTypesSetupPhase>("Common types setup")
}