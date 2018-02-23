package sirgl.simple.vm.ast.ext

import sirgl.simple.vm.ast.AstNode

val AstNode.rangeText: String
    get() = "@($startOffset: $endOffset)"


fun AstNode.prettyText(): String {
    val sb = StringBuilder()
    prettyText(0, sb)
    return sb.toString()
}

private fun AstNode.prettyText(level: Int, sb: StringBuilder) {
    addLevelIndent(level, sb)
    sb.append(toString())
    for (child in children) {
        sb.append('\n')
        child.prettyText(level + 1, sb)
    }
}

private fun addLevelIndent(level: Int, sb: StringBuilder) {
    for (i in (0 until level)) {
        sb.append("  ")
    }
}