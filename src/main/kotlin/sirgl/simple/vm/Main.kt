package sirgl.simple.vm

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.ShowHelpException
import com.xenomachina.argparser.default
import java.io.StringWriter

class MyArgs(parser: ArgParser) {
    val sourcePath by parser.storing(
        "-s", "--source",
        help = "path to source files and libs"
    ).default { "." }

    val mainQualifiedName by parser.storing(
        "-m", "--mainclass",
        help = "fully qualified name of class, containing main function"
    )

}

class Configuration(
    val sourcePath: String, // TODO make it list
    val mainFQN: String
)

fun main(args: Array<String>) {
    try {
        val parsedArgs = ArgParser(args)
            .parseInto(::MyArgs)
        val compiler = LangCompiler(Configuration(parsedArgs.sourcePath, parsedArgs.mainQualifiedName), ::buildDefaultPipeline)
        compiler.run()
    } catch (e: ShowHelpException) {
        val writer = StringWriter()
        e.printUserMessage(writer, "langc", 80)
        println(writer)
    }

}