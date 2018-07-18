use std::rc::Rc;
use class_file::MethodInfo;
use class_file::ClassFile;
use std::collections::HashMap;

#[derive(Debug)]
pub enum Value {
    I8 { value: i8 },
    I32 { value: i32 },
    Obj { value: Box<Object> },
    Bool { value: bool },
}

impl Value {
    pub fn try_as_i8(&self) -> Option<i8> {
        match *self {
            Value::I8 { value } => Some(value),
            _ => None,
        }
    }

    pub fn try_as_i32(&self) -> Option<i32> {
        match *self {
            Value::I32 { value } => Some(value),
            _ => None,
        }
    }

    pub fn try_as_obj(&self) -> Option<Box<Object>> {
        match *self {
            Value::Obj { value } => Some(value),
            _ => None,
        }
    }

    pub fn try_as_bool(&self) -> Option<bool> {
        match *self {
            Value::Bool { value } => Some(value),
            _ => None,
        }
    }
}

#[derive(Debug)]
pub enum VarType {
    I8,
    I32,
    Obj { cls: Rc<Object> }, // is it right?
    Bool,
    Void
}

#[derive(Debug)]
pub struct Object {
    class: Rc<Class>, // probably here should be more unsafe and performant solution
    fields: Vec<Value> // field descriptor to its value
}

#[derive(Debug)]
pub struct Class {
    parent_class: Rc<Class>,
    class_file: Rc<ClassFile>,
    vtable: Vec<MethodInfo>,
    fields_descr_to_index: HashMap<u16, u16>,
}

impl Class {
    pub fn from_file(file: &ClassFile) -> Class {
        Class {
            parent_class: (),
            class_file: (),
            vtable: Vec::new(),
            fields_descr_to_index: (),
        }
    }
}