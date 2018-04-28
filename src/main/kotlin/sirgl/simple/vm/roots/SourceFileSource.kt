package sirgl.simple.vm.roots

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

// Need for error reporting
interface SymbolSource {
    val path: Path?
    val type: SymbolSourceType
}

enum class SymbolSourceType {
    Source,
    Compiled
}

interface FileSymbolSource : SymbolSource {
    fun getInputStream(): InputStream
}

interface SourceFileSource : FileSymbolSource

interface CompiledFileSource : FileSymbolSource

class RealSourceFileSource(override val path: Path) : SourceFileSource {
    override val type = SymbolSourceType.Source

    override fun getInputStream(): InputStream = Files.newInputStream(path)
}

class RealCompiledFileSource(override val path: Path) : CompiledFileSource {
    override val type = SymbolSourceType.Compiled

    override fun getInputStream(): InputStream = Files.newInputStream(path)
}