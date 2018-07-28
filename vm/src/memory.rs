use std::rc::Rc;
use class_file::MethodInfo;
use class_file::ClassFile;
use types::Type;
use std::collections::HashMap;
use std::collections::linked_list::LinkedList;

#[derive(Debug)]
pub enum Value {
    I8 { value: i8 },
    I32 { value: i32 },
    Obj { value: Option<Box<Object>> },
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

    pub fn try_as_obj(&self) -> Option<Option<Box<Object>>> {
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
    Obj { cls: Rc<Object> },
    // is it right?
    Bool,
    Void,
}

#[derive(Debug)]
pub struct Object {
    pub class: Rc<Class>,
    // probably here should be more unsafe and performant solution
    pub fields: Vec<Value>, // field descriptor to its value
}

impl Object {
    fn from_class(class: Rc<Class>) -> Object {
        let class_file = class.class_file;
        let field_descriptors = class_file.field_descriptors;
        let mut fields = Vec::<Value>::with_capacity(field_descriptors.len());
        let pool = class_file.pool;
        for field_descriptor in field_descriptors {
            let field_entry = pool.resolve_to_var(field_descriptor).unwrap();
            let type_signature_descriptor = field_entry.type_signature_descriptor;
            let signature_string = pool.resolve_to_string(type_signature_descriptor).unwrap();
            let tp = Type::parse(signature_string.str.as_str());
            fields.push(match tp {
                Type::Bool => Value::Bool { value: false },
                Type::I8 => Value::I8 { value: 0 },
                Type::I32 => Value::I32 { value: 0 },
                Type::Array { base_type } => Value::Obj { value: None },
                Type::Object {  package, name } => Value::Obj { value: None },
                Type::Void => unreachable!(),
            })
        }
        return Object { class, fields };
    }
}

#[derive(Debug)]
pub struct Class {
    parent_class: Rc<Class>,
    class_file: Rc<ClassFile>,
    pub vtable: Vec<MethodInfo>,
    pub fields_descr_to_index: HashMap<u16, u16>,
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

pub struct Heap {
    objects: LinkedList<Rc<Object>>
}

impl Heap {
    pub fn allocate_object(&self, class: Rc<Class>) -> Rc<Object> {
        let object = Object::from_class(class);
        let object_ref = Rc::new(object);
        self.objects.push_back(object_ref);
        return object_ref
    }

    pub fn new() -> Heap {
        Heap { objects: LinkedList::new() }
    }
}