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

pub struct ClassFile {
    pool: ConstantPool
}


pub struct ConstantPool {
    pub entries: Vec<CPEntry>
}

pub struct StringCPEntry {}

pub struct IntCPEntry {
    pub num: i32
}

pub struct MethodCPEntry {}

pub struct VarCPEntry {}

pub struct ClassCPEntry {}


pub struct Bytecode {
    bytecode: Vec<Instruction>
}

#[derive(Copy, Clone)]
enum Instruction {
    SingleByte {
        opcode: Opcode
    },
    WithInlineOperand {
        opcode: Opcode,
        inline_operand: u16,
    },
}


pub enum CPEntry {
    Num { num: IntCPEntry },
    Str { str: StringCPEntry },
    Method { method: MethodCPEntry },
    Var { var: VarCPEntry },
    Class { method: ClassCPEntry },
}

#[derive(Debug)]
pub enum ParseError {
    MagicDiffers { value: u32 },
    UnknownBytecodeVersion { version: u16 },
    UnknownLabel { label: u8 },
    IoError { error: Box<Error> },
}

impl Error for ParseError {
    fn description(&self) -> &str {
        match *self {
           ParseError::MagicDiffers { value } => "Magic differs",
           ParseError::UnknownBytecodeVersion { version } => "Unknown bytecode version",
           ParseError::UnknownLabel { label } => "Unknown label",
           ParseError::IoError { ref error } => "IO error occurred",
       }
    }
}

impl Display for ParseError {
    fn fmt(&self, f: &mut Formatter) -> Result<(), fmt::Error> {
        match *self {
            ParseError::MagicDiffers { value } => {
                f.write_fmt(format_args!("Magic differs: {} found, {} expected", value, FILE_MAGIC))
            },
            ParseError::UnknownBytecodeVersion { version } => {
                f.write_fmt(format_args!("Bytecode version differs: {} found, {} expected", version, BYTECODE_VERSION))
            },
            ParseError::UnknownLabel { label } => {
                f.write_fmt(format_args!("Unknown label: {}", label))
            },
            ParseError::IoError { ref error } => {
                f.write_str("IO error occurred")
            },
        }
    }
}


static FILE_MAGIC : u32 = 0x0B1B2B3B;
static BYTECODE_VERSION: u16 = 0;

macro_rules! parse_descr {
    ($x:expr) => {
        match $x.read_u16::<BigEndian>() {
            Ok(version) => {
                version
            },
            Err(err) => {
                return Err(ParseError::IoError { error: Box::new(err) } )
            },
        }
    };
}

macro_rules! parse_u32 {
    ($x:expr) => {
        match $x.read_u32::<BigEndian>() {
            Ok(version) => {
                version
            },
            Err(err) => {
                return Err(ParseError::IoError { error: Box::new(err) } )
            },
        }
    };
}

impl ClassFile {
    pub fn parse<'e, R: ReadBytesExt + Sized>(mut read: R) -> Result<ClassFile, ParseError> {
        let mut buffer: Vec<u8> = Vec::new();
        match read.read_u32::<BigEndian>() {
            Ok(value) => {
                if value != FILE_MAGIC {
                    return Err(ParseError::MagicDiffers { value })
                }
            },
            Err(err) => {
                return Err(ParseError::IoError { error: Box::new(err) } )
            },
        }
        match read.read_u16::<BigEndian>() {
            Ok(version) => {
                if version != BYTECODE_VERSION {
                    return Err(ParseError::UnknownBytecodeVersion { version })
                }
            },
            Err(err) => {
                return Err(ParseError::IoError { error: Box::new(err) } )
            },
        }
        let class_descr = parse_descr!(read);
        let parent_class_descr = parse_descr!(read);
        let fields_count = parse_u32!(read);
        unimplemented!();

//        let mut buffer = [0u8; 65536];
//        buffer
//        loop {
//             probably not read_exact should be used here
//            match read.read() {
//                Ok(size) => {
//
//                }
//                Err => return Err(ParseError::IoError)
//            }
//        }
//        Opcode::BloadField.
//        unimplemented!()
//        Ok(ClassFile { pool: ConstantPool { entries: Vec::new() } }) // TODO
    }
}