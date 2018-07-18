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
use resolve::ClassIndex;
use class_file::constant_pool::ConstantPool;

pub struct Task {
    files: Vec<ClassFile>,
    main_class_file_index: u32,
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
    frame_stack: Vec<StackFrame>,
    operand_stack: Vec<Value>,
    class_index: ClassIndex,
}

struct CallEnvironment<'a, 'b> {
    class_index: &'a ClassIndex,
    constant_pool: &'b ConstantPool,
}

impl Interpreter {
    pub fn run(&mut self, task: &Task) -> Result<(), InterpeterError> {
        let main_class = task.find_main_class();
        match main_class.find_main_method() {
            None => Err(InterpeterError::MainMethodNotFound),
            Some(method) => {}
        }
    }

    fn drop_frame(&mut self) {
        let mut stack = self.frame_stack;
        match stack.pop() {
            None => panic!("Underflow"),
            Some(_) => {}
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
    fn call_static(&self) {}

    pub fn new(files: Vec<ClassFile>) -> Interpreter {
        return Interpreter {
            frame_stack: Vec::new(),
            operand_stack: Vec::new(),
            class_index: ClassIndex::new(),
        };
    }
}

struct InterpretedCall<'a> {
    ip: u16,
    method: &'a MethodInfo,
}

impl<'a> InterpretedCall<'a> {
    fn interpret_command(&mut self, env: &CallEnvironment) {}

    fn fetch_instruction(&self) -> Instruction {
        let instruction = self.method.bytecode.unwrap()[self.ip as usize];
        instruction
        // TODO probably more safe handling of trying to call native
    }

    fn binary_i32_instr(&self, operand_stack: &mut Vec<Value>, func: fn(i32, i32) -> i32) {
        let left = operand_stack.pop().unwrap().try_as_i32().unwrap();
        let right = operand_stack.pop().unwrap().try_as_i32().unwrap();
        operand_stack.push(Value::I32 { value: func(left, right) })
    }

