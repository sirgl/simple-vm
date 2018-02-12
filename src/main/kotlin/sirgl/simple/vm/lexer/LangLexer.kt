package sirgl.simple.vm.lexer


val keywords = mutableListOf(
        "fun",
        "while",
        "class",
        "var",
        "bool",
        "native",
        "continue",
        "break",
        "return",
        "try",
        "catch",
        "i32",
        "i8",
        "true",
        "false"
)

val keywordToKind = mutableMapOf(
        "fun" to LexemeKind.Fun,
        "while" to LexemeKind.While,
        "class" to LexemeKind.Class,
        "var" to LexemeKind.Var,
        "bool" to LexemeKind.Bool,
        "native" to LexemeKind.Native,
        "continue" to LexemeKind.Continue,
        "break" to LexemeKind.Break,
        "return" to LexemeKind.Return,
        "try" to LexemeKind.Try,
        "catch" to LexemeKind.Catch,
        "i32" to LexemeKind.I32,
        "i8" to LexemeKind.I8,
        "true" to LexemeKind.True,
        "false" to LexemeKind.False
)

val operators = mutableListOf(
        "+",
        "-",
        "*",
        "/",
        "%",
        "<=",
        "<",
        ">=",
        ">",
        "=="
)


//fun main(args: Array<String>) {
//    println(keywords.associateBy ({it}) { it[0].toUpperCase() + it.substring(1) }.toList()
//            .joinToString(separator =",\n") { "\"${it.first}\" to LexemeKind.${it.second}" })
//}

interface LangLexer {
    // Here probably should be InputStream, also it should be DFA
    fun tokenize(text: String): List<Lexeme>
}

class HandwrittenLangLexer : LangLexer {
    override fun tokenize(text: String) = LexerState(text).tokenize()
}

/**
 * One-off object. After tokenize() must not be used
 */
@Suppress("LoopToCallChain")
private class LexerState(
        private val text: String,
        private val skipWhitespace: Boolean = true,
        private var position: Int = 0,
        private var line: Int = 0
) {
    private val lexemes = mutableListOf<Lexeme>()
    private var isRecovery = false
    private var recoveryPosition = -1

    //TODO here actually can be useful lookahead position and list of functions, linked with it for convinience, e.g. expect

    fun tokenize(): List<Lexeme> {
        if (text.isEmpty()) return emptyList()
        while (!isEnd()) {
            scanLexeme()
        }
        addErrorWhenRecovery()
        return lexemes
    }

    private fun scanLexeme() {
        if (tryWhitespace()) return
        for ((keyword, kind) in keywordToKind) {
            if (tryWord(keyword, kind)) return
        }
        for (operator in operators) {
            if (tryWord(operator, LexemeKind.Operator)) return
        }
        if (tryIntLiteral()) return
        if (tryStringLiteral()) return
        if (tryCharLiteral()) return
        if(tryIdentifier()) return
        if (!isRecovery) {
            isRecovery = true
            recoveryPosition = position
        }
        position++
    }

    private fun tryIntLiteral(): Boolean {
        var current = position
        while (!isEnd(current) && text[current].isDigit()) {
            current++
        }
        if (current != position) {
            addLexeme(current, LexemeKind.IntLiteral)
            return true
        }
        return false
    }

    private fun tryStringLiteral(): Boolean {
        var current = position
        if (text[current] != '\"') {
            return false
        }
        current++
        while (!isEnd(current)) {
            val ch = text[current]
            if (ch == '\\') {
                current++
                if (isEnd(current) || !"\\\n\r\t\"".contains(text[current])) {
                    activateRecovery(current)
                    return true
                }
            }
            if (ch == '\"') break
            current++
        }
        if (isEnd(current) || text[current] != '\"') {
            activateRecovery(current)
            return true
        }
        addLexeme(current + 1, LexemeKind.StringLiteral)
        return true
    }

    private fun activateRecovery(lastExcl: Int) {
        recoveryPosition = position
        isRecovery = true
        position = lastExcl
    }

    private fun tryIdentifier(): Boolean {
        var current = position
        if (!text[current].isLetter()) return false
        current++
        while (!isEnd(current) && text[current].isLetterOrDigit()) {
            current++
        }
        addLexeme(current, LexemeKind.Identifier)
        return true
    }

    private fun tryCharLiteral(): Boolean {
        var current = position
        if (text[current] != '\'') return false
        current++
        if (isEnd(current)) return false
        if (text[current] == '\\') {
            current++
            if (isEnd(current) || "\r\n\\\t\'".contains(text[current])) {
                return false
            }
        }
        current++
        if (isEnd(current) || text[current] != '\'') return false
        addLexeme(current, LexemeKind.CharLiteral)
        return true
    }

    private fun tryWhitespace(): Boolean {
        var current = text[position]
        var whitespaceEnd = position
        while (!isEnd() && current.isWhitespace()) {
            if (current == '\n') {
                line++
            }
            whitespaceEnd++
            current = text[whitespaceEnd]
        }
        if (whitespaceEnd != position) {
            if (!skipWhitespace) {
                addLexeme(whitespaceEnd, LexemeKind.WhiteSpace)
            } else {
                position = whitespaceEnd
            }
            return true
        }
        return false
    }

    private fun tryWord(word: String, kind: LexemeKind): Boolean {
        val wordLength = word.length
        val endPosition = position + wordLength
        if (endPosition > text.length) return false
        val probablyWord = text.substring(position, endPosition)
        if (probablyWord != word) return false
        addLexeme(endPosition, kind)
        return true
    }

    private fun isEnd(pos: Int) = pos >= text.length

    private fun isEnd() = isEnd(position)

    /**
     * Add lexeme and skip position to endPosition
     * @param endPosition exclusive
     */
    private fun addLexeme(endPosition: Int, kind: LexemeKind) {
        addErrorWhenRecovery()
        val lexemeText = text.substring(position, endPosition)
        lexemes.add(Lexeme(position, endPosition, kind, lexemeText, line))
        position = endPosition
    }

    private fun addErrorWhenRecovery() {
        if (isRecovery) {
            lexemes.add(Lexeme(recoveryPosition, position, LexemeKind.Error, text.substring(recoveryPosition, position), line))
            isRecovery = false
        }
    }
}