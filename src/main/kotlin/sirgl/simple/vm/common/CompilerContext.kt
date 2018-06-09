package sirgl.simple.vm.common

import sirgl.simple.vm.Configuration
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.driver.AstBuilder
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.driver.GlobalScope
import sirgl.simple.vm.roots.SymbolSourceProvider
import java.nio.file.Path

class CompilerContext(
    val astBuilder: AstBuilder,
    val astCache: AstCache,
    val globalScope: GlobalScope,
    val errorSink: ErrorSink,
    val sourceProviders: List<SymbolSourceProvider>
)