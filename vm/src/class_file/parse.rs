use byteorder::*;

#[macro_export]
macro_rules! parse_u16 {
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

#[macro_export]
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

#[macro_export]
macro_rules! parse_u8 {
    ($x:expr) => {
        match $x.read_u8() {
            Ok(version) => {
                version
            },
            Err(err) => {
                return Err(ParseError::IoError { error: Box::new(err) } )
            },
        }
    };
}
