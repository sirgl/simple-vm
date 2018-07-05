package sirgl.simple.vm

import org.junit.Assert
import sirgl.simple.vm.common.defaultSourceFileExtension
import sirgl.simple.vm.ext.extension
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

abstract class MultiFileProjectTest(val basePath: String) {
    protected fun test(folderName: String) {
        val folderPath = "$basePath/$folderName"
        val path = Paths.get(folderPath)
        val sourcePaths = Files.list(path)
                .filter { child -> child.extension() == defaultSourceFileExtension }
                .toList()
        val expectedOutput = File("$folderPath/output.txt").readText()
        Assert.assertEquals(expectedOutput, apply(sourcePaths))
    }

    abstract fun apply(sourcePaths: List<Path>): String
}