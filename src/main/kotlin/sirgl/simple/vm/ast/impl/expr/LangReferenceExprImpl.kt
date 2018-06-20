package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.findParentOfClass
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.resolve.symbols.*
import sirgl.simple.vm.type.*

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
        val symbol = resolve() ?: return@lazy UnknownType
        when (symbol) {
            is VarSymbol -> symbol.type
            is MethodSymbol -> MethodReferenceType(symbol.name)
            is ClassSymbol -> symbol.type
            else -> UnknownType
        }
    }

    override fun accept(visitor: LangVisitor) = visitor.visitReferenceExpr(this)

    override val debugName = "ReferenceExpr"

    override fun toString() = super.toString() + " name: $name"

    override val children: List<AstNode> = when (qualifier) {
        null -> emptyList()
        else -> listOf(qualifier)
    }

    private var resolvedSymbol: Symbol? = null

    private fun resolveWithoutCache(): Symbol? {
        qualifier ?: return findParentOfClass<Scoped>()?.scope?.resolve(name, startOffset)
        val qualifierType = qualifier.type
        return when (qualifierType) {
            is ClassType -> qualifierType.classSymbol.resolve(name, startOffset)
            is ArrayType -> when (name) {
                "length" -> LengthSymbolImpl
                else -> null
            }
            is UnknownType -> when (qualifier) {
                is LangReferenceExpr -> when (qualifier.resolve()) {
                    is PackageSymbol -> TODO()
                    else -> null
                }
                else -> null
            }
            else -> null
        }
    }

    override fun resolve(): Symbol? {
        if (resolvedSymbol == null) {
            resolvedSymbol = resolveWithoutCache()
        }
        return resolvedSymbol
    }
}