package sirgl.simple.vm.ast.stmt

import sirgl.simple.vm.ast.LangStmt
import sirgl.simple.vm.ast.LangTypeElement
import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.resolve.symbols.LocalVarSymbol

interface LangVarDeclStmt : LangVarDecl, LangStmt {
    override val symbol: LocalVarSymbol
    override val typeElement: LangTypeElement

}