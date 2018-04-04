package sirgl.simple.vm.ast

import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.signatures.ClassSignature

interface LangClass : AstNode, Scope {
    fun toSignature(sourceFile: SourceFile): ClassSignature

    override val parent: LangFile
    val simpleName: String
    val qualifiedName: String
    val parentClassName: String?
    val members: List<LangMember>

    val fields: List<LangField>
        get() = members.filter { it is LangField }.map { it as LangField }
    val methods: List<LangMethod>
        get() = members.filter { it is LangMethod }.map { it as LangMethod }

    val signature: ClassSignature
    val parentClassSignature: ClassSignature
}