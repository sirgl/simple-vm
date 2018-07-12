pub struct Interpreter {
    ip: u32,

}

impl Interpreter {
    pub fn run(&self) -> Result<(), InterpeterError> {
        return Ok(());
    }

    pub fn new() -> Interpreter {
        return Interpreter { ip: 0 }
    }
}


struct StackFrame {
    variables: Vec<SlotVariable>
}

// TODO or make it enum???
struct SlotVariable {

}



enum InterpeterError {

}