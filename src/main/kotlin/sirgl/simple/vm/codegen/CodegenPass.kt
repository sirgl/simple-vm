package sirgl.simple.vm.codegen

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.stmt.*
import sirgl.simple.vm.ast.support.LangVarDecl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.codegen.assembler.*
import sirgl.simple.vm.driver.phases.SingleVisitorAstPass
import sirgl.simple.vm.resolve.symbols.*
import sirgl.simple.vm.type.*

class CodegenPass : SingleVisitorAstPass() {
    override val name: String = "Codegen"
    lateinit var classWriter: ClassWriter

    override val visitor: LangVisitor = object : LangVisitor() {

        override fun visitClass(cls: LangClass) {
            classWriter = ClassWriter(cls.symbol)
        }

        override fun visitMethod(method: LangMethod) {
            val methodWriter = MethodWriter(classWriter, true)
            val block = method.block
            for (parameter in method.symbol.parameters) {
                methodWriter.addParameter(parameter)
            }
            if (block == null) {
                TODO("Handle native methods")
            } else {
                setupLocalVarSlots(methodWriter, block)
                generateBlock(methodWriter, block)
            }
            methodWriter.emit(NoopInstruction())
            val bytecode = methodWriter.getBytecode()

            val returnTypeDescr = constantPool.addType(method.returnType)
            val parameterDescriptors = method.parameters.map { getVarDescr(it) }
            val classDescriptor = getDescriptorByClassSymbol(method.enclosingClass.symbol)
            val methodNameDescriptor = constantPool.addString(method.name)
            val methodDescr = constantPool.addMethod(classDescriptor, methodNameDescriptor, returnTypeDescr, parameterDescriptors)
            classWriter.addMethodInfo(MethodWithBytecode(methodDescr, bytecode))
        }

        private fun setupLocalVarSlots(methodWriter: MethodWriter, block: LangBlock) {
            SimpleWalker().prepassRecursive(block) {
                val varDecl = it as? LangVarDeclStmt ?: return@prepassRecursive
                varDecl.symbol.slot = methodWriter.addLocalVariable(varDecl.symbol)
            }
        }

        private fun getDescriptorByClassSymbol(classSymbol: ClassSymbol) =
                constantPool.addClass(classSymbol.packageSymbol.name, classSymbol.simpleName)

        override fun visitField(field: LangField) {
            classWriter.addField(getVarDescr(field))
        }

        private fun getVarDescr(variable: LangVarDecl): CPDescriptor {
            return getVarDescr(variable.name, variable.type)
        }

        private fun getVarDescr(symbol: VarSymbol) = getVarDescr(symbol.name, symbol.type)

        private fun getVarDescr(name: String, type: LangType): CPDescriptor {
            val typeDescr = constantPool.addType(type)
            val nameDescr = constantPool.addString(name)
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
                is LangVarDeclStmt -> {
                    stmt.initializer?.let { generateExpr(methodWriter, it) }
                    val varSymbol = stmt.symbol
                    val slot = methodWriter.getVariableSlot(varSymbol)
                    val type = varSymbol.type
                    storeToSlotByType(methodWriter, type, slot)
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
                    generateTypePromotion(castingExprType, targetType, methodWriter)
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
                is LangAssignExpr -> generateAssignExpr(methodWriter, expr)
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
                    // TODO load this if not qualified


                    for (argument in expr.arguments) {
                        generateExpr(methodWriter, argument)
                    }
                    val caller = expr.caller
                    val methodReferenceType = caller.type as MethodReferenceType
                    val methodSymbol = methodReferenceType.methodSymbol
                    val returnTypeDescr = constantPool.addType(methodSymbol.returnType)
                    val parametersDescr = methodSymbol.parameters.map {
                        constantPool.addVar(constantPool.addType(it.type), constantPool.addString(it.name))
                    }
                    val methodNameDescriptor = constantPool.addString(methodSymbol.name)
                    val classDescriptor = getDescriptorByClassSymbol(methodSymbol.enclosingClass)
                    val methodDescr = constantPool.addMethod(classDescriptor, methodNameDescriptor, returnTypeDescr, parametersDescr)

                    methodWriter.emit(CallVirtualInstruction(methodDescr))
                }
                is LangReferenceExpr -> {
                    expr.qualifier?.let { generateExpr(methodWriter, it) }
                    val symbol = expr.resolve()
                    when (symbol) {
                        is LocalVarSymbol, is ParameterSymbol -> {
                            symbol as VarSymbol
                            val type = symbol.type
                            val slot = methodWriter.getVariableSlot(symbol)
                            loadFromSlotByType(methodWriter, type, slot)
                        }
                        is FieldSymbol -> {
                            val fieldType = symbol.type
                            if (!expr.isQualified) {
                                emitLoadThis(methodWriter)
                            }
                            val fieldDescr = getVarDescr(symbol)
                            loadFromFieldByType(methodWriter, fieldType, fieldDescr)
                        }
                    }
                }
            }
            val typePromotion = expr.promoteToType
            if (typePromotion != null) {
                generateTypePromotion(expr.type, typePromotion, methodWriter)
            }
        }

