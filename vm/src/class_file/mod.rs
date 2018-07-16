use std::io::Read;
use std::io::BufReader;
use std::io::BufRead;
use std::error::Error;
use std::any::TypeId;
use byteorder::*;

use opcodes::*;
use std::fmt;
use std::fmt::Display;
use std::fmt::Formatter;
use class_file::constant_pool::ConstantPool;
use class_file::constant_pool::CPEntry;


#[macro_use]
mod parse;
mod constant_pool;

pub struct ClassFile {
    pub pool: ConstantPool,
    pub methods: Vec<MethodInfo>,
    pub field_descriptors: Vec<u16>,
    pub class_descriptor: u16,
    pub parent_class_descriptor: u16,
}

impl ClassFile {
    // TODO check method is not native and
    pub fn find_main_method(&self) -> Option<&MethodInfo> {
        self.methods.iter().find(|method_info| {
            if method_info.bytecode.is_none() {
                return false
            }
            let descriptor = method_info.method_descriptor;
            match self.pool.resolve(descriptor) {
                None => false,
                Some(cp_entry) => match cp_entry {
                    CPEntry::Method { method } => {
                        match method.resolve_name(&self.pool) {
                            None => false,
                            Some(name) => name == "main",
                        }
                    },
                    _ =>  false
                },
            }
//            x.method_descriptor
        })
    }
}


#[derive(Copy, Clone, Debug)]
pub enum Instruction {
    SingleByte {
        opcode: Opcode
    },
    WithInlineOperand {
        opcode: Opcode,
        inline_operand: u16,
    },
}

impl Instruction {
    pub fn opcode(self) -> Opcode {
        match self {
            Instruction::SingleByte { opcode } => opcode,
            Instruction::WithInlineOperand { opcode, inline_operand } => opcode,
        }
    }
}

#[derive(Debug)]
pub struct MethodInfo {
    pub bytecode: Option<Vec<Instruction>>,
    // may be none when method is native
    method_descriptor: u16,
}

impl MethodInfo {
    pub fn parse<'e, R: ReadBytesExt + Sized>(read: & mut R) -> Result<MethodInfo, ParseError> {
        let method_descriptor = parse_u16!(read);
        let mut i = 0;
        let modifier_list_byte = parse_u8!(read); // has bytecode or no
        if modifier_list_byte == 0 {
            return Ok(MethodInfo { bytecode: None, method_descriptor })
        } else if modifier_list_byte != 1 {
            return Err(ParseError::UnknownModifierList { modifier_list_byte })
        }
        let bytecode_size = parse_u32!(read) as usize;
        let mut bytecode = Vec::<Instruction>::with_capacity(bytecode_size);
        while i < bytecode_size {
            let byte = parse_u8!(read);
            match Opcode::parse(byte) {
                None => return Err(ParseError::UnknownBytecode {bytecode : byte}),
                Some(opcode) => {
                    if opcode.has_inline_operand() {
                        if i + 2 >= bytecode_size {
                            return Err(ParseError::MissedInlineOperand)
                        }
                        let inline_operand = parse_u16!(read);
                        bytecode.push(Instruction::WithInlineOperand {
                            opcode,
                            inline_operand
                        });
                        i += 2;
                    } else {
                        bytecode.push(Instruction::SingleByte { opcode });
                    }
                },
            }
            i += 1;
        }
        return Ok(MethodInfo { bytecode: Some(bytecode), method_descriptor })
    }
}


#[derive(Debug)]
pub enum ParseError {
    MagicDiffers { value: u32 },
    UnknownBytecodeVersion { version: u16 },
    UnknownLabel { label: u8 },
    IoError { error: Box<Error> },
    UnknownBytecode { bytecode: u8 },
    MissedInlineOperand,
    UnknownModifierList { modifier_list_byte: u8 },
}

impl Error for ParseError {
    fn description(&self) -> &str {
        match *self {
            ParseError::MagicDiffers { value } => "Magic differs",
            ParseError::UnknownBytecodeVersion { version } => "Unknown bytecode version",
            ParseError::UnknownLabel { label } => "Unknown label",
            ParseError::IoError { ref error } => "IO error occurred",
            ParseError::UnknownBytecode { bytecode } => "Unknown bytecode",
            ParseError::MissedInlineOperand => "Missed inline operand",
            ParseError::UnknownModifierList { modifier_list_byte } => "Unknown modifier list",
        }
    }
}

impl Display for ParseError {
    fn fmt(&self, f: &mut Formatter) -> Result<(), fmt::Error> {
        match *self {
            ParseError::MagicDiffers { value } => {
                f.write_fmt(format_args!("Magic differs: {} found, {} expected", value, FILE_MAGIC))
            }
            ParseError::UnknownBytecodeVersion { version } => {
                f.write_fmt(format_args!("Bytecode version differs: {} found, {} expected", version, BYTECODE_VERSION))
            }
            ParseError::UnknownLabel { label } => {
                f.write_fmt(format_args!("Unknown label: {}", label))
            }
            ParseError::IoError { ref error } => {
                f.write_fmt(format_args!("IO error occurred: {}", error))
            }
            ParseError::UnknownBytecode { bytecode } => {
                f.write_fmt(format_args!("Unknown bytecode: {}", bytecode))
            }
            ParseError::MissedInlineOperand => {
                f.write_str("Missed inline operand")
            }
            ParseError::UnknownModifierList { modifier_list_byte } => {
                f.write_fmt(format_args!("Unknown modifier list byte: {}", modifier_list_byte))
            }
        }
    }
}


static FILE_MAGIC: u32 = 0x0B1B2B3B;
static BYTECODE_VERSION: u16 = 0;


impl ClassFile {
    pub fn parse<'e, R: ReadBytesExt + Sized>(mut read: R) -> Result<ClassFile, ParseError> {
        let mut buffer: Vec<u8> = Vec::new();
        let magic = parse_u32!(read);
        if magic != FILE_MAGIC {
            return Err(ParseError::MagicDiffers { value: magic });
        }
        let version = parse_u16!(read);
        if version != BYTECODE_VERSION {
            return Err(ParseError::UnknownBytecodeVersion { version });
        }
        let class_descriptor = parse_u16!(read);
        let parent_class_descriptor = parse_u16!(read);
        let fields_count = parse_u32!(read);
        let mut fields = Vec::<u16>::new();
        for i in 0..fields_count {
            fields.push(parse_u16!(read))
        }
        let constant_pool = ConstantPool::parse(&mut read)?;


        let method_count = parse_u32!(read) as usize;
        let mut methods = Vec::<MethodInfo>::with_capacity(method_count);
        for i in 0..method_count {
            let method_info = MethodInfo::parse(&mut read);
            methods.push(method_info?);
        }
        Ok(ClassFile { pool: constant_pool, methods, field_descriptors: fields, class_descriptor, parent_class_descriptor })
    }
}