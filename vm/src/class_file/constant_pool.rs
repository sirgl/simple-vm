use class_file::ParseError;
use byteorder::ReadBytesExt;
use byteorder::*;

#[macro_use]
use super::parse;


#[derive(Debug)]
enum CpLabel {
    Str = 0,
    Int,
    Method,
    Var,
    ClassRef,
}

impl CpLabel {
    pub fn parse(label: u8) -> Option<CpLabel> {
        match label {
            0 => Some(CpLabel::Str),
            1 => Some(CpLabel::Int),
            2 => Some(CpLabel::Method),
            3 => Some(CpLabel::Var),
            4 => Some(CpLabel::ClassRef),
            _ => None
        }
    }
}

#[derive(Debug)]
pub enum CPEntry {
    Num { num: IntCPEntry },
    Str { str: StringCPEntry },
    Method { method: MethodCPEntry },
    Var { var: VarCPEntry },
    Class { class: ClassCPEntry },
}

impl CPEntry {
    fn parse<'e, R: ReadBytesExt + Sized>(read: &mut  R) -> Result<CPEntry, ParseError> {
        let label = parse_u8!(read);
        let option = CpLabel::parse(label);
        if option.is_none() {
            return Err(ParseError::UnknownLabel { label })
        }
        let x = option.unwrap();
        eprintln!("option.unwrap() = {:?}", x);
        return match x {
            CpLabel::Str => Ok(CPEntry::Str { str: StringCPEntry::parse(read)? }),
            CpLabel::Int => Ok(CPEntry::Num { num: IntCPEntry::parse(read)? }),
            CpLabel::Method => Ok(CPEntry::Method { method: MethodCPEntry::parse(read)? }),
            CpLabel::Var => Ok(CPEntry::Var { var: VarCPEntry::parse(read)? }),
            CpLabel::ClassRef => Ok(CPEntry::Class { class: ClassCPEntry::parse(read)? }),
            _ => unimplemented!()
        }
//        return Err(ParseError::MagicDiffers { value: 12 })
    }
}

pub struct ConstantPool {
    pub entries: Vec<CPEntry>
}

impl ConstantPool {
    pub fn parse<'e, R: ReadBytesExt + Sized>(read: &mut  R) -> Result<ConstantPool, ParseError> {
        let size = parse_u32!(read);
        let mut entries = Vec::<CPEntry>::with_capacity(size as usize);
        for i in 0..size {
            let x = CPEntry::parse(read)?;
            eprintln!("x = {:?}", x);
            entries.push(x);
        }
        Ok(ConstantPool { entries })
    }
}

trait ParseableEntry : Sized {
    fn parse<'e, R: ReadBytesExt + Sized>(read: &mut  R) -> Result<Self, ParseError>;
}

#[derive(Debug)]
pub struct StringCPEntry {
    str: String
}

impl ParseableEntry for StringCPEntry {
    fn parse<'e, R: ReadBytesExt + Sized>(read: &mut R) -> Result<Self, ParseError> {
        let size = parse_u32!(read) as usize;
        let mut buf: Vec<u8> = Vec::with_capacity(size);
        buf.resize(size, 0);
        match read.read_exact(buf.as_mut_slice()) {
            Err(err) => return Err(ParseError::IoError { error: Box::new(err) }),
            _ => {}
        }
        match String::from_utf8(buf) {
            Ok(string) => Ok(StringCPEntry { str: string }),
            Err(err) => Err(ParseError::IoError { error: Box::new(err) }),
        }
    }
}

#[derive(Debug)]
pub struct IntCPEntry {
    pub num: i32
}

impl ParseableEntry for IntCPEntry {
    fn parse<'e, R: ReadBytesExt + Sized>(read: &mut R) -> Result<Self, ParseError> {
        match read.read_i32::<BigEndian>() {
            Ok(res) => Ok(IntCPEntry { num: res }),
            Err(err) => Err(ParseError::IoError { error: Box::new(err) }),
        }
    }
}

#[derive(Debug)]
pub struct MethodCPEntry {
    class_descriptor: u16,
    name_descriptor: u16,
    return_type_signature_descriptor: u16,
    parameter_var_descriptors: Vec<u16>
}

impl ParseableEntry for MethodCPEntry {
    fn parse<'e, R: ReadBytesExt + Sized>(read: &mut R) -> Result<Self, ParseError> {
        let class_descriptor = parse_u16!(read);
        let name_descriptor = parse_u16!(read);
        let return_type_signature_descriptor = parse_u16!(read);
        let size = parse_u32!(read) as usize;
        let mut parameter_var_descriptors = Vec::<u16>::with_capacity(size);
        for i in 0..size {
            parameter_var_descriptors.push(parse_u16!(read));
        }
        Ok(MethodCPEntry {
            class_descriptor,
            name_descriptor,
            return_type_signature_descriptor,
            parameter_var_descriptors,
        })
    }
}

#[derive(Debug)]
pub struct VarCPEntry {
    type_signature_descriptor: u16,
    name_descriptor: u16
}

impl ParseableEntry for VarCPEntry {
    fn parse<'e, R: ReadBytesExt + Sized>(read: &mut R) -> Result<Self, ParseError> {
        let type_signature_descriptor = parse_u16!(read);
        let name_descriptor = parse_u16!(read);
        Ok(VarCPEntry { type_signature_descriptor, name_descriptor })
    }
}

#[derive(Debug)]
pub struct ClassCPEntry {
    simple_name_descriptor: u16,
    package_descriptor: u16
}

impl ParseableEntry for ClassCPEntry {
    fn parse<'e, R: ReadBytesExt + Sized>(read: &mut R) -> Result<Self, ParseError> {
        let simple_name_descriptor = parse_u16!(read);
        let package_descriptor = parse_u16!(read);
        Ok(ClassCPEntry { simple_name_descriptor, package_descriptor })
    }
}