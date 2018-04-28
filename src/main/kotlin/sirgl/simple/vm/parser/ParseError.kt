package sirgl.simple.vm.parser

import sirgl.simple.vm.driver.CompilationError
import sirgl.simple.vm.driver.SourceFile

class ParseError(
    override val text: String,
    override val sourceFile: SourceFile?
) : CompilationError