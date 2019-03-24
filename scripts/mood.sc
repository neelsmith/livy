import edu.holycross.shot.tabulae.builder._

import sys.process._
import scala.language.postfixOps

import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

val compiler = "/usr/bin/fst-compiler-utf8"
val fstinfl = "/usr/bin/fst-infl"
val make = "/usr/bin/make"

/**  Parse words listed in a file, and return their analyses
* as a String.
*
* @param wordsFile File with words to parse listed one per line.
* @param parser Name of corpus-specific parser, a subdirectory of
* tabulae/parsers.
*/

def makeWordList(file: String): String = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("editions/" + file).mkString
  val corpusnospace = corpus.replaceAll("\\p{Punct}|\\d","").toLowerCase
  val corpussplit = corpusnospace.split("\\s+")
  val finalcorpus = corpussplit.filter(_.nonEmpty)
  val md = new BufferedWriter(new FileWriter(new File("results/" + "pl_" + file)))
  for (diff <- finalcorpus) md.write(diff + "\n")
  md.close()
  val fl = System.getProperty("user.dir")
  val fll = fl + "/results/" + "pl_" + file
  return fll
}

def parse(wordFile: String, parser: String = "livy-morphology") : String = {
  val fcp = makeWordList(wordFile)
  def compiled = s"tabulae/parsers/${parser}/latin.a"
  val cmd = fstinfl + " " + compiled + "  " + fcp
  println("Beginning to parse word list in " + wordFile)
  println("Please be patient: there will be a pause after")
  println("the messages 'reading transducer...' and 'finished' while the parsing takes place.")
  cmd !!
}

def mood(wordsFile: String = "words.txt", output1: String = "indic.txt", output2: String = "imptv.txt", output3: String = "subj.txt") : Unit = {
  import java.io._
  import java.lang._
  val parses = parse(wordsFile)
  val a = parses.mkString
  val aa = a.split("> ").toArray.filter(_.contains("<indic"))
  val aaa = new BufferedWriter(new FileWriter(new File("results/" + output1)))
  for (diff <- aa) aaa.write(diff + "\n")
  aaa.close()
  println("created list of " + aa.size + " indicatives in RESULTS directory")
  val bb = a.split("> ").toArray.filter(_.contains("<imptv"))
  val bbb = new BufferedWriter(new FileWriter(new File("results/" + output2)))
  for (diff <- bb) bbb.write(diff + "\n")
  bbb.close()
  println("created list of " + bb.size + " imperatives in RESULTS directory")
  val cc = a.split("> ").toArray.filter(_.contains("<subj"))
  val ccc = new BufferedWriter(new FileWriter(new File("results/" + output3)))
  for (diff <- cc) ccc.write(diff + "\n")
  ccc.close()
  println("created list of " + cc.size + " subjunctives in RESULTS directory")
}
