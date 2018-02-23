package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

class LangParameterImpl(
        name: String,
        type: LangType,
        startLexeme: Lexeme,
        endLexeme: Lexeme
) : LangVarDeclImpl(name, type, startLexeme, endLexeme), LangParameter {
    override lateinit var parent: LangMethod

    override fun accept(visitor: LangVisitor) {
        visitor.visitParameter(this)
    }

    override val debugName = "Parameter"
}