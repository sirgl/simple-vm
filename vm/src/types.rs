

pub enum Type {
    Bool,
    Void,
    I8,
    I32,
    Array { base_type: Box<Type> },
    Object { package: String, name: String },
}

impl Type {
    pub fn parse(string: &str) -> Type {
//        string.
        unimplemented!();
    }
}