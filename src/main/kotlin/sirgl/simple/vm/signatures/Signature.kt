package sirgl.simple.vm.signatures

import sirgl.simple.vm.driver.SourceFile


interface Signature {
    val sourceFile: SourceFile
    val name: String
}