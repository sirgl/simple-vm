package sirgl.simple.vm.ast.support

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangTypeElement
import sirgl.simple.vm.resolve.symbols.VarSymbol
import sirgl.simple.vm.type.LangType

interface LangVarDecl {
    val name: String
    val typeElement: LangTypeElement?
    val type: LangType
    val initializer: LangExpr?
    val symbol: VarSymbol
}