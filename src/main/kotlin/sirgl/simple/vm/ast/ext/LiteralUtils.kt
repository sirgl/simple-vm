package sirgl.simple.vm.ast.ext

// Returns representation of string
// Expects correct string (that came from parser)
fun parseStringLiteral(str: String): String {
    val sb = StringBuilder()
    var escaped = false
    for (i in (1 until str.length - 1)) {
        val c = str[i]
        when {
            escaped -> {
                sb.append(c)
                escaped = false
            }
            c == '\\' -> escaped = true
            else -> sb.append(c)
        }
    }
    return sb.toString()
}

fun parseCharLiteral(str: String): Byte {
    val repr = str.substring(1, str.lastIndex)
    return when (repr.first()) {
        '\\' -> {
            val c = repr[1]
            when (c) {
                'n' -> '\n'
                't' -> '\t'
                '\"' -> '\"'
                '\'' -> '\''
                else -> throw IllegalStateException()
            }.toByte()
        }
        else -> repr.first().toByte()
    }
}