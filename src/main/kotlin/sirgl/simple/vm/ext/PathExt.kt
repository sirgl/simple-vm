package sirgl.simple.vm.ext

import java.nio.file.Path

fun Path.extension() = fileName.toString().substringAfterLast('.')
fun Path.name() = fileName.toString().substringBeforeLast('.')