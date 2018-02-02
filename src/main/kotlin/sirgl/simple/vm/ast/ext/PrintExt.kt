package sirgl.simple.vm.ast.ext

import sirgl.simple.vm.ast.AstNode

val AstNode.rangeText: String
    get() = "@($startOffset: $endOffset)"