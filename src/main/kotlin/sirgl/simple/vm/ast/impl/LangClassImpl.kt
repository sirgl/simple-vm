package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangMember
import sirgl.simple.vm.ast.ext.getFile
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.scope.Scope
import sirgl.simple.vm.signatures.ClassSignature
import sirgl.simple.vm.type.ClassType

class LangClassImpl(
        private val scope: Scope,
        override val simpleName: String,
        override val members: List<LangMember>,
        firstLexeme: Lexeme,
        endLexeme: Lexeme,
        override val parentClassName: String?
) : AstNodeImpl(firstLexeme, endLexeme), LangClass, Scope by scope {
    override fun toSignature(sourceFile: SourceFile): ClassSignature {
        (parent as LangFileImpl).sourceFile = sourceFile
        val classSignature = signatureCache ?: createSignature(sourceFile)
        signatureCache = classSignature
        return classSignature
    }

    init {
        scope.element = this
    }

    override val qualifiedName: String by lazy { findFqn() }
    override lateinit var parent: LangFile

    private fun findFqn(): String {
        val declaredPackage = parent.packageDeclaration?.declaredPackage ?: return simpleName
        return "$declaredPackage.$simpleName"
    }

    override fun accept(visitor: LangVisitor) {
        visitor.visitClass(this)
    }

    override val debugName = "Class"

    override fun toString() = super.toString() + " name: $simpleName" + if (parentClassName != null) " parent: $parentClassName" else ""

    override val children: List<AstNode> = members

    override val signature: ClassSignature
        get() {
            val classSignature = signatureCache ?: createSignature(getFile().sourceFile)
            signatureCache = classSignature
            return classSignature
        }
    private var signatureCache: ClassSignature? = null

    private fun createSignature(sourceFile: SourceFile): ClassSignature {
        val fieldSignatures = fields.map { it.signature }
        val methodSignatures = methods.map { it.signature }
        val type = ClassType(qualifiedName)
        val classSignature = ClassSignature(sourceFile, simpleName, parent.packageDeclaration?.declaredPackage, fieldSignatures, methodSignatures, type)
        type.classSignature = classSignature
        return classSignature
    }
}