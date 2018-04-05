package sirgl.simple.vm.codegen

import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangMethod
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.stmt.LangExprStmt
import sirgl.simple.vm.ast.stmt.LangIfStmt
import sirgl.simple.vm.ast.stmt.LangVarDeclStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.codegen.assembler.*
import sirgl.simple.vm.type.I32Type

class CodegenVisitor : LangVisitor() {
    val pool = ConstantPool()

    override fun visitMethod(method: LangMethod) {
        super.visitMethod(method)
        val mw = MethodWriter()
        val methodContext = MethodContext() // TODO it will not from zero if parameters present (always, considering this and absence of )
        val block = method.block ?: return
        generateBlockCode(block, mw, pool, methodContext)
    }

    private fun generateBlockCode(block: LangBlock, mw: MethodWriter, pool: ConstantPool, methodContext: MethodContext) {
        for (stmt in block.stmts) {
            when (stmt) {
                is LangIfStmt -> {
                    generateExprCode(stmt.condition, mw, pool)
                    val ifTrueInstruction = IfTrueInstruction(null)
                    val ifFalseInstruction = IfFalseInstruction(null)
                    mw.emit(ifFalseInstruction)
                    val thenBranch = mw.label()
                    ifTrueInstruction.label = thenBranch
                    val elseBlock = stmt.elseBlock
                    if (elseBlock != null) {
                        generateBlockCode(elseBlock, mw, pool, methodContext)
                    }
                    val elseOrAfterIfLabel = mw.label()
                    ifFalseInstruction.label = elseOrAfterIfLabel
                }
                is LangVarDeclStmt -> {
                    val initializer = stmt.initializer
                    if (initializer != null) {
                        stmt.slot = methodContext.slot // TODO Can be more smart slot allocation (branches of if can get different slots)
                        generateExprCode(initializer, mw, pool)
                        when (initializer.type) {
                            I32Type -> mw.emit(StoreIntInstruction(methodContext.slot))
                        }
                        methodContext.slot++
                    }
                }
                is LangExprStmt -> generateExprCode(stmt.expr, mw, pool)
            }
        }
    }


    // Probably better to do it with visitor
    fun generateExprCode(expr: LangExpr, mw: MethodWriter, pool: ConstantPool) {
        when (expr) {
            is LangBinaryExpr -> {
                generateExprCode(expr.left, mw, pool)
                generateExprCode(expr.right, mw, pool)
                mw.emit(BinopInstruction(expr.opTypeBinary))
            }
            is LangPrefixExpr -> {
                generateExprCode(expr, mw, pool)
                mw.emit(UnaryInstruction(expr.prefixOperatorType))
            }
            is LangIntLiteralExpr -> {
                val poolIndex = pool.addInt(expr.value)
                mw.emit(IloadConstInstruction(poolIndex.toShort()))
            }
            is LangCharLiteralExpr -> {
                val poolIndex = pool.addInt(expr.value.toInt())
                mw.emit(IloadConstInstruction(poolIndex.toShort()))
                mw.emit(ConvertIntToCharInstruction())
            }
            is LangBoolLiteralExpr -> {
                when (expr.value) {
                    true -> mw.emit(LoadTrueInstruction())
                    false -> mw.emit(LoadFalseInstruction())
                }
            }
            is LangNullExpr -> {
                mw.emit(LoadNullInstruction())
            }
            is LangAssignExpr -> {
                generateExprCode(expr.rightValue, mw, pool)
                val leftRef = expr.leftRef
                when (leftRef) {
                    is LangReferenceExpr -> {
                        leftRef.resolve() // TODO
                    }
                }
            }
        }
    }
}

class MethodContext(
        var slot: Short = 0
)