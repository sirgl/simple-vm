use std::slice::Iter;


#[derive(Copy, Clone)]
pub enum Opcode {
    Add = 0,
    Mul,
    Sub,
    Div,
    Rem,

    Ilt,
    Ile,
    Igt,
    Ige,
    Ieq,

    Noop,
    Neg,
    Inv,
    Goto,
    IfTrue,
    IfFalse,
    IfNull,
    IfNotNull,

    IloadConst,
    CloadConst,
    SloadConst,
    LoadTrue,
    LoadFalse,
    LoadOne,
    LoadZero,
    LoadNull,

    Pop,

    I2c,
    C2i,

    IstoreSlot,
    CstoreSlot,
    BstoreSlot,
    RstoreSlot,
    IloadSlot,
    CloadSlot,
    BloadSlot,
    RloadSlot,

    IstoreField,
    CstoreField,
    BstoreField,
    RstoreField,
    IloadField,
    CloadField,
    BloadField,
    RloadField,
    Typecheck,
    CallVirtual,
    CallConstructor,
    CallStatic,
    Return,
}


#[derive(Debug)]
pub struct OpcodeInfo {
    name: &'static str,
    has_inline_operand: bool,
}

static ADD_INFO: OpcodeInfo = OpcodeInfo {
    name: "ADD",
    has_inline_operand: false,
};

static MUL_INFO: OpcodeInfo = OpcodeInfo {
    name: "MUL",
    has_inline_operand: false,
};

static SUB_INFO: OpcodeInfo = OpcodeInfo {
    name: "SUB",
    has_inline_operand: false,
};

static DIV_INFO: OpcodeInfo = OpcodeInfo {
    name: "DIV",
    has_inline_operand: false,
};

static REM_INFO: OpcodeInfo = OpcodeInfo {
    name: "REM",
    has_inline_operand: false,
};

static ILT_INFO: OpcodeInfo = OpcodeInfo {
    name: "ILT",
    has_inline_operand: false,
};

static ILE_INFO: OpcodeInfo = OpcodeInfo {
    name: "ILE",
    has_inline_operand: false,
};

static IGT_INFO: OpcodeInfo = OpcodeInfo {
    name: "IGT",
    has_inline_operand: false,
};

static IGE_INFO: OpcodeInfo = OpcodeInfo {
    name: "IGE",
    has_inline_operand: false,
};

static IEQ_INFO: OpcodeInfo = OpcodeInfo {
    name: "IEQ",
    has_inline_operand: false,
};

static NOOP_INFO: OpcodeInfo = OpcodeInfo {
    name: "NOOP",
    has_inline_operand: false,
};

static NEG_INFO: OpcodeInfo = OpcodeInfo {
    name: "NEG",
    has_inline_operand: false,
};

static INV_INFO: OpcodeInfo = OpcodeInfo {
    name: "INV",
    has_inline_operand: false,
};

static GOTO_INFO: OpcodeInfo = OpcodeInfo {
    name: "GOTO",
    has_inline_operand: true,
};

static IF_TRUE_INFO: OpcodeInfo = OpcodeInfo {
    name: "IF_TRUE",
    has_inline_operand: true,
};

static IF_FALSE_INFO: OpcodeInfo = OpcodeInfo {
    name: "IF_FALSE",
    has_inline_operand: true,
};

static IF_NULL_INFO: OpcodeInfo = OpcodeInfo {
    name: "IF_NULL",
    has_inline_operand: true,
};

static IF_NOT_NULL_INFO: OpcodeInfo = OpcodeInfo {
    name: "IF_NOT_NULL",
    has_inline_operand: true,
};

static ILOAD_CONST_INFO: OpcodeInfo = OpcodeInfo {
    name: "ILOAD_CONST",
    has_inline_operand: true,
};

static CLOAD_CONST_INFO: OpcodeInfo = OpcodeInfo {
    name: "CLOAD_CONST",
    has_inline_operand: true,
};

static SLOAD_CONST_INFO: OpcodeInfo = OpcodeInfo {
    name: "SLOAD_CONST",
    has_inline_operand: true,
};

static LOAD_TRUE_INFO: OpcodeInfo = OpcodeInfo {
    name: "LOAD_TRUE",
    has_inline_operand: false,
};