        private fun generateTypePromotion(castingExprType: LangType, targetType: LangType, methodWriter: MethodWriter) {
            when {
                castingExprType === I32Type && targetType === I8Type -> methodWriter.emit(ConvertIntToCharInstruction())
                castingExprType === I8Type && targetType === I32Type -> methodWriter.emit(ConvertCharToIntInstruction())
            }
        }

        private fun generateAssignExpr(methodWriter: MethodWriter, expr: LangAssignExpr) {
            generateExpr(methodWriter, expr.rightValue)
            val leftRef = expr.leftRef
            when (leftRef) {
                is LangReferenceExpr -> {
                    val qualifier = leftRef.qualifier
                    if (qualifier != null) {
                        generateExpr(methodWriter, qualifier)
                    }
                    val leftRefSymbol = leftRef.resolve()
                    when (leftRefSymbol) {
                        is FieldSymbol -> {
                            val leftRefQualifier = leftRef.qualifier
                            if (leftRefQualifier != null) {
                                generateExpr(methodWriter, leftRefQualifier)
                            } else {
                                emitLoadThis(methodWriter)
                            }
                            val fieldType = leftRefSymbol.type
                            val fieldDescr = getVarDescr(leftRefSymbol)
                            storeToFieldByType(methodWriter, fieldType, fieldDescr)
                        }
                        is ParameterSymbol, is LocalVarSymbol -> {
                            leftRefSymbol as VarSymbol
                            val leftRefType = leftRefSymbol.type
                            val slot = methodWriter.getVariableSlot(leftRefSymbol)
                            storeToSlotByType(methodWriter, leftRefType, slot)
                        }
                    }
                }
                else -> {
                    throw UnsupportedOperationException("Unsupported operation")
                }
            }
        }

        private fun storeToSlotByType(methodWriter: MethodWriter, leftRefType: LangType, slot: Short) {
            when (leftRefType) {
                is ClassType, is ArrayType -> methodWriter.emit(StoreReferenceInstruction(slot))
                is I32Type -> methodWriter.emit(StoreIntInstruction(slot))
                is BoolType -> methodWriter.emit(StoreBoolInstruction(slot))
                is I8Type -> {
                    methodWriter.emit(ConvertCharToIntInstruction())
                    methodWriter.emit(StoreIntInstruction(slot))
                }
                else -> throw UnsupportedOperationException()
            }
        }


        private fun storeToFieldByType(methodWriter: MethodWriter, leftRefType: LangType, fieldDescr: CPDescriptor) {
            methodWriter.emit(when (leftRefType) {
                is ClassType, is ArrayType -> StoreFieldReferenceInstruction(fieldDescr)
                is I32Type -> StoreFieldIntInstruction(fieldDescr)
                is BoolType -> StoreFieldBoolInstruction(fieldDescr)
                else -> throw UnsupportedOperationException()
            })
        }

        private fun loadFromSlotByType(methodWriter: MethodWriter, leftRefType: LangType, slot: Short) {
            when (leftRefType) {
                is ClassType, is ArrayType -> methodWriter.emit(LoadReferenceInstruction(slot))
                is I32Type -> methodWriter.emit(LoadIntInstruction(slot))
                is BoolType -> methodWriter.emit(LoadBoolInstruction(slot))
                is I8Type -> {
                    methodWriter.emit(LoadIntInstruction(slot))
                    methodWriter.emit(ConvertIntToCharInstruction())
                }
                else -> throw UnsupportedOperationException()
            }
        }

        private fun loadFromFieldByType(methodWriter: MethodWriter, leftRefType: LangType, fieldDescr: CPDescriptor) {
            methodWriter.emit(when (leftRefType) {
                is ClassType, is ArrayType -> LoadFieldReferenceInstruction(fieldDescr)
                is I32Type -> LoadFieldIntInstruction(fieldDescr)
                is BoolType -> LoadFieldBoolInstruction(fieldDescr)
                else -> throw UnsupportedOperationException()
            })
        }

        private fun emitLoadThis(methodWriter: MethodWriter) {
            methodWriter.emit(LoadReferenceInstruction(0))
        }

        private val constantPool get() = classWriter.constantPool
    }
}