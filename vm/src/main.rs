use class_file::*;
use std::path::Path;
use std::fs::File;
use std::io::BufReader;
use interpreter::Interpreter;
use class_file::ClassFile;

mod class_file;
mod opcodes;
mod interpreter;
mod memory;
mod resolve;
mod types;

extern crate byteorder;

fn main() {
//    let x: ConstantPool = ConstantPool { entries: Vec::new() };
////    x.entries.
//    let mut entries = x.entries;
//    let entry = IntCPEntry { num: 0 };
//    entries.push(CPEntry::Num { num: entry } );
////    let path = Path::new("hello.txt");
////    let file = File::open(path).unwrap();
//    let interpreter = Interpreter::new();
//
    let file = File::open("/Users/jetbrains/IdeaProjects/simple-vm/out.cls").unwrap();
    let reader = BufReader::new(file);
    let result = ClassFile::parse(reader);

    match result {
        Ok(cls) => {
            println!("ok");
        },
        Err(err) => {
            print!("Parse error: {}", err);
        },
    }

}
