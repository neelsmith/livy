// Compile a morphological parser

import edu.holycross.shot.tabulae.builder._
import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

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


println("Compile a morphological parser from tabulae")
println("repository in adjacent directory:")
println("\n\tcompile()\n")
println("or in a specified directory:")
println("\n\tcompile(\"TABULAE_DIRECTORY\" )\n")
