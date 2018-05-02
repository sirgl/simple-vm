package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.ext.getClass
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.LangType

class MethodSymbolImpl(
    override val name: String,
    override val symbolSource: SymbolSource,
    override val enclosingClass: ClassSymbol,
    override val returnType: LangType,
    override val parameters: List<ParameterSymbol>
) : MethodSymbol

fun LangMethod.toSymbol() : MethodSymbol =
    MethodSymbolImpl(name, getSymbolSource(), getClass().symbol, returnType, parameters.map { it.toSymbol() })