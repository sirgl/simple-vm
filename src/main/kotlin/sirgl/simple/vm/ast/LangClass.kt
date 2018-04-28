package sirgl.simple.vm.ast

import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.resolve.symbols.ClassSymbol

interface LangClass : AstNode, Scoped {
    override val parent: LangFile
    val simpleName: String
    val qualifiedName: String
    val parentClassName: String?
    val parentClassReferenceElement: LangReferenceElement?
    val members: List<LangMember>

    val fields: List<LangField>
        get() = members.filter { it is LangField }.map { it as LangField }
    val methods: List<LangMethod>
        get() = members.filter { it is LangMethod }.map { it as LangMethod }
    val symbol: ClassSymbol
}