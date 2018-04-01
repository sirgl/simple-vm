package sirgl.simple.vm.driver

import java.io.InputStream

class SourceFile(
        val path: String?,
        private val inputStreamProvider: () -> InputStream
) {
    val inputStream: InputStream
        get() = inputStreamProvider()

}

val SourceFile?.fileName: String
    get() = this?.path ?: "<unknown>"