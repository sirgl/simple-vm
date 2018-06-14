package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.ast.support.LangFunction
import sirgl.simple.vm.resolve.symbols.MethodSymbolImpl
import sirgl.simple.vm.resolve.symbols.toSymbol

interface LangConstructor : LangMember, LangFunction


val constructorName =  "__init__"

fun LangConstructor.toSymbol() =
    MethodSymbolImpl(constructorName, getSymbolSource(), returnType, parameters.map { it.toSymbol() })