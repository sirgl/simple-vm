package sirgl.simple.vm.lexer

class Lexeme(
        val startOffset: Int,
        val endOffset: Int,
        val kind: LexemeKind,
        val text: String,
        val line: Int = -1
) {
    override fun toString(): String {
        return "$kind@[$startOffset: $endOffset) #$line, text = \"$text\""
    }
}