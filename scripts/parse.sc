import edu.holycross.shot.tabulae.builder._

import sys.process._
import scala.language.postfixOps

import better.files._
import java.io.{File => JFile}
import better.files.Dsl._

val compiler = "/usr/bin/fst-compiler-utf8"
val fstinfl = "/usr/bin/fst-infl"
val make = "/usr/bin/make"


def guide : Unit = {
  println("\nWelcome to your parser with TABULAE!")
  println("\nFor information about the following subjects, see below:")
  println("\n" + """Histograms: type "histograms" and press enter/return""")
  println("""Wordlists: type "wordlists" and press enter/return""")
  println("""Parsing: type "parser" and press enter/return""")
}

def histograms : Unit = {
  println("\nUsing a histogram function will provide you with a list of word")
  println("forms from your edition, sorted by frequency. You can write a histogram")
  println("for the whole edition or specify a histogram for a part of speech. ")
  println("""Each function creates a file in the "results" directory. Note that""")
  println("if you run a function again, that file will be overwritten. Each")
  println("function and its usage is listed below:")
  println("\nhistThresh(THRESHOLD, EDITION)")
  println("""     Type "histThresh" with the threshold """ + """(i.e. number of most frequent""")
  println("""     word forms you want included in the histogram) and the edition you""")
  println("     want to make a histogram out of in parentheses. Find the histogram in the")
  println("""     "results" directory, beginning with "hist" and the threshold number.""")
  println("     Ex: histThresh(10," + """ "ode1_38.txt")""")
  println("\nhistogram(EDITION)")
  println("""     Type "histogram" with the edition in parentheses. This function makes""")
  println("     a histogram of every word form in the edition, no threshold provided. Find")
  println("""     the histogram in the "results" directory, beginning with "hist." """")
  println("     Ex: histogram(" + """"ode1_38.txt")""")
  println("\nFor histograms of parts of speech, do the same as above but use the following")
  println("""functions. In the "results" directory the histograms are specified by part""")
  println("""of speech, not "hist." Note that you must parse the edition first and use the""")
  println(""""toFile" function to write the full parse to a file before these histograms are""")
  println("usable.")
  println("\n" + "     " + "For nouns: nounHist(EDITION)")
  println("     For verbs: verbHist(EDITION)")
  println("     For adjectives: adjHist(EDITION)")
  println("     For adverbs: advHist(EDITION)")
  println("     For participles: ptcplHist(EDITION)")
  println("     For pronouns: pronHist(EDITION)")
  println("     For infinitives: infinHist(EDITION)")
  println("     For indeclinables: indeclHist(EDITION)")
  println("     For gerunds: gndHist(EDITION)")
  println("     For gerundives: gdvHist(EDITION)")
  println("     For supines: supHist(EDITION)")
  println("\n" + """If you would like to parse a histogram, simply place it in the "editions"""")
  println("""directory and call it by name with the "parse" function.""")
}

def wordlists : Unit = {
  println("\nUsing the wordlist function will provide you with a list of word")
  println("forms from your edition, sorted alphabetically, with no duplicates.")
  println("for the whole edition or specify a histogram for a part of speech. ")
  println("""This function provides a file starting in "wl" the "results" directory""")
  println("Note that if you run the function again, that file will be overwritten.")
  println("\nwordList(EDITION)")
  println("""     Type "wordList" with the edition in parentheses.""")
  println("     Ex: wordList(" + """"ode1_38.txt")""")
  println("\n" + """If you would like to parse a wordlist, simply place it in the "editions"""")
  println("""directory and call it by name with the "parse" function.""")
}

def parser : Unit = {
  println("\nParsing a corpus incorporates three functions. First, you must compile")
  println("""a parser with the "compile" function, after which you can use the "parse" function.""")
  println("""To write a full version of the parse to the "results" directory, use the""")
  println(""""toFile" function. These functions are all explained below.""")
  println("\ncompile(MORPHOLOGY)")
  println("""     Type "compile" with the morphology """ + """(i.e. the directory of stem""")
  println("""     and rule tables, such as "livy-morphology") and hit return/enter. This""")
  println("     will create your parser under the same name as the morphology.")
  println("""     Ex: compile("livy-morphology")""")
  println("\nparse(EDITION, PARSER)")
  println("""     Type "parse" with the edition """ + "(i.e. the file you want to parse")
  println("""     in the "editions" directory), then the name of the parser you've compiled.""")
  println("     Then hit return/enter. This will parse your edition in the command line.")
  println("""     Note that the default parser is "livy-morphology" for this repository, so """)
  println("     you do not need to type the parser after the edition if you are parsing livy.")
  println("""     Also note that this function creates a wordlist starting with "pl" in the "results"""")
  println("""     directory. The list is the same as what you would acquire from the "wordList"""")
  println("     function.")
  println("""     Ex: parse("ode1_38.txt", "livy-morphology")""")
  println("\ntoFile(RESULT, EDITION)")
  println("""     Type "toFile" with the result """ + "(i.e. something like res7, the")
  println("     name of the string that your parse appeared as in the command line), then")
  println("     the name of the edition you parsed. Then hit return/enter. This will create")
  println("""     a file starting with "fp" in the "results" directory that contains your""")
  println("     full parse. This is a necessary action before creating histograms.")
  println("""     Ex: toFile(res7, "ode1_38.txt")""")
}

def wordList(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("editions/" + file).mkString.filter(!_.isDigit)
  val corpusnospace = corpus.replaceAll("\n"," ").trim
  val corpussplit = corpusnospace.replaceAll("\\p{Punct}|\\d","").toLowerCase.split("\\s+").toSet
  val finalcorpus = corpussplit.toArray.filter(_.nonEmpty).sorted
  val md = new BufferedWriter(new FileWriter(new File("results/" + "wl_" + file)))
  for (diff <- finalcorpus) md.write(diff + "\n")
  md.close()
  println(finalcorpus.size + " unique lexical tokens")
}

def compile(corpus: String, datasets: String = ".") = {
  val repo = File("./tabulae")
  val conf =  Configuration(compiler, fstinfl, make, datasets)
  try {
    FstCompiler.compile(File(datasets), repo, corpus, conf, true)
    val tabulaeParser = repo/"parsers"/corpus/"latin.a"

    println("\nCompilation completed.\nParser is available in " +  tabulaeParser)
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

def toFile(result: String, name: String): Unit = {
  import java.io._
  import java.lang._
  val pw = new BufferedWriter(new FileWriter(new File("results/" + "fp_" + name)))
  pw.write(result)
  pw.close()
}

def histogram(file : String) : Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("editions/" + file).mkString
  val corpusnospace = corpus.replaceAll("\n"," ").trim
  val corpussplit = corpusnospace.replaceAll("\\p{Punct}|\\d","").toLowerCase.split("\\s+")
  val hist = corpussplit.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "hist" + "_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created histogram in RESULTS directory")
}

def histThresh(thresh : Int = 100, file : String) : Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("editions/" + file).mkString
  val corpusnospace = corpus.replaceAll("\n"," ").trim
  val corpussplit = corpusnospace.replaceAll("\\p{Punct}|\\d","").toLowerCase.split("\\s+")
  val hist = corpussplit.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).take(thresh).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "hist" + thresh + "_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created histogram with threshold in RESULTS directory")
}

def nounHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("noun>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "nouns_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of nouns in RESULTS directory")
}

def verbHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("verb>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "verbs_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of verbs in RESULTS directory")
}


def ptcplHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("ptcpl>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "ptcpls_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of participles in RESULTS directory")
}


def adjHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("adj>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "adjs_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of adjectives in RESULTS directory")
}

def advHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("adv>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "adverbs_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of adverbs in RESULTS directory")
}


def indeclHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("<indecl"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "indecls_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of indeclinables in RESULTS directory")
}

def pronHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("pron>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "pronouns_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of pronouns in RESULTS directory")
}

def gndHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("gnd>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "gnds_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of gerunds in RESULTS directory")
}

def gdvHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("gdv>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "gdvs_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of gerundives in RESULTS directory")
}

def infinHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("infin>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "infins_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of infinitives in RESULTS directory")
}

def supHist(file: String): Unit = {
  import java.io._
  import java.lang._
  val corpus = scala.io.Source.fromFile("results/" + "fp_" + file).mkString
  val co = corpus.split("> ").toArray.filter(_.contains("sup>"))
  val cor = co.mkString.split("\n")
  val corp = cor.toArray.filterNot(_.contains("<u>")).filterNot(_.contains("no result"))
  val hist = corp.groupBy(e => e).map(e => e._1 -> e._2.length)
  import scala.collection.immutable.ListMap
  val histo = ListMap(hist.toSeq.sortWith(_._2 > _._2):_*).toString
  val histog = histo.replaceAll("[()]","").trim.replaceAll("ListMap","").trim.replaceAll(" -> ","#")
  val histogr = histog.split(", ").toArray
  val hd = new BufferedWriter(new FileWriter(new File("results/" + "sups_" + file)))
  for (diff <- histogr) hd.write(diff + "\n")
  hd.close()
  println("created list of supines in RESULTS directory")
}

println("\n\nFor a guide to usage, type " + """"guide"""" + " and hit return/enter.\n")
