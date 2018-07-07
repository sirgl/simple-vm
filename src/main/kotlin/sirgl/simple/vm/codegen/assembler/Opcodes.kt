package sirgl.simple.vm.codegen.assembler

enum class Opcode(val inlineOerandType: InlineOperandType) {
    // integer arithmetic
    ADD(InlineOperandType.NoInlineOperand),
    MUL(InlineOperandType.NoInlineOperand),
    SUB(InlineOperandType.NoInlineOperand),
    DIV(InlineOperandType.NoInlineOperand),
    REM(InlineOperandType.NoInlineOperand),

    // integer comparison
    ILT(InlineOperandType.NoInlineOperand),
    ILE(InlineOperandType.NoInlineOperand),
    IGT(InlineOperandType.NoInlineOperand),
    IGE(InlineOperandType.NoInlineOperand),
    IEQ(InlineOperandType.NoInlineOperand),

    // noop
    NOOP(InlineOperandType.NoInlineOperand),

    // integer unary
    NEG(InlineOperandType.NoInlineOperand),
    INV(InlineOperandType.NoInlineOperand),

    // control flow
    GOTO(InlineOperandType.Label),
    IF_TRUE(InlineOperandType.Label),
    IF_FALSE(InlineOperandType.Label),
    IF_NULL(InlineOperandType.Label),
    IF_NOT_NULL(InlineOperandType.Label),

    // constant loading
    ILOAD_CONST(InlineOperandType.ConstantPoolEntry), // int
    CLOAD_CONST(InlineOperandType.ConstantPoolEntry), // char
    SLOAD_CONST(InlineOperandType.ConstantPoolEntry), // string
    LOAD_TRUE(InlineOperandType.NoInlineOperand),
    LOAD_FALSE(InlineOperandType.NoInlineOperand),
    LOAD_ONE(InlineOperandType.NoInlineOperand),
    LOAD_ZERO(InlineOperandType.NoInlineOperand),
    LOAD_NULL(InlineOperandType.NoInlineOperand),

    // discarding method result in ExprStmt
    POP(InlineOperandType.NoInlineOperand),

    // conversion
    I2C(InlineOperandType.NoInlineOperand),
    C2I(InlineOperandType.NoInlineOperand),

    // store
    ISTORE_SLOT(InlineOperandType.VariableSlot), // int
    CSTORE_SLOT(InlineOperandType.VariableSlot), // char
    BSTORE_SLOT(InlineOperandType.VariableSlot), // boolean
    RSTORE_SLOT(InlineOperandType.VariableSlot), // reference

    // load
    ILOAD_SLOT(InlineOperandType.VariableSlot), // int
    CLOAD_SLOT(InlineOperandType.VariableSlot), // char
    BLOAD_SLOT(InlineOperandType.VariableSlot), // boolean
    RLOAD_SLOT(InlineOperandType.VariableSlot), // reference

    // typecheck
    TYPECHECK(InlineOperandType.ConstantPoolEntry),

    CALL_VIRTUAL(InlineOperandType.ConstantPoolEntry),
    CALL_STATIC(InlineOperandType.ConstantPoolEntry),
    RETURN(InlineOperandType.NoInlineOperand),
    ;
    // TODO calls
    // TODO Array operations

    val hasInlineOperand: Boolean
        get() = inlineOerandType != InlineOperandType.NoInlineOperand
}

enum class InlineOperandType {
    NoInlineOperand,
    ConstantPoolEntry,
    Label,
    VariableSlot
}