package sirgl.simple.vm.driver

import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.impl.LangClassImpl
import sirgl.simple.vm.common.AstCache
import sirgl.simple.vm.lexer.HandwrittenLangLexer
import sirgl.simple.vm.lexer.LangLexer
import sirgl.simple.vm.lexer.LexemeKind
import sirgl.simple.vm.lexer.UnknownLexemeError
import sirgl.simple.vm.parser.HandwrittenLangParser
import sirgl.simple.vm.parser.LangParser
import sirgl.simple.vm.parser.ParseError
import sirgl.simple.vm.roots.SourceFileSource

// TODO probably thread pool is not needed, I can just create threads that will take sourceFile from
class AstBuilder(
        private val globalScope: GlobalScope,
        private val errorSink: ErrorSink,
        private val astCache: AstCache
) : AutoCloseable {
//    private val threadPool = ThreadPoolExecutor(4, 4, 0, TimeUnit.MILLISECONDS, ArrayBlockingQueue(100))

    private val lexer: LangLexer = HandwrittenLangLexer()
    private val parser: LangParser = HandwrittenLangParser()

    fun submit(fileSymbolSource: SourceFileSource) {
        AstBuildingTask(fileSymbolSource, globalScope, astCache, lexer, parser, errorSink).run() // TODO get back thread pool
    }

    override fun close() {
//        threadPool.shutdown()
    }
}

class AstBuildingTask(
        private val sourceFileSource: SourceFileSource,
        private val globalScope: GlobalScope,
        private val astCache: AstCache,
        private val lexer: LangLexer,
        private val parser: LangParser,
        private val errorSink: ErrorSink
) : Runnable {

    override fun run() {
        val file = parse() ?: return
        (file.classDecl as LangClassImpl).setupSymbol(globalScope)
        globalScope.addSourceFile(file)
        astCache.addSourceFile(file, file.symbolSource, this)
    }

    fun parse(): LangFile? {
        val text = sourceFileSource.getInputStream().bufferedReader().readText()
        val tokens = lexer.tokenize(text)

        for (token in tokens) {
            if (token.kind == LexemeKind.Error) {
                errorSink.submitError(UnknownLexemeError(token, sourceFileSource))
            }
        }
        if (errorSink.hasErrors) return null

        val parseResult = parser.parse(tokens)
        val fail = parseResult.fail
        return if (fail != null) {
            errorSink.submitError(ParseError(fail.toString(), sourceFileSource))
            null
        } else {
            val ast = parseResult.ast!!
            ast.symbolSource = sourceFileSource
            ast
        }
    }
}