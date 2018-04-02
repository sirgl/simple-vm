package sirgl.simple.vm.signatures

import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.type.LangType

class VariableSignature(
        override val sourceFile: SourceFile,
        val type: LangType,
        override val name: String
) : Signature