    fn binary_bool_instr(&self, operand_stack: &mut Vec<Value>, func: fn(i32, i32) -> bool) {
        let left = operand_stack.pop().unwrap().try_as_i32().unwrap();
        let right = operand_stack.pop().unwrap().try_as_i32().unwrap();
        operand_stack.push(Value::Bool { value: func(left, right) })
    }
}


impl<'a> Callable for InterpretedCall<'a> {
    // TODO it is more correct to return here Result with error and not to panic everywhere
    fn call(&mut self, env: CallEnvironment, operand_stack: &mut Vec<Value>) {
        loop {
            let instruction = self.fetch_instruction();
            // TODO print stack condition and stack trace
            match instruction.opcode() {
                Opcode::Add => {
                    self.binary_i32_instr(operand_stack, |left, right| left + right)
                }
                Opcode::Mul => {
                    self.binary_i32_instr(operand_stack, |left, right| left * right)
                }
                Opcode::Sub => {
                    self.binary_i32_instr(operand_stack, |left, right| left - right)
                }
                Opcode::Div => {
                    self.binary_i32_instr(operand_stack, |left, right| left / right)
                }
                Opcode::Rem => {
                    self.binary_i32_instr(operand_stack, |left, right| left % right)
                }

                Opcode::Ilt => {
                    self.binary_bool_instr(operand_stack, |left, right| left < right)
                }
                Opcode::Ile => {
                    self.binary_bool_instr(operand_stack, |left, right| left <= right)
                }
                Opcode::Igt => {
                    self.binary_bool_instr(operand_stack, |left, right| left > right)
                }
                Opcode::Ige => {
                    self.binary_bool_instr(operand_stack, |left, right| left >= right)
                }
                Opcode::Ieq => {
                    self.binary_bool_instr(operand_stack, |left, right| left == right)
                }

                Opcode::Noop => {}

                Opcode::Neg => {
                    let value = operand_stack.pop().unwrap().try_as_i32().unwrap();
                    operand_stack.push(Value::I32 { value: -value })
                }
                Opcode::Inv => {
                    let value = operand_stack.pop().unwrap().try_as_bool().unwrap();
                    operand_stack.push(Value::Bool { value: !value })
                }
                Opcode::Goto => {
                    let label = instruction.try_get_operand().unwrap();
                    self.ip = label;
                    continue;
                }
                Opcode::IfTrue => {
                    let label = instruction.try_get_operand().unwrap();
                    let value = operand_stack.pop().unwrap().try_as_bool().unwrap();
                    if value {
                        self.ip = label;
                        continue;
                    }
                }
                Opcode::IfFalse => {
                    let label = instruction.try_get_operand().unwrap();
                    let value = operand_stack.pop().unwrap().try_as_bool().unwrap();
                    if !value {
                        self.ip = label;
                        continue;
                    }
                }
                Opcode::IfNull => {
                    let label = instruction.try_get_operand().unwrap();
                    let value = operand_stack.pop().unwrap().try_as_obj().unwrap();
                    unimplemented!()
//                    if value {
//                        self.ip = label;
//                        continue;
//                    }
                }
                Opcode::IfNotNull => {
                    unimplemented!()
                }
                Opcode::IloadConst => {
                    let cp_descriptor = instruction.try_get_operand().unwrap();
                    let cp_entry = env.constant_pool.resolve_to_int(cp_descriptor).unwrap().num;
                    operand_stack.push(Value::I32 { value: cp_entry })
                }
                Opcode::CloadConst => {
                    let cp_descriptor = instruction.try_get_operand().unwrap();
                    let cp_entry = env.constant_pool.resolve_to_int(cp_descriptor).unwrap().num;
                    operand_stack.push(Value::I8 { value: cp_entry as i8 })
                }
                Opcode::SloadConst => {
                    let cp_descriptor = instruction.try_get_operand().unwrap();
                    let cp_entry = env.constant_pool.resolve_to_string(cp_descriptor).unwrap().str;
                    // TODO allocate on stack String instance
                    operand_stack.push(Value::Obj { value: })
                }
                Opcode::LoadTrue => {
                    operand_stack.push(Value::Bool { value: true })
                }
                Opcode::LoadFalse => {
                    operand_stack.push(Value::Bool { value: true })
                }
                Opcode::LoadOne => {
                    operand_stack.push(Value::I32 { value: 1i32 })
                }
                Opcode::LoadZero => {
                    operand_stack.push(Value::I32 { value: 1i32 })
                }
                Opcode::LoadNull => {
                    unimplemented!()
                }
                Opcode::Pop => {
                    operand_stack.pop()
                }
                Opcode::I2c => {
                    let value = operand_stack.pop().unwrap().try_as_i32().unwrap();
                    operand_stack.push(Value::I8 { value: value as i8 })
                    // TODO what happens if overflow? truncation?
                }
                Opcode::C2i => {
                    let value = operand_stack.pop().unwrap().try_as_i8().unwrap();
                    operand_stack.push(Value::I32 { value: value as i32 })
                }
                Opcode::IstoreSlot => {}
                Opcode::CstoreSlot => {}
                Opcode::BstoreSlot => {}
                Opcode::RstoreSlot => {}
                Opcode::IloadSlot => {}
                Opcode::CloadSlot => {}
                Opcode::BloadSlot => {}
                Opcode::RloadSlot => {}
                Opcode::IstoreField => {}
                Opcode::CstoreField => {}
                Opcode::BstoreField => {}
                Opcode::RstoreField => {}
                Opcode::IloadField => {}
                Opcode::CloadField => {}
                Opcode::BloadField => {}
                Opcode::RloadField => {}
                Opcode::Typecheck => {}
                Opcode::CallVirtual => {}
                Opcode::CallConstructor => {}
                Opcode::CallStatic => {}
                Opcode::Return => return,
            }
        }
    }
}

struct NativeCall {}

// Required to implement native calls
trait Callable {
    fn call(&mut self, env: CallEnvironment, operand_stack: &mut Vec<Value>);
}

struct StackFrame {
    slots: Vec<Value>,
    callee_descr: u16,
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
            }
        }
    }
}