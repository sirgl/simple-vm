use std::collections::HashMap;
use memory::Class;
use class_file::ClassFile;
use std::rc::Rc;
use class_file::MethodInfo;

struct Package {
    name_to_class: HashMap<String, Class> // TODO string key?? not efficient?
}


pub struct ClassIndex {
    name_to_package: HashMap<String, Package>
}

impl ClassIndex {
    pub fn from_files(files: &Vec<ClassFile>) -> ClassIndex {
        let incomplete_index = IncompleteIndex::from_files(files);
        unimplemented!();
        // TODO complete index
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
    pub name_to_class: HashMap<String, IncompleteClass>
}

impl IncompletePackage {
    fn add_class(&self, class_name: &str, class: IncompleteClass) {
        self.name_to_class.insert(class_name.to_string(), class);
    }

    fn new() -> IncompletePackage {
        IncompletePackage { name_to_class: HashMap::new() }
    }
}

struct IncompleteIndex {
    name_to_package: HashMap<String, IncompletePackage>
}

impl IncompleteIndex {
    fn from_files(files: &Vec<ClassFile>) -> IncompleteIndex {
        let mut name_to_package: HashMap<String, IncompletePackage> = HashMap::new();
        let incomplete_classes = files.iter()
            .map(|file| IncompleteClass::new(file));
        for incomplete_class in incomplete_classes {
            let class_file = incomplete_class.class_file;
            let constant_pool = &class_file.pool;
            let class_cpentry = constant_pool.resolve_to_class(class_file.class_descriptor).unwrap(); // TODO error, actually possible

            let name = class_cpentry.resolve_simple_name(constant_pool).unwrap(); // also here
            let package = class_cpentry.resolve_package(constant_pool).unwrap();
            match name_to_package.get(package) {
                None => {
                    name_to_package.insert(package.to_string(), IncompletePackage::new())
                }
                Some(packageEntry) => {
                    packageEntry.add_class(name, incomplete_class)
                }
            }
        }
        IncompleteIndex { name_to_package }
    }
}
