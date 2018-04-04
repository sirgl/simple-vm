package sirgl.simple.vm.resolve

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.ext.findParentOfClass
import sirgl.simple.vm.ast.ext.getClass
import sirgl.simple.vm.resolve.signatures.Signature

class ScopeImpl() : Scope {
    override lateinit var element: AstNode
    override val parentScope: Scope? by lazy {
        element.findParentOfClass<Scope>()
    }
    private val localSignatures = mutableMapOf<String, Signature>()
    private val multipleDeclarations = mutableMapOf<String, MutableSet<Signature>>()

    override fun getMultipleDeclarations() = multipleDeclarations.keys.toList()

    override fun resolve(reference: LangReferenceExpr): Signature? {
        if (reference.isThis) {
            return reference.getClass().signature
        }
        if (reference.isSuper) {
            // TODO
        }
        if (!reference.isQualified) {
            val targetName = reference.name
            return localSignatures[targetName]
                    ?: parentScope?.resolve(reference)
        } else {
            // TODO make it in global scope
        }
        TODO()
    }

    override fun register(signature: Signature) {
        val key = signature.name
        val previous = localSignatures.put(key, signature)
        if (previous != null) {
            val multipleDeclarationsSet = multipleDeclarations[key] ?: mutableSetOf()
            multipleDeclarationsSet.add(signature)
            multipleDeclarations[key] = multipleDeclarationsSet
        }
//        signatures[signature.name] = signature
    }

    override fun containsName(name: String): Boolean {
        return localSignatures.containsKey(name) || parentScope?.containsName(name) ?: false
    }

}