package sirgl.simple.vm.driver

import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.lexer.HandwrittenLangLexer
import sirgl.simple.vm.lexer.LangLexer
import sirgl.simple.vm.lexer.LexemeKind
import sirgl.simple.vm.lexer.UnknownLexemeError
import sirgl.simple.vm.parser.HandwrittenLangParser
import sirgl.simple.vm.parser.LangParser
import sirgl.simple.vm.parser.ParseError

// TODO probably thread pool is not needed, I can just create threads that will take sourceFile from
class AstBuilder(
    private val resolveCache: ResolveCache,
    private val errorSink: ErrorSink
) : AutoCloseable {
//    private val threadPool = ThreadPoolExecutor(4, 4, 0, TimeUnit.MILLISECONDS, ArrayBlockingQueue(100))

    private val lexer: LangLexer = HandwrittenLangLexer()
    private val parser: LangParser = HandwrittenLangParser()

    fun submit(sourceFile: SourceFile) {
        AstBuildingTask(sourceFile, resolveCache, lexer, parser, errorSink).run() // TODO get back thread pool
    }

    override fun close() {
//        threadPool.shutdown()
    }
}

class AstBuildingTask(
    private val sourceFile: SourceFile,
    private val resolveCache: ResolveCache,
    private val lexer: LangLexer,
    private val parser: LangParser,
    private val errorSink: ErrorSink
) : Runnable {

    override fun run() {
        val file = parse() ?: return
//        resolveCache.addSourceFile(sourceFile, file, this) // TODO
    }

    fun parse(): LangFile? {
        val text = sourceFile.inputStream.bufferedReader().readText()
        val tokens = lexer.tokenize(text)

        for (token in tokens) {
            if (token.kind == LexemeKind.Error) {
                errorSink.submitError(UnknownLexemeError(token, sourceFile))
            }
        }
        if (errorSink.hasErrors) return null

        val parseResult = parser.parse(tokens)
        val fail = parseResult.fail
        return if (fail != null) {
            errorSink.submitError(ParseError(fail.toString(), sourceFile))
            null
        } else {
            parseResult.ast!!
        }
    }
}