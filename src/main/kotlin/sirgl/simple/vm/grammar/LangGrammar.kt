package sirgl.simple.vm.grammar

import norswap.autumn.Grammar
import norswap.autumn.UncaughtException
import norswap.autumn.model.keyword
import norswap.autumn.parsers.*
import sirgl.simple.vm.ast.ext.parseLiteral
import sirgl.simple.vm.ast.impl.LangBinaryOperatorImpl
import sirgl.simple.vm.ast.impl.expr.*


@Suppress("FunctionName", "MemberVisibilityCanBePrivate")
// Toy grammar and parser, not supposed to be fast, but to be written fast
class LangGrammar : Grammar() {

    // Lexer:
    fun lineComment() = seq { "//".str && until0({ char_any() }, { "\n".str }) }

    fun multiComment() = seq { "/*".str && until0({ char_any() }, { "*/".str }) }

    override fun whitespace() = repeat0 { choice { space_char() || lineComment() || multiComment() } }

    val `fun` = "fun".keyword
    val `while` = "while".keyword
    val `class` = "class".keyword
    val `var` = "var".keyword
    val `bool` = "bool".keyword
    val `native` = "native".keyword
    val `continue` = "continue".keyword
    val `break` = "break".keyword
    val `return` = "return".keyword
    val `try` = "try".keyword
    val i32 = "i32".keyword
    val i8 = "i8".keyword

    //// Expressions


    fun int_literal() = build_str(
            syntax = { repeat1 { digit() } },
            value = { LangIntLiteralExprImpl(it.toInt(), frame_start(), pos) }
    )

    fun escape_char() = build_str(
            syntax = { "\\".str && "btnfr\"'\\".set },
            value = { it[1] }
    )

    fun string_char() = choice { escape_char() || seq { not { "\"".str } && char_any() } }

    fun string_literal() = build_str(
            syntax = { "\"".str && this.repeat0 { this.string_char() } && "\"".str },
            value = { LangStringLiteralExprImpl(parseLiteral(it), frame_start(), pos) }
    )

    fun bool_literal() = build_str(
            syntax = { choice { "true".str || "false".str } },
            value = { LangBoolLiteralExprImpl(it.toBoolean(), frame_start(), pos) }
    )

    fun this_expr() = build(
            syntax = { "this".str },
            effect = { LangThisExprImpl(frame_start(), pos) }
    )

    fun null_expr() = build(
            syntax = { "null".str },
            effect = { LangNullExprImpl(frame_start(), pos) }
    )

    fun bin_operator() = build_str(
            syntax = {
                choice {
                    "+".str
                            || "-".str
                            || "*".str
                            || "/".str
                            || "%".str
                            || "<=".str
                            || ">=".str
                            || "==".str
                            || "<".str
                            || ">".str
                }
            },
            value = { LangBinaryOperatorImpl(it, frame_start(), pos) }
    )

    fun binary_expr() = build(
            syntax = assoc_left {
                left = { expr() }
                right = {
                    seq { bin_operator() && expr() }
                }
            },
            effect = {
                LangBinaryExprImpl(
                        it(0),
                        it(2),
                        it(1),
                        frame_start(),
                        pos
                )
            }
    )

    fun expr(): Boolean = choice {
        binary_expr() || primary_expr()
    }

    fun primary_expr() = choice {
        int_literal()
                || string_literal()
                || null_expr()
    }


    override fun root() = expr()
}

fun diagnose(str: String, grammar: LangGrammar) {
    if (grammar.parse(str)) {
        println(grammar.stack.pop())
    } else {
        val failure = grammar.failure
        if (failure is UncaughtException) {
            failure.e.printStackTrace()
        }
        println("failure: " + failure?.invoke())
    }
}

fun main(args: Array<String>) {
    val grammar = LangGrammar()
    diagnose("null+null", grammar)
}