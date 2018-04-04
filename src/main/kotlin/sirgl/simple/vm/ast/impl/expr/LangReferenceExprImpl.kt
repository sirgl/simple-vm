package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.getScope
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.signatures.ClassSignature
import sirgl.simple.vm.resolve.signatures.MethodSignature
import sirgl.simple.vm.resolve.signatures.Signature
import sirgl.simple.vm.resolve.signatures.VariableSignature
import sirgl.simple.vm.type.LangType
import sirgl.simple.vm.type.MethodReferenceType
import sirgl.simple.vm.type.UnknownType

class LangReferenceExprImpl(
        nameLexeme: Lexeme,
        override val name: String,
        override val qualifier: LangExpr? = null,
        override val isSuper: Boolean,
        override val isThis: Boolean
) : LangReferenceExpr, LangExprImpl(
        nameLexeme.startOffset,
        qualifier?.endOffset ?: nameLexeme.endOffset,
        nameLexeme.line
) {
    override val type: LangType by lazy {
        val signature = resolve()
        when (signature) {
            is ClassSignature -> signature.type
            is MethodSignature -> MethodReferenceType(signature.name).apply { method = signature }
            is VariableSignature -> signature.type
            null -> UnknownType
            else -> throw UnsupportedOperationException("This kind of signature is not supported: ${signature.javaClass}")
        }
//        (resolve() as? ClassSignature)?.type ?: UnknownType
    }

    override fun accept(visitor: LangVisitor) = visitor.visitReferenceExpr(this)

    override val debugName = "ReferenceExpr"

    override fun toString() = super.toString() + " name: $name"

    override val children: List<AstNode> = when (qualifier) {
        null -> emptyList()
        else -> listOf(qualifier)
    }

    private val resolvedSignature: Signature? by lazy {
        getScope().resolve(this)
    }

    override fun resolve() = resolvedSignature
}