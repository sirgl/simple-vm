package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.resolve.symbols.ParameterSymbol

interface LangParameter : LangVarDecl, AstNode {
    override val parent: AstNode
    override val symbol: ParameterSymbol
    override val typeElement: LangTypeElement
}