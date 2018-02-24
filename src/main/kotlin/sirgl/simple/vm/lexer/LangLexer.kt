package sirgl.simple.vm.lexer

import sirgl.simple.vm.lexer.LexemeKind.*


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
        "false",
        "import",
        "void"
)

val keywordToKind = mutableMapOf(
        "fun" to Fun,
        "while" to While,
        "class" to Class,
        "var" to Var,
        "bool" to Bool,
        "native" to Native,
        "continue" to Continue,
        "break" to Break,
        "return" to Return,
        "try" to Try,
        "catch" to Catch,
        "i32" to I32,
        "i8" to I8,
        "true" to True,
        "false" to False,
        "import" to Return,
        "package" to Package,
        "void" to Void
)

val operatorToKind = mutableMapOf(
        "+" to OpPlus,
        "-" to OpMinus,
        "*" to OpAsterisk,
        "/" to OpDiv,
        "%" to OpPercent,
        "!" to OpExcl,
        "<=" to OpLtEq,
        "<" to OpLt,
        ">=" to OpGtEq,
        ">" to OpGt,
        "=" to OpEq,
        "==" to OpEqEq,
        "!=" to OpNotEq,
        "&&" to OpAndAnd,
        "||" to OpOrOr,
        "as" to OpAs
)

val punctuationToKind = mutableMapOf(
        ";" to Semicolon,
        ":" to Colon,
        "." to Dot,
        "," to Comma,
        "[" to LBracket,
        "]" to RBracket,
        "(" to LParen,
        ")" to RParen,
        "{" to LBrace,
        "}" to RBrace
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
        private val skipComments: Boolean = true,
        private var position: Int = 0,
        private var line: Int = 0
) {
    private val lexemes = mutableListOf<Lexeme>()
    private var isRecovery = false
    private var recoveryPosition = -1

    fun tokenize(): List<Lexeme> {
        if (text.isEmpty()) {
            addEndLexeme()
            return lexemes
        }
        while (!isEnd()) {
            scanLexeme()
        }
        addErrorWhenRecovery()
        addEndLexeme()
        return lexemes
    }

    private fun addEndLexeme() {
        addLexeme(position, EOL)
    }

    private fun scanLexeme() {
        if (tryWhitespace()) return
        if (tryEolComment()) return
        // Next three loops can be eliminated by DFA
        for ((keyword, kind) in keywordToKind) {
            if (tryWord(keyword, kind)) return
        }
        for ((punct, kind) in punctuationToKind) {
            if (tryWord(punct, kind)) return
        }
        for ((op, kind) in operatorToKind) {
            if (tryWord(op, kind)) return
        }
        if (tryIntLiteral()) return
        if (tryStringLiteral()) return
        if (tryCharLiteral()) return
        if (tryIdentifier()) return
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
            addLexeme(current, IntLiteral)
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
        addLexeme(current + 1, StringLiteral)
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
        addLexeme(current, Identifier)
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
        addLexeme(current, CharLiteral)
        return true
    }

    private fun tryEolComment(): Boolean {
        var current = position
        if (text[current] != '/') return false
        current++
        if (isEnd(current) || text[current] != '/') return false
        current++
        while (true) {
            if (isEnd(current) || text[current] == '\n') {
                addLexemeConditional(current, skipWhitespace, EolComment)
                return true
            }
            current++
        }
    }

//    private fun tryCStyleComment() : Boolean {
//}

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
            addLexemeConditional(whitespaceEnd, skipWhitespace, WhiteSpace)
            return true
        }
        return false
    }

    private fun addLexemeConditional(endPosition: Int, condition: Boolean, kind: LexemeKind) {
        if (condition) {
            addErrorWhenRecovery()
            position = endPosition
        } else {
            addLexeme(endPosition, kind)
        }
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
            lexemes.add(Lexeme(recoveryPosition, position, Error, text.substring(recoveryPosition, position), line))
            isRecovery = false
        }
    }
}