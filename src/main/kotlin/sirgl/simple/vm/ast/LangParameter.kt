package sirgl.simple.vm.ast

interface LangParameter : LangVarDecl {
    override val parent: LangMethod
}