package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.support.LangFunction
import sirgl.simple.vm.resolve.symbols.MethodSymbol

interface LangMethod : LangMember, LangFunction {
    val name: String
    val returnTypeElement: LangTypeElement?
}