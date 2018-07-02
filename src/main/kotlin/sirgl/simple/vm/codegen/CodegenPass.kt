package sirgl.simple.vm.codegen

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.expr.*
import sirgl.simple.vm.ast.stmt.LangExprStmt
import sirgl.simple.vm.ast.stmt.LangIfStmt
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
            val bytecode = methodWriter.getBytecode()

            val returnTypeDescr = cpBuilder.addType(method.returnType)
            val parameterDescriptors = method.parameters.map { getVarDescr(it) }
            val methodDescr = cpBuilder.addMethod(name, returnTypeDescr, parameterDescriptors)
            classWriter.addMethodInfo(MethodWithBytecode(methodDescr, bytecode))
        }

        override fun visitField(field: LangField) {
            classWriter.addField(getVarDescr(field))
        }

        private fun getVarDescr(variable: LangVarDecl): CPDescriptor {
            val typeDescr = cpBuilder.addType(variable.type)
            val nameDescr = cpBuilder.addString(variable.name)
            return cpBuilder.addVar(typeDescr, nameDescr)
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
                    val jumpToAfterElse = GotoInstruction(null) // TODO set label and if
                    methodWriter.emit(jumpToAfterElse)
                    goToElse.label = methodWriter.labelNext()
                    val elseBlock = stmt.elseBlock
                    if (elseBlock != null) {
                        generateBlock(methodWriter, elseBlock)
                    }
                }
                is LangExprStmt -> {
                    generateStmt(methodWriter, stmt)
                    methodWriter.emit(PopInstruction()) // Discarding operation result
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
                    val builder = cpBuilder
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
                    val cpDescriptor = cpBuilder.addChar(expr.value)
                    methodWriter.emit(IloadConstInstruction(cpDescriptor))
                }
                is LangIntLiteralExpr -> {
                    val cpDescriptor = cpBuilder.addInt(expr.value)
                    methodWriter.emit(IloadConstInstruction(cpDescriptor))
                }
                is LangStringLiteralExpr -> {
                    val cpDescriptor = cpBuilder.addString(expr.value)
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

        private val cpBuilder get() = classWriter.cp
    }
}