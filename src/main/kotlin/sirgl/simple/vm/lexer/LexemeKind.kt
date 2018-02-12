package sirgl.simple.vm.lexer

enum class LexemeKind {
    Fun,
    While,
    Class,
    Var,
    Bool,
    Native,
    Continue,
    Break,
    Return,
    Try,
    Catch,
    I32,
    I8,
    True,
    False,

    Identifier,

    Operator,

    Comment,

    Error,

    IntLiteral,

    StringLiteral,

    CharLiteral,

    WhiteSpace
}