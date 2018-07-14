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


#[macro_use]
mod parse;
mod constant_pool;

pub struct ClassFile {
    pool: ConstantPool
}


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
            }
            ParseError::UnknownBytecodeVersion { version } => {
                f.write_fmt(format_args!("Bytecode version differs: {} found, {} expected", version, BYTECODE_VERSION))
            }
            ParseError::UnknownLabel { label } => {
                f.write_fmt(format_args!("Unknown label: {}", label))
            }
            ParseError::IoError { ref error } => {
                f.write_str("IO error occurred")
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
        let class_descr = parse_u16!(read);
        let parent_class_descr = parse_u16!(read);
        let fields_count = parse_u32!(read);
        let mut fields = Vec::<u16>::new();
        for i in 0..fields_count {
            fields.push(parse_u16!(read))
        }
        let constant_pool = ConstantPool::parse(&mut read)?;
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