static LOAD_FALSE_INFO: OpcodeInfo = OpcodeInfo {
    name: "LOAD_FALSE",
    has_inline_operand: false,
};

static LOAD_ONE_INFO: OpcodeInfo = OpcodeInfo {
    name: "LOAD_ONE",
    has_inline_operand: false,
};

static LOAD_ZERO_INFO: OpcodeInfo = OpcodeInfo {
    name: "LOAD_ZERO",
    has_inline_operand: false,
};

static LOAD_NULL_INFO: OpcodeInfo = OpcodeInfo {
    name: "LOAD_NULL",
    has_inline_operand: false,
};

static POP_INFO: OpcodeInfo = OpcodeInfo {
    name: "POP",
    has_inline_operand: false,
};

static I2C_INFO: OpcodeInfo = OpcodeInfo {
    name: "I2C",
    has_inline_operand: false,
};

static C2I_INFO: OpcodeInfo = OpcodeInfo {
    name: "C2I",
    has_inline_operand: false,
};

static ISTORE_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "ISTORE_SLOT",
    has_inline_operand: true,
};

static CSTORE_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "CSTORE_SLOT",
    has_inline_operand: true,
};

static BSTORE_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "BSTORE_SLOT",
    has_inline_operand: true,
};

static RSTORE_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "RSTORE_SLOT",
    has_inline_operand: true,
};

static ILOAD_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "ILOAD_SLOT",
    has_inline_operand: true,
};

static CLOAD_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "CLOAD_SLOT",
    has_inline_operand: true,
};

static BLOAD_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "BLOAD_SLOT",
    has_inline_operand: true,
};

static RLOAD_SLOT_INFO: OpcodeInfo = OpcodeInfo {
    name: "RLOAD_SLOT",
    has_inline_operand: true,
};

static ISTORE_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "ISTORE_FIELD",
    has_inline_operand: true,
};

static CSTORE_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "CSTORE_FIELD",
    has_inline_operand: true,
};

static BSTORE_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "BSTORE_FIELD",
    has_inline_operand: true,
};

static RSTORE_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "RSTORE_FIELD",
    has_inline_operand: true,
};

static ILOAD_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "ILOAD_FIELD",
    has_inline_operand: true,
};

static CLOAD_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "CLOAD_FIELD",
    has_inline_operand: true,
};

static BLOAD_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "BLOAD_FIELD",
    has_inline_operand: true,
};

static RLOAD_FIELD_INFO: OpcodeInfo = OpcodeInfo {
    name: "RLOAD_FIELD",
    has_inline_operand: true,
};

static TYPECHECK_INFO: OpcodeInfo = OpcodeInfo {
    name: "TYPECHECK",
    has_inline_operand: true,
};

static CALL_VIRTUAL_INFO: OpcodeInfo = OpcodeInfo {
    name: "CALL_VIRTUAL",
    has_inline_operand: true,
};

static CALL_CONSTRUCTOR_INFO: OpcodeInfo = OpcodeInfo {
    name: "CALL_CONSTRUCTOR",
    has_inline_operand: true,
};

static CALL_STATIC_INFO: OpcodeInfo = OpcodeInfo {
    name: "CALL_STATIC",
    has_inline_operand: true,
};

static RETURN_INFO: OpcodeInfo = OpcodeInfo {
    name: "RETURN",
    has_inline_operand: false,
};

impl Opcode {
    pub fn as_byte(self) -> u8 {
        self as u8
    }

