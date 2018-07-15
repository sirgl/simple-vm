use std::collections::HashMap;
use memory::Class;
use class_file::ClassFile;
use std::rc::Rc;
use class_file::MethodInfo;

struct Package {
    name_to_class: HashMap<String, Class> // TODO string key?? not efficient?
}


struct Resolve {
    name_to_package: HashMap<String, Package>
}

impl Resolve {
    fn from_files(files: &Vec<ClassFile>) -> Resolve {
        files.iter().map(|file| file).collect();
        Resolve { name_to_package: HashMap::new }
    }

}


// Building (without cross references)

struct IncompleteClass {
    class_file: Rc<ClassFile>,
    vtable: Vec<MethodInfo>,
    field_descr_to_index: HashMap<u16, u16>,
}

impl IncompleteClass {
    fn new(file: &ClassFile) -> IncompleteClass {
        let vtable = file.methods;
        let field_count = file.field_descriptors.len();
        let mut field_descr_to_index = HashMap::<u16, u16>::with_capacity(field_count);
        let mut i = 0 as u16;
        for descriptor in file.field_descriptors {
            field_descr_to_index.insert(descriptor, i);
            i += 1;
        }
        IncompleteClass {
            class_file: Rc::from(file),
            vtable,
            field_descr_to_index,
        }
    }
}

struct IncompletePackage {
    name_to_class: HashMap<String, IncompleteClass>
}

struct TemporaryResolve {
    name_to_package: HashMap<String, Package>
}
