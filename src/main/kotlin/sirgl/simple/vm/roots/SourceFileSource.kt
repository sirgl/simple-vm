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
    Compiled,
    Internal
}

// Misnaming, probably should be renamed
interface FileSymbolSource : SymbolSource {
    fun getInputStream(): InputStream
    override val path: Path?
}

interface SourceFileSource : FileSymbolSource

interface CompiledFileSource : FileSymbolSource

class FsSourceFileSource(override val path: Path) : SourceFileSource {
    override val type = SymbolSourceType.Source

    override fun getInputStream(): InputStream = Files.newInputStream(path)
}

class FsCompiledFileSource(override val path: Path) : CompiledFileSource {
    override val type = SymbolSourceType.Compiled

    override fun getInputStream(): InputStream = Files.newInputStream(path)
}

class InMemorySourceFileSource(val text: String) : SourceFileSource {
    override val path: Path? = null

    override fun getInputStream() = text.byteInputStream()

    override val type: SymbolSourceType = SymbolSourceType.Source
}

object InternalSymbolSource : SymbolSource {
    override val path: Path? = null
    override val type: SymbolSourceType = SymbolSourceType.Internal
}