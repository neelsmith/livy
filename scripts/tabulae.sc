// Generate vocabulary lists and analyze with tabulae
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2.{TextRepositorySource, Corpus => O2Corpus}
import edu.holycross.shot.mid.validator._
import edu.holycross.shot.latin._

import edu.holycross.shot.tabulae.builder._
import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

import scala.collection.immutable.ListMap

import java.io.PrintWriter

import scala.io.Source
import sys.process._
import scala.language.postfixOps

val compiler = "/usr/bin/fst-compiler-utf8"
val fstinfl = "/usr/bin/fst-infl"
val make = "/usr/bin/make"


def fullCorpus :  O2Corpus = {
  println("Assembling complete corpus...")
  val c =  TextRepositorySource.fromCexFile("editions/livy-omar.cex").corpus
  println("Done.")
  c
}


def minkovaTunberg(corpus: O2Corpus =  fullCorpus) :  O2Corpus = {
    println("Extracting Minkova-Tunberg subcorpus...")

    val idxFile =  "minkova-tunberg/minkova-tunberg.txt"
    val mkUrns  =  for (ln <- Source.fromFile(idxFile).getLines.toVector) yield {
      CtsUrn(ln)
    }
    //https://www.scala-lang.org/old/node/55.html
    val mkCorpus = corpus ~~ mkUrns
    println("Done.")
    mkCorpus
}


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
def parse(wordsFile : String = "words.txt") : String = {
  val cmd = fstinfl + " parser/latin.a  " + wordsFile
  println("Beginning to parse word list in " + wordsFile)
  println("Please be patient: there will be a pause after")
  println("the messages 'reading transducer...' and 'finished' while the parsing takes place.")
  cmd !!
}

def wordList(corpus: O2Corpus, wordsFile: String = "words.txt") = {
  val tokens = Latin24Alphabet.tokenizeCorpus(corpus)
  val lexical = tokens.filter(_.tokenCategory.toString == "Some(LexicalToken)")
  val wordList = lexical.map(_.string.toLowerCase).distinct.sorted
  new PrintWriter(wordsFile) { write(wordList.mkString("\n")); close;}
}

case class Freq(string: String, count: Int)


def parseN(histo: Vector[Freq], n: Int = 100, wordsFile : String = "words.txt") : String = {
  val words = histo.take(n).map(_.string)
  new PrintWriter(wordsFile) { write(words.mkString("\n")); close;}
  parse(wordsFile)
}

def parseThreshold(histo: Vector[Freq],thresh: Int = 100, wordsFile : String = "words.txt") : String = {
  val words = histo.filter(_.count > thresh).map(_.string)
  new PrintWriter(wordsFile) { write(words.mkString("\n")); close;}
  parse(wordsFile)
}

def histogram(tokens: Vector[MidToken]): Vector[Freq] = {
  val grouped = tokens.groupBy(_.string)
  val mapped = grouped.map{ case (k,v) => (k, v.size )  }
  val counts = mapped.toSeq.sortWith(_._2 > _._2)
  val freqs = for (count <- counts) yield { Freq(count._1, count._2) }
  freqs.toVector
}


def guide : Unit = {


println("\n\n1. Data sets")
println("-----------")
println("Load full corpus of Livy and Periochae:")
println("\n\tval corpus = fullCorpus")
println("\nExtract selections in Minkova-Tunberg edition:")
println("\n\tval mtCorpus = minkovaTunberg(corpus)")


println("\n\n2. Analyzing corpora")
println("--------------------")
println("Write word list for a corpus to a file:")
println("\n\twordList(corpus, FILENAME)")
println("\n(FILENAME is optional: default is 'words.txt')")
println("\n\nTokenize a corpus:")
println("\n\tval tokens = Latin24Alphabet.tokenizeCorpus(corpus)")
println("\nSelect only lexical tokens from a list of tokens:")
println("\n\tval lexical = tokens.filter(_.tokenCategory.toString == \"Some(LexicalToken)\")")
println("\nCreate histogram of tokens for a list of tokens:")
println("\n\tval histogram = histogram(lexical)")


println("\n\n3. Parsing")
println("----------")
println("Compile a morphological parser from a tabulae")
println("repository located in ./tabulae :")
println("\n\tcompile()\n")
println("\n\nParse a word list:")
println("\n\tval results = parse(WORDSFILE)")
println("\n(WORDSFILE is optional: default is 'words.txt')")
println("\nParse top N items in a histogram:")
println("\n\tparseN(tokens,N)")
println("\n(N is optional: default is 100)")

println("\nParse items in a histogram appearing more than N times:")
println("\n\tparseThreshold(tokens,THRESHOLD)")
println("\n(THRESHOLD is optional: default is 100)")

}

println("\n\nFor a quick guide:")
println("\n\tguide")
