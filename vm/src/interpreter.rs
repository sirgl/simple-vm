use std::error::Error;
use class_file::ClassFile;
use std::fmt::Display;
use std::fmt::Formatter;
use std::fmt;
use class_file::MethodInfo;
use class_file::Instruction;
use opcodes::Opcode;
use memory::Value;
use memory::Class;

pub struct Task {
    files: Vec<ClassFile>,
    main_class_file_index: u32
}

impl Task {
    fn find_main_class(&self) -> &ClassFile {
        &self.files[self.main_class_file_index as usize]
    }

    fn new(files: Vec<ClassFile>, main_class_file_index: u32) -> Task {
        if main_class_file_index as usize > files.len() {
            panic!("Main class index must be inside of the range");
        }
        Task { files, main_class_file_index }
    }
}


pub struct Interpreter<'a> {
//    ip: u32,
    frame_stack: Vec<StackFrame>,
    operand_stack: Vec<Value>,
    method: Option<&'a MethodInfo>,
    files: Vec<ClassFile>,
    classes: Vec<Class>
}

impl Interpreter {
    pub fn run(&mut self, task: &Task) -> Result<(), InterpeterError> {
        let main_class = task.find_main_class();
        match main_class.find_main_method() {
            None => Err(InterpeterError::MainMethodNotFound),
            Some(method) => {
                self.interpret()
            },
        }
    }

    // Rework
    fn interpret(&mut self) {
        loop {
            let instruction = self.fetch();
            let opcode = instruction.opcode();
            match opcode {
//                Opcode::Add => {},
//                Opcode::Mul => {},
//                Opcode::Sub => {},
//                Opcode::Div => {},
//                Opcode::Rem => {},
//                Opcode::Ilt => {},
//                Opcode::Ile => {},
//                Opcode::Igt => {},
//                Opcode::Ige => {},
//                Opcode::Ieq => {},
//                Opcode::Noop => {},
//                Opcode::Neg => {},
//                Opcode::Inv => {},
//                Opcode::Goto => {},
//                Opcode::IfTrue => {},
//                Opcode::IfFalse => {},
//                Opcode::IfNull => {},
//                Opcode::IfNotNull => {},
//                Opcode::IloadConst => {},
//                Opcode::CloadConst => {},
//                Opcode::SloadConst => {},
//                Opcode::LoadTrue => {},
//                Opcode::LoadFalse => {},
//                Opcode::LoadOne => {},
//                Opcode::LoadZero => {},
//                Opcode::LoadNull => {},
//                Opcode::Pop => {},
//                Opcode::I2c => {},
//                Opcode::C2i => {},
//                Opcode::IstoreSlot => {},
//                Opcode::CstoreSlot => {},
//                Opcode::BstoreSlot => {},
//                Opcode::RstoreSlot => {},
//                Opcode::IloadSlot => {},
//                Opcode::CloadSlot => {},
//                Opcode::BloadSlot => {},
//                Opcode::RloadSlot => {},
//                Opcode::IstoreField => {},
//                Opcode::CstoreField => {},
//                Opcode::BstoreField => {},
//                Opcode::RstoreField => {},
//                Opcode::IloadField => {},
//                Opcode::CloadField => {},
//                Opcode::BloadField => {},
//                Opcode::RloadField => {},
//                Opcode::Typecheck => {},
//                Opcode::CallVirtual => {},
//                Opcode::CallConstructor => {},
//                Opcode::CallStatic => {},
                Opcode::Return => {
                },
            }
        }
    }

    fn drop_frame(&mut self) {
        let mut stack = self.frame_stack;
        match stack.pop() {
            None => panic!("Underflow"),
            Some(_) => {},
        }
//        self.method = Some(&stack.last())
    }

    fn stack_trace(&self) -> &str {
        unimplemented!()
    }


    fn fetch(&self) -> Instruction {
        let bytecode = self.method.unwrap().bytecode;
        bytecode[ip]
    }

    // creates stack frame with: this (if present), parameters, local variables
    // pass control to this function
    fn call_static(&self, ) {

    }

    pub fn new(files: Vec<ClassFile>) -> Interpreter {
//        files.iter()
//            .map(|file| file.)
        return Interpreter { frame_stack: Vec::new(), operand_stack: Vec::new(), method: None, files, classes: Vec::new() }
    }
}

// Required to implement native calls
trait Callable {
    fn call(&self, )
}

struct StackFrame {
    slots: Vec<Value>,
    callee_descr: u16
}


#[derive(Debug)]
enum InterpeterError {
    MainMethodNotFound
}

impl Error for InterpeterError {
    fn description(&self) -> &str {
        match *self {
            InterpeterError::MainMethodNotFound => "Main method not found",
        }
    }
}

impl Display for InterpeterError {
    fn fmt(&self, f: &mut Formatter) -> Result<(), fmt::Error> {
        match *self {
            InterpeterError::MainMethodNotFound => {
                f.write_fmt(format_args!("Main method is not found"))
            },
        }
    }
}