package sirgl.simple.vm.ast

interface LangModifier : AstNode {
    val kind: ModifierKind
}

enum class ModifierKind {
    Native
}