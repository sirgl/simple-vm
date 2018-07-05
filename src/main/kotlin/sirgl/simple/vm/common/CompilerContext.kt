package sirgl.simple.vm.common

import sirgl.simple.vm.codegen.CodegenOutputStrategy
import sirgl.simple.vm.driver.AstBuilder
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.driver.GlobalScope
import sirgl.simple.vm.roots.SymbolSourceProvider

class CompilerContext(
        val astBuilder: AstBuilder,
        val astCache: AstCache,
        val globalScope: GlobalScope,
        val errorSink: ErrorSink,
        val sourceProviders: MutableList<SymbolSourceProvider>,
        val codegenOutputStrategy: CodegenOutputStrategy
)