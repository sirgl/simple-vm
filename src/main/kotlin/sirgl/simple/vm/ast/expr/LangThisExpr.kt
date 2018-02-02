package sirgl.simple.vm.ast.expr

import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.ext.getParentOfClass

interface LangThisExpr : LangExpr {
    val currentClass: LangClass
    get() = getParentOfClass()
}