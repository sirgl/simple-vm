package sirgl.simple.vm.codegen

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.stmt.*
import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.codegen.assembler.*
import sirgl.simple.vm.codegen.assembler.MethodWriter
import sirgl.simple.vm.driver.phases.SingleVisitorAstPass
import sirgl.simple.vm.type.ClassType
import sirgl.simple.vm.type.I32Type
import sirgl.simple.vm.type.I8Type

class CodegenPass : SingleVisitorAstPass() {
    override val name: String = "Codegen"
    lateinit var classWriter: ClassWriter

    override val visitor: LangVisitor = object : LangVisitor() {

        override fun visitClass(cls: LangClass) {
            classWriter = ClassWriter(cls.symbol)
        }

        override fun visitMethod(method: LangMethod) {
            val methodWriter = MethodWriter(classWriter)
            val block = method.block
            if (block == null) {
                TODO("Handle native methods")
            } else {
                generateBlock(methodWriter, block)
            }
            methodWriter.emit(NoopInstruction())
            val bytecode = methodWriter.getBytecode()

            val returnTypeDescr = constantPool.addType(method.returnType)
            val parameterDescriptors = method.parameters.map { getVarDescr(it) }
            val methodDescr = constantPool.addMethod(method.name, returnTypeDescr, parameterDescriptors)
            classWriter.addMethodInfo(MethodWithBytecode(methodDescr, bytecode))
        }

        override fun visitField(field: LangField) {
            classWriter.addField(getVarDescr(field))
        }

        private fun getVarDescr(variable: LangVarDecl): CPDescriptor {
            val typeDescr = constantPool.addType(variable.type)
            val nameDescr = constantPool.addString(variable.name)
            return constantPool.addVar(typeDescr, nameDescr)
        }

        private fun generateBlock(methodWriter: MethodWriter, block: LangBlock) {
            for (stmt in block.stmts) {
                generateStmt(methodWriter, stmt)
            }
        }

        private fun generateStmt(methodWriter: MethodWriter, stmt: LangStmt) {
            when (stmt) {
                is LangIfStmt -> {
                    generateExpr(methodWriter, stmt.condition)
                    val goToElse = IfFalseInstruction(null)
                    methodWriter.emit(goToElse)
                    generateBlock(methodWriter, stmt.thenBlock)
                    val elseBlock = stmt.elseBlock
                    val jumpToAfterElse: GotoInstruction?
                    if (elseBlock != null) {
                        jumpToAfterElse = GotoInstruction(null)
                        methodWriter.emit(jumpToAfterElse)
                    } else {
                        jumpToAfterElse = null
                    }
                    goToElse.label = methodWriter.labelNext()
                    if (elseBlock != null) {
                        generateBlock(methodWriter, elseBlock)
                        jumpToAfterElse!!.label = methodWriter.labelCurrent()
                    }
                }
                is LangWhileStmt -> {
                    val goToCondition = GotoInstruction(null)
                    methodWriter.emit(goToCondition)
                    val bodyStart = methodWriter.labelNext()
                    generateBlock(methodWriter, stmt.block)
                    val conditionStart = methodWriter.labelNext()
                    goToCondition.label = conditionStart
                    for (continueGoto in methodWriter.continueGotos) {
                        continueGoto.label = conditionStart
                    }
                    methodWriter.continueGotos.clear()
                    generateExpr(methodWriter, stmt.condition)
                    methodWriter.emit(IfTrueInstruction(bodyStart))
                    val afterLoop = methodWriter.labelCurrent()
                    for (breakGoto in methodWriter.breakGotos) {
                        breakGoto.label = afterLoop
                    }
                    methodWriter.breakGotos.clear()
                }
                is LangBreakStmt -> {
                    methodWriter.breakGotos.add(GotoInstruction(null))
                }
                is LangContinueStmt -> {
                    methodWriter.continueGotos.add(GotoInstruction(null))
                }
                is LangExprStmt -> {
                    generateExpr(methodWriter, stmt.expr)
                    methodWriter.emit(PopInstruction()) // Discarding operation result
                }
                is LangReturnStmt -> {
                    val expr = stmt.expr
                    if (expr != null) {
                        generateExpr(methodWriter, expr)
                    }
                    methodWriter.emit(ReturnInstruction())
                }
            }
        }

        private fun generateExpr(methodWriter: MethodWriter, expr: LangExpr) {
            when (expr) {
                is LangBinaryExpr -> {
                    generateExpr(methodWriter, expr.left)
                    generateExpr(methodWriter, expr.right)
                    methodWriter.emit(BinopInstruction(expr.opTypeBinary)) // TODO short circuit operations
                }
                is LangTypeCheckExpr -> {
                    val builder = constantPool
                    val classSymbol = (expr.targetType as ClassType).classSymbol
                    val classEntry = builder.addClass(classSymbol.packageSymbol.name, classSymbol.simpleName)
                    methodWriter.emit(TypecheckInstruction(classEntry))
                }
                is LangCastExpr -> {
                    val targetType = expr.targetType
                    val castingExprType = expr.expr.type
                    when {
                        castingExprType === I32Type && targetType === I8Type -> methodWriter.emit(ConvertIntToCharInstruction())
                        castingExprType === I8Type && targetType === I32Type -> methodWriter.emit(ConvertCharToIntInstruction())
                    }
                }
                is LangPrefixExpr -> {
                    generateExpr(methodWriter, expr.expr)
                    val prefixOperatorType = expr.prefixOperatorType
                    if (prefixOperatorType != PrefixOperatorType.Plus) {
                        methodWriter.emit(UnaryInstruction(prefixOperatorType))
                    }
                }
                is LangNullExpr -> methodWriter.emit(LoadNullInstruction())
                is LangParenExpr -> generateExpr(methodWriter, expr.expr)
                is LangAssignExpr -> {
                    // TODO
                }
                is LangCharLiteralExpr -> {
                    val cpDescriptor = constantPool.addChar(expr.value)
                    methodWriter.emit(IloadConstInstruction(cpDescriptor))
                }
                is LangIntLiteralExpr -> {
                    val cpDescriptor = constantPool.addInt(expr.value)
                    methodWriter.emit(IloadConstInstruction(cpDescriptor))
                }
                is LangStringLiteralExpr -> {
                    val cpDescriptor = constantPool.addString(expr.value)
                    methodWriter.emit(IloadConstInstruction(cpDescriptor))
                }
                is LangBoolLiteralExpr -> {
                    methodWriter.emit(
                            if (expr.value)
                                LoadTrueInstruction()
                            else
                                LoadFalseInstruction()
                    )
                }
                is LangElementAccessExpr -> {
                    // TODO
                }
                is LangCallExpr -> {
                    // TODO
                }
                is LangReferenceExpr -> {
                    // TODO
                }
            }
        }

        private val constantPool get() = classWriter.constantPool
    }
}