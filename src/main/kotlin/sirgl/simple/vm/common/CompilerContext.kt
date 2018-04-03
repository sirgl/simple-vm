package sirgl.simple.vm.common

import sirgl.simple.vm.Configuration
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.driver.AstBuilder
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.driver.ResolveCache
import sirgl.simple.vm.driver.SourceFile

class CompilerContext(
        val astBuilder: AstBuilder,
        val resolveCache: ResolveCache,
        val configuration: Configuration,
        val errorSink: ErrorSink,
        var sourceFiles: List<SourceFile> = listOf(),
        val asts: MutableList<LangFile> = mutableListOf()
) {
}