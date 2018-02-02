package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangNameElement

interface LangReferenceExpr : LangExpr {
    val nameElement: LangNameElement
    val qualifier: LangExpr?
}