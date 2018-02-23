package sirgl.simple.vm.lexer

enum class LexemeKind {
    // Keywords
    Fun,
    While,
    Class,
    Var,
    Native,
    Continue,
    Break,
    Return,
    Try,
    Catch,
    True,
    False,
    Import,
    Package,

    // Types
    I32,
    I8,
    Bool,
    Void,

    // Punctuation
    Semicolon, // ;
    LParen, // (
    RParen, // )
    LBrace, // {
    RBrace, // }
    LBracket, // [
    RBracket, // ]
    Dot, // .
    Colon, // :
    Comma, // ,

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