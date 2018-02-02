package sirgl.simple.vm

import org.junit.Assert
import org.junit.jupiter.api.DynamicTest
import sirgl.simple.vm.common.defaultSourceFileExtension
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

abstract class FileBasedTestCaseBase<T> {

    abstract fun applyAction(text: String): T

    private class Case<out T>(val path: Path, val valueAfter: T)

    abstract val directory: Path

    fun getAllTests(): Stream<DynamicTest> {
        return findBeforeTestFiles(directory)
                .filter { filterBeforeName(it.fileName.toString()) }
                .map { Case(it, applyAction(Files.newBufferedReader(it).readText())) }
                .map { case ->
                    DynamicTest.dynamicTest(case.path.fileName.toString().substringBeforeLast('.')) {
                        val expectedText = Files.newBufferedReader(findResultPath(case.path)).readText()
                        check(case.valueAfter, expectedText)
                    }
                }
    }

    fun filterBeforeName(name: String) : Boolean {
        return !name.contains("after") && name.endsWith(".$defaultSourceFileExtension")
    }

    fun transformName(fileNameBefore: String): String {
        return fileNameBefore + ".after"
    }

    fun check(actual: T, expectedText: String) {
        Assert.assertEquals("Different content in file.", actual, expectedText)
    }

    fun findResultPath(path: Path): Path {
        val fileName = path.fileName.toString()
        val nameOnly = fileName.substringBeforeLast('.')
        val ext = fileName.substringAfterLast('.')
        // TODO black path magic here
        return Paths.get("/" + path.subpath(0, path.nameCount - 1).resolve(transformName(nameOnly) + "." + ext).toString())
    }

    fun findBeforeTestFiles(directory: Path): Stream<Path> {
        if (!Files.isDirectory(directory)) {
            throw IllegalArgumentException("$directory expected to be a directory")
        }
        return Files.list(directory)
    }
}

abstract class FileTestCase<T> : FileBasedTestCaseBase<T>() {
    abstract val relativePath: Path

    override val directory: Path by lazy {
        Paths.get(FileTestCase::class.java.classLoader.getResource(relativePath.toString()).path)
    }

}