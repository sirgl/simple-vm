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