// Generate vocabulary lists and analyze with tabulae
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.mid.validator._

import edu.holycross.shot.tabulae.builder._
import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

import sys.process._
import scala.language.postfixOps

val compiler = "/usr/bin/fst-compiler-utf8"
val fstinfl = "/usr/bin/fst-infl"
val make = "/usr/bin/make"


def compile(repo: String =  "./tabulae") = {
  val tabulae = File(repo)
  val datasets = "."
  val c = "livy-morphology"
  val conf =  Configuration(compiler,fstinfl,make,datasets)

  try {
    FstCompiler.compile(File(datasets), File(repo), c, conf, true)
    val tabulaeParser = repo/"parsers/livy-morphology/latin.a"
    val localParser = File("parser/latin.a")
    cp(tabulaeParser, localParser)
    println("\nCompilation completed.  Parser latin.a is " +
    "available in directory \"parser\"\n\n")
  } catch {
    case t: Throwable => println("Error trying to compile:\n" + t.toString)
  }

}

/**  Parse words listed in a file, and return their analyses
* as a String.
*
* @param wordsFile File with words to parse listed one per line.
* @param parser Name of corpus-specific parser, a subdirectory of
* tabulae/parsers.
*/
def parse(wordsFile : String) : String = {
  val cmd = fstinfl + " parser/latin.a  " + wordsFile
  println("Beginning to parse word list in " + wordsFile)
  println("Please be patient: there will be a pause after")
  println("the messages 'reading transducer...' and 'finished' while the parsing takes place.")
  cmd !!
}


println("Compile a morphological parser from a tabulae")
println("repository located in ./tabulae :")
println("\n\tcompile()\n")
println("or from tabulae in a specified directory:")
println("\n\tcompile(\"TABULAE_DIRECTORY\" )\n")
