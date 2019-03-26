import scala.io.Source
import edu.holycross.shot.tabulae._

val mtWordList = "mtLexIndexLc.cex"
val livy6bksList = "livy6bksIndexLc.cex"
val livyWordList = "livyLexIndexLc.cex"

val mtParses : String = Source.fromFile("mtParses.txt").getLines.mkString("\n")
val livy6bkParses : String = Source.fromFile("sixBooksParsed.txt").getLines.mkString("\n")
val livyParses : String = Source.fromFile("livy-all-parsed.txt").getLines.mkString("\n")


def histoFromIndex(fName: String): Vector[(String, Int)] = {
  val wordIndex = Source.fromFile(fName).getLines.toVector
  val wds = for (tkn <- wordIndex) yield {
    val cols = tkn.split("#").toVector
    cols.size match {
      case 2 => cols(1)
      case _ => {println("NO COLS IN '" + cols + "''"); ""}
    }
  }
  wds.groupBy(s => s).map { m => (m._1,m._2.size)}.toSeq.sortBy(_._2).reverse.toVector
}

def mtHisto = histoFromIndex(mtWordList)
def l6Histo = histoFromIndex(livy6bksList)
def livyHisto = histoFromIndex(livyWordList)


def fails(parseString: String) : Vector[String] = {
  parseString.split("\n").filter(_.contains("no result")).toVector.map(s => s.replaceAll("no result for ","")).toVector
}

case class TokenEntityMatches (tkn: String, ids: Vector[String], analyses: Vector[String])

def tokenToEntity(parseString: String) : Vector[TokenEntityMatches]= {
  val parsedTokens =   parseString.split("\n").filterNot(_.contains("no result")).mkString("\n").split("> ").toVector.filter(_.nonEmpty)
  val teMatches = for (parse <- parsedTokens) yield {
    val lines = parse.split("\n").toVector
    val tkn = lines.head.replaceFirst("> ","")
    val analyses = lines.tail
    val lexents = for (lysis <- analyses) yield {
      lysis.replaceAll("<u>[^<]+</u><u>([^<]+).+","$1")
    }
    TokenEntityMatches(tkn, lexents.toVector, analyses)
  }
  teMatches
}

def lemmaFormMap(teMatches: Vector[TokenEntityMatches]) = {
  val pairs = for (tem <- teMatches) yield {
    val pairings = for (lexent <- tem.ids) yield {
      (lexent, tem.tkn)
    }
    pairings
  }
  val grouped = pairs.flatten.groupBy(_._1)
  grouped.map{ case (k,v) => { val slim = v.map(_._2); (k,slim.distinct) } }

}



case class LemmatizedCorpus(lemmaMappings: Map[String, Vector[String]], tokenHisto: Vector[(String,Int)]) {

  def tokenOccurrences(tkn: String) : Option[Int] = {
    val found = tokenHisto.filter(_._1 == tkn)
    found.size match {
      case 1 => Some(found(0)._2)
      case 0 => {println("No matches found for " + tkn + "."); None}
      case _ => {println("Multiple matches found for " + tkn + "."); None}
    }
  }

  def lemmaOccurrences(id: String) : Option[Int] = {
    try {
      val tokens = lemmaMappings(id)
      val counts = tokens.map(tokenOccurrences(_)).flatten.map(_.toInt)
      Some(counts.sum)

    } catch {
      case nse: java.util.NoSuchElementException => {
        println("No entity with id " + id + " found.")
        None
      }
    }
  }

  def lexicalEntities: Vector[String] = lemmaMappings.keySet.toSeq.sorted.toVector

  def lemmaHisto = {

    val counts = for (lex <- lexicalEntities) yield {
      (lex,lemmaOccurrences(lex).get )
    }
    counts.toVector.sortBy(_._2).reverse
  }
}


def lemmatizedFromParses(parseString: String, tokenHisto: Vector[(String,Int)]) : LemmatizedCorpus = {

  //compute lemmaMappings:
  val teMapping = tokenToEntity(parseString)
  // get map of lemma to forms
  val formMap = lemmaFormMap(teMapping)


  //use apropriate token histogram (eg, mtHisto),
  // and create a LemmatizedCorpus
  LemmatizedCorpus(formMap, tokenHisto)
}

def summarizeAnalyses(parseString: String): Unit = {
  val bad = fails(parseString)
  val better = tokenToEntity(parseString)
  val total = bad.size + better.size

  println("\nSuccess rate:")
  println("-------------")
  println("Distinct tokens: " + total)

  println("Successfully parsed: " + better.size + s" (${((better.size.toDouble / total) * 100).round}%)")
  println("Failed to parse: " + bad.size)


  println("\nMorphological ambiguity:")
  println("------------------------")
  val ambiguousEntity = better.filter(_.ids.distinct.size > 1)
  println(s"${ambiguousEntity.size} entries could be from more than one lexical entity.")
  println(s"(${((ambiguousEntity.size.toDouble / better.size) * 100).round}% of successful parses.)")

  val ambiguousForm = better.filterNot(_.ids.distinct.size > 1).filter(_.ids.size > 1)
  println(s"${ambiguousForm.size} entries could be more than form of the same lexical entity.")
  println(s"(${((ambiguousForm.size.toDouble / better.filterNot(_.ids.distinct.size > 1).size) * 100).round}% of parses  identified with a single lexical entity.)")
}

/*
// Get a lemmatized MT:
val mtLemmatized = lemmatizedFromParses(mtParses,mtHisto)

// ALl of Livy:
val livyLemmatized = lemmatizedFromParses(livyParses,livyHisto)
*/
