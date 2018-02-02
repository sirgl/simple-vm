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

// TODO
// Very bad code :(
// Here should be added lexer state and functions for convenience
class LangLexerHandwrittenImpl : LangLexer {
    override fun tokenize(text: String): List<Lexeme> {
        var position = 0
        val length = text.length
        val lexemes = mutableListOf<Lexeme>()
        var recovery = false
        var recoveryPosition = -1
        loop@
        while (length > position) {
            if (text[position].isWhitespace()) {
                position++
                continue
            }

            //keyword
            for ((keyword, kind) in keywordToKind) {
                if (testString(position, text, keyword)) {
                    val nextPosition = keyword.length + position
                    val lexeme = Lexeme(position, nextPosition - 1, kind, text.substring(position, nextPosition))
                    lexemes.add(lexeme)
                    position += keyword.length
                    continue@loop
                }
            }

            //operator
            for (operator in operators) {
                if (testString(position, text, operator)) {
                    val nextPosition = operator.length + position
                    val lexeme = Lexeme(position, nextPosition - 1, LexemeKind.Operator, text.substring(position, nextPosition))
                    lexemes.add(lexeme)
                    position += operator.length
                    continue@loop
                }
            }

            // int literal
            var digitPosition = position
            var char = text[digitPosition]
            if (char.isDigit() && recovery) {
                recovery = false
                lexemes.add(Lexeme(recoveryPosition, position - 1, LexemeKind.Error, text.substring(recoveryPosition, position)))
            }
            while (char.isDigit()) {
                digitPosition++
                if (digitPosition == text.length) break
                char = text[digitPosition]
            }
            if (digitPosition != position) {
                val lexeme = Lexeme(position, digitPosition - 1, LexemeKind.IntLiteral, text.substring(position, digitPosition))
                position = digitPosition
                lexemes.add(lexeme)
                continue@loop
            }

            // string literal
            if (char == '\"') {
                if(recovery) {
                    recovery = false
                    lexemes.add(Lexeme(recoveryPosition, position - 1, LexemeKind.Error, text.substring(recoveryPosition, position)))
                }
                var strPosition = position + 1
                if (position + 1 >= text.length) {
                    lexemes.add(Lexeme(position, position, LexemeKind.Error, "\""))
                    break
                }
                while (strPosition < text.length) {
                    char = text[strPosition]
                    if (char == '\"') {
                        break
                    }
                    if (char == '\\') {
                        strPosition++
                        if (strPosition >= text.length) {
                            recovery = true
                            recoveryPosition = strPosition
                            position = strPosition + 1
                            continue@loop
                        }
                        val escaped = text[strPosition]
                        if (escaped == '\"' || escaped == 't') {
                            strPosition++
                            continue
                        } else {
                            recovery = true
                            recoveryPosition = strPosition
                            position = strPosition + 1
                            continue@loop
                        }
                    } else {
                        strPosition++
                    }
                }
                if(strPosition >= text.length) {
                    recovery = true
                    recoveryPosition = position
                    break@loop
                }
                val literalText = text.substring(position, strPosition + 1)
                val lexeme = Lexeme(position, strPosition, LexemeKind.StringLiteral, literalText)
                lexemes.add(lexeme)
                position = strPosition + 1
                if (position >= text.length) break
                continue@loop
            }

            if (char.isLetter()) {
                if(recovery) {
                    recovery = false
                    lexemes.add(Lexeme(recoveryPosition, position - 1, LexemeKind.Error, text.substring(recoveryPosition, position)))
                }
                var idPosition = position + 1
                if (position + 1 >= text.length) {
                    lexemes.add(Lexeme(position, position, LexemeKind.Error, char.toString()))
                    break
                }
                while (idPosition < text.length) {
                    char = text[idPosition]
                    if (!char.isLetterOrDigit()) {
                        break
                    }
                    idPosition++
                }
                val lexeme = Lexeme(position, idPosition, LexemeKind.Identifier, text.substring(position, idPosition))
                lexemes.add(lexeme)
                position = idPosition + 1
                if (position >= text.length) break
                continue@loop
            }
            if(!recovery) {
                recovery = true
                recoveryPosition = position
                position++
            }
        }
        if(recovery) {
            val lexeme = Lexeme(recoveryPosition, text.length - 1, LexemeKind.Error, text.substring(position, text.length))
            lexemes.add(lexeme)
        }
        return lexemes
    }

    private fun testString(startPosition: Int, text: String, str: String): Boolean {
        if (startPosition + str.length > text.length) return false
        val result = text.substring(startPosition, startPosition + str.length)
        return result == str
    }
}