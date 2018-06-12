package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangField
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.LangType

class LocalVarSymbolImpl(
    override val name: String,
    override val type: LangType,
    val element: AstNode,
    varNode: LangVarDeclStmt
) : LocalVarSymbol {
    override val offset: Int = varNode.endOffset
    override val symbolSource: SymbolSource by lazy { element.getSymbolSource() }
}

fun LangVarDeclStmt.toSymbol(varNode: LangVarDeclStmt) : LocalVarSymbol = LocalVarSymbolImpl(name, type, this, varNode)