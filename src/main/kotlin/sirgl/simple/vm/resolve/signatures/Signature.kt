package sirgl.simple.vm.resolve.signatures

import sirgl.simple.vm.driver.SourceFile


interface Signature {
    val sourceFile: SourceFile
    val name: String
}