    pub fn name(self) -> &'static str {
        self.get_info().name
    }

    pub fn has_inline_operand(self) -> bool {
        self.get_info().has_inline_operand
    }

    fn get_info(self) -> &'static OpcodeInfo {
        let opcode: u8 = Opcode::Add as u8;
        return match self {
            Opcode::Add => &ADD_INFO,
            Opcode::Mul => &MUL_INFO,
            Opcode::Sub => &SUB_INFO,
            Opcode::Div => &DIV_INFO,
            Opcode::Rem => &REM_INFO,
            Opcode::Ilt => &ILT_INFO,
            Opcode::Ile => &ILE_INFO,
            Opcode::Igt => &IGT_INFO,
            Opcode::Ige => &IGE_INFO,
            Opcode::Ieq => &IEQ_INFO,
            Opcode::Noop => &NOOP_INFO,
            Opcode::Neg => &NEG_INFO,
            Opcode::Inv => &INV_INFO,
            Opcode::Goto => &GOTO_INFO,
            Opcode::IfTrue => &IF_TRUE_INFO,
            Opcode::IfFalse => &IF_FALSE_INFO,
            Opcode::IfNull => &IF_NULL_INFO,
            Opcode::IfNotNull => &IF_NOT_NULL_INFO,
            Opcode::IloadConst => &ILOAD_CONST_INFO,
            Opcode::CloadConst => &CLOAD_CONST_INFO,
            Opcode::SloadConst => &SLOAD_CONST_INFO,
            Opcode::LoadTrue => &LOAD_TRUE_INFO,
            Opcode::LoadFalse => &LOAD_FALSE_INFO,
            Opcode::LoadOne => &LOAD_ONE_INFO,
            Opcode::LoadZero => &LOAD_ZERO_INFO,
            Opcode::LoadNull => &LOAD_NULL_INFO,
            Opcode::Pop => &POP_INFO,
            Opcode::I2c => &I2C_INFO,
            Opcode::C2i => &C2I_INFO,
            Opcode::IstoreSlot => &ISTORE_SLOT_INFO,
            Opcode::CstoreSlot => &CSTORE_SLOT_INFO,
            Opcode::BstoreSlot => &BSTORE_SLOT_INFO,
            Opcode::RstoreSlot => &RSTORE_SLOT_INFO,
            Opcode::IloadSlot => &ILOAD_SLOT_INFO,
            Opcode::CloadSlot => &CLOAD_SLOT_INFO,
            Opcode::BloadSlot => &BLOAD_SLOT_INFO,
            Opcode::RloadSlot => &RLOAD_SLOT_INFO,
            Opcode::IstoreField => &ISTORE_FIELD_INFO,
            Opcode::CstoreField => &CSTORE_FIELD_INFO,
            Opcode::BstoreField => &BSTORE_FIELD_INFO,
            Opcode::RstoreField => &RSTORE_FIELD_INFO,
            Opcode::IloadField => &ILOAD_FIELD_INFO,
            Opcode::CloadField => &CLOAD_FIELD_INFO,
            Opcode::BloadField => &BLOAD_FIELD_INFO,
            Opcode::RloadField => &RLOAD_FIELD_INFO,
            Opcode::Typecheck => &TYPECHECK_INFO,
            Opcode::CallVirtual => &CALL_VIRTUAL_INFO,
            Opcode::CallConstructor => &CALL_CONSTRUCTOR_INFO,
            Opcode::CallStatic => &CALL_STATIC_INFO,
            Opcode::Return => &RETURN_INFO,
        };
    }

    pub fn iterator() -> Iter<'static, Opcode> {
        static OPCODES: [Opcode; 50] = [
            Opcode::Add,
            Opcode::Mul,
            Opcode::Sub,
            Opcode::Div,
            Opcode::Rem,
            Opcode::Ilt,
            Opcode::Ile,
            Opcode::Igt,
            Opcode::Ige,
            Opcode::Ieq,
            Opcode::Noop,
            Opcode::Neg,
            Opcode::Inv,
            Opcode::Goto,
            Opcode::IfTrue,
            Opcode::IfFalse,
            Opcode::IfNull,
            Opcode::IfNotNull,
            Opcode::IloadConst,
            Opcode::CloadConst,
            Opcode::SloadConst,
            Opcode::LoadTrue,
            Opcode::LoadFalse,
            Opcode::LoadOne,
            Opcode::LoadZero,
            Opcode::LoadNull,
            Opcode::Pop,
            Opcode::I2c,
            Opcode::C2i,
            Opcode::IstoreSlot,
            Opcode::CstoreSlot,
            Opcode::BstoreSlot,
            Opcode::RstoreSlot,
            Opcode::IloadSlot,
            Opcode::CloadSlot,
            Opcode::BloadSlot,
            Opcode::RloadSlot,
            Opcode::IstoreField,
            Opcode::CstoreField,
            Opcode::BstoreField,
            Opcode::RstoreField,
            Opcode::IloadField,
            Opcode::CloadField,
            Opcode::BloadField,
            Opcode::RloadField,
            Opcode::Typecheck,
            Opcode::CallVirtual,
            Opcode::CallConstructor,
            Opcode::CallStatic,
            Opcode::Return
        ];
        OPCODES.iter()
    }
}