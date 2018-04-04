package sirgl.simple.vm.type

fun LangType.isSubtypeOf(another: LangType) : Boolean {
    return when (another) {
        is NullType -> true
        is I32Type -> this === I32Type || this === I8Type
        is ClassType -> this is ClassType // TODO
        else -> false
    }
}