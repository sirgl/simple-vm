package sirgl.simple.vm.lexer

enum class LexemeKind {
    // Keywords
    Fun,
    Constructor,
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
    If,
    Else,
    Super,
    This,
    Null,

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

    // Identifiers
    Identifier,

    // Operators
    OpPlus,
    OpMinus,
    OpAsterisk,
    OpDiv,
    OpPercent,
    OpExcl,
    OpLtEq,
    OpLt,
    OpGtEq,
    OpGt,
    OpEq,
    OpEqEq,
    OpNotEq,
    OpAndAnd,
    OpOrOr,
    OpAs,
    OpIs,

    // Comments
    EolComment,
    CStyleComment,

    // Errors
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