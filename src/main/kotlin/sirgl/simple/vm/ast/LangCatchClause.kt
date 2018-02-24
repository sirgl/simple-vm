package sirgl.simple.vm.ast

interface LangCatchClause : AstNode {
    val parameter: LangParameter
    val block: LangBlock
}