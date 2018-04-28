package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.resolve.symbols.FieldSymbol

interface LangField : LangMember, LangVarDecl {
    override val symbol: FieldSymbol
    override val typeElement: LangTypeElement
}