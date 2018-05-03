package sirgl.simple.vm.type

import sirgl.simple.vm.common.CommonClassNames
import sirgl.simple.vm.common.CommonClassTypes
import sirgl.simple.vm.resolve.symbols.findParentSymbol

fun LangType.isAssignableTo(another: LangType): Boolean {
    return subtypeStatus(another).isSubtype
}

fun LangType.subtypeStatus(another: LangType): SubtypeResult {
    if (this is MethodReferenceType || another is MethodReferenceType) {
        throw IllegalArgumentException("Method reference type should not be used to check subtype")
    }
    return when (another) {
        is NullType -> SubtypeResult(true)
        is I32Type -> when (this) {
            is I32Type -> SubtypeResult(true)
            is I8Type -> SubtypeResult(true, I32Type)
            is ClassType -> classTypeToPrimitivePromotionCheck(CommonClassNames.LANG_I32, I32Type)
            else -> SubtypeResult(false)
        }
        is I8Type -> when (this) {
            is I8Type -> SubtypeResult(true)
            is ClassType -> classTypeToPrimitivePromotionCheck(CommonClassNames.LANG_I8, I8Type)
            else -> SubtypeResult(false)
        }
        is BoolType -> when (this) {
            is BoolType -> SubtypeResult(true)
            is ClassType -> classTypeToPrimitivePromotionCheck(CommonClassNames.LANG_BOOLEAN, BoolType)
            else -> SubtypeResult(false)
        }
        is ClassType -> when (this) {
            is I8Type -> primitiveToClassPromotionCheck(another, CommonClassNames.LANG_I8, CommonClassTypes.LANG_I8)
            is I32Type -> primitiveToClassPromotionCheck(another, CommonClassNames.LANG_I32, CommonClassTypes.LANG_I32)
            is BoolType -> primitiveToClassPromotionCheck(another, CommonClassNames.LANG_BOOLEAN, CommonClassTypes.LANG_BOOLEAN)
            is ClassType -> {
                val qualifiedName = another.classSymbol.qualifiedName
                if (classSymbol.findParentSymbol { it.qualifiedName == qualifiedName} != null) {
                    SubtypeResult(true)
                } else {
                    SubtypeResult(false)
                }
            }
            else -> SubtypeResult(false)
        }
        else -> SubtypeResult(false)
    }
}

private fun primitiveToClassPromotionCheck(
    another: ClassType,
    anotherTypeName: String,
    typeToPromotion: ClassType
) = if (another.classSymbol.qualifiedName == anotherTypeName) {
    SubtypeResult(true, typeToPromotion)
} else {
    SubtypeResult(false)
}

private fun ClassType.classTypeToPrimitivePromotionCheck(name: String, promoteToType: LangType) = when {
    promoteToType.name == CommonClassNames.LANG_I32 -> SubtypeResult(true, promoteToType)
    else -> SubtypeResult(false)
}

class SubtypeResult(
    val isSubtype: Boolean,
    val promoteTo: LangType? = null // is null when isSubtype == false
)

// One type is subtype: any type is subtype of NullType or any class type is subtype of lang.Object
// Type is not subtype, but after promotion will be: i32 is not subtype of I32, but after promotion to I32 it will be
// Type is not subtype anyway