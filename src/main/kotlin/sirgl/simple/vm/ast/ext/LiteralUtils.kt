package sirgl.simple.vm.ast.ext

// Returns representation of string
fun parseLiteral(str: String): String {
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