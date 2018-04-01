package sirgl.simple.vm.common

import sirgl.simple.vm.Configuration
import sirgl.simple.vm.driver.AstBuilder
import sirgl.simple.vm.driver.AstCache
import sirgl.simple.vm.driver.SourceFile

class CompilerContext(
        val astBuilder: AstBuilder,
        val astCache: AstCache,
        val configuration: Configuration,
        var sourceFiles: List<SourceFile> = mutableListOf()
) {
}