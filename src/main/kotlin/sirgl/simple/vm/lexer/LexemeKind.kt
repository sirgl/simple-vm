package sirgl.simple.vm.lexer

enum class LexemeKind {
    // Keywords
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
    Import,
    Package,

    // Punctuation
    Semicolon,
    LBrace, // {
    RBrace, // {
    LBracket, // [
    RBracket, // ]
    Dot, // .

    // Identifier
    Identifier,

    // Operators
    Operator,

    // Comments
    Comment,

    // Error
    Error,

    // Literals
    IntLiteral,
    StringLiteral,
    CharLiteral,

    // Spaces
    WhiteSpace,

    // EOL
    EOL
}