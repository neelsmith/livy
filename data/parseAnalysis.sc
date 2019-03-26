import scala.io.Source
import edu.holycross.shot.tabulae._

val mtWordList = "mtLexIndexLc.cex"
val livy6bksList = "livy6bksIndexLc.cex"
val livyWordList = "livyLexIndexLc.cex"

val mtParses : String = Source.fromFile("mtParses.txt").getLines.mkString("\n")
val livy6bkParses : String = Source.fromFile("sixBooksParsed.txt").getLines.mkString("\n")
val livyParses : String = Source.fromFile("livy-all-parsed.txt").getLines.mkString("\n")

val lsLabels : Vector[String] = Source.fromFile("ls-id-labels.txt").getLines.toVector

def makeLabelMap = {
  val tidy = for (label <- lsLabels) yield {
    val columns = label.split("#").toVector
    columns.size match {
      case 2 => {
        val id = columns(0).replaceAll(".+\\.","")
        val txt = columns(1).replaceAll("[_^]","")
        (id, txt)
      }
      case _ => {
        //println("FAILED on " + columns)
        ("","")
      }
    }

  }
  tidy.toVector
}

val labelMap = makeLabelMap

def labelForId(id: String) : String = {
  val idValue = id.replaceAll(".+\\.","")
  val matches = labelMap.filter(_._1 == idValue)
  matches.size match {
    case 1 =>  matches(0)._2
    case 0 => { "" } //println("No match for " + id); ""}
    case _ => { "" } //println("Multiple matches for " + id); ""}

  }

}

/** Read given index file and compute histogram of token occurrences.
*
* @param fName Name of index file.  For use with morphological data, this
* should be one of the files named *Lc.cex (listed above) with all tokens
* converted to lower case only.
*/
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

/** Histogram of tokens in Minkova-Tunberg selections.*/
def mtHisto = histoFromIndex(mtWordList)
/** Histogram of tokens in Livy books 1-6.*/
def l6Histo = histoFromIndex(livy6bksList)
/** Histogram of tokens in all of Livy.*/
def livyHisto = histoFromIndex(livyWordList)


/** Read a string of output in SFST format and find tokens that
* failed to parse.
*
* @param parseString A string of parser output.
*/
def fails(parseString: String) : Vector[String] = {
  parseString.split("\n").filter(_.contains("no result")).toVector.map(s => s.replaceAll("no result for ","")).toVector
}


/** Grouping of a surface token, a Vector of lexical entity identifiers, and a
* corresponding Vector of complete morphological analyses.
*
* @param tkn Surface form (token).
* @param ids Identifiers for lexical entities.  Each identifier corresponds to one
* entry in the analyses Vector.
* @param analyses Full analytical data for analyses.
*/
case class TokenEntityMatches (tkn: String, ids: Vector[String], analyses: Vector[String])



/** From raw parser output, construct a Vector of [[TokenEntityMatches]].
*
* @param parseString A string of parser output.
*/
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


/** Given a group of [[TokenEntityMatches]], compute the mapping of lexical identifiers
*to attested surface forms.
*
* @param teMatches Vector of [[TokenEntityMatches]] to analyze.
*/
def lemmaFormMap(teMatches: Vector[TokenEntityMatches]) : Map[String, Vector[String]]= {
  val pairs = for (tem <- teMatches) yield {
    val pairings = for (lexent <- tem.ids) yield {
      (lexent, tem.tkn)
    }
    pairings
  }
  val grouped = pairs.flatten.groupBy(_._1)
  grouped.map{ case (k,v) => { val slim = v.map(_._2); (k,slim.distinct) } }
}


/** A high-level representation of a lemmatized corpus.
*
* @param lemmaMappins Mappings of lexical entity IDs to surface forms (tokens).
* @param tokenHisto Histogram of token occurrences.
* @param analyticalData Analytical data for each token.
*/
case class LemmatizedCorpus(lemmaMappings: Map[String, Vector[String]], tokenHisto: Vector[(String,Int)], analyticalData : Vector[TokenEntityMatches]) {

  /** Count occurrences of a given token in the corpus.
  *
  * @param tkn Token to count.
  */
  def tokenOccurrences(tkn: String) : Option[Int] = {
    val found = tokenHisto.filter(_._1 == tkn)
    found.size match {
      case 1 => Some(found(0)._2)
      case 0 => {println("No matches found for " + tkn + "."); None}
      case _ => {println("Multiple matches found for " + tkn + "."); None}
    }
  }


  /** Count occurrences of a given lexical entity in the corpus.
  *
  * @id Identifier for the lexical entity to count.
  */
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

  /** Set of lexical entities present in this corpus.*/
  def lexicalEntities: Vector[String] = lemmaMappings.keySet.toSeq.sorted.toVector


  /** Histogram of occurrences of lexical entities in this corpus.*/
  def lemmaHisto = {
    val counts = for (lex <- lexicalEntities) yield {
      (lex,lemmaOccurrences(lex).get )
    }
    counts.toVector.sortBy(_._2).reverse
  }

  def labelledLemmaHisto = {
    val counts = for (lex <- lexicalEntities) yield {
      (lex,labelForId(lex),lemmaOccurrences(lex).get )
    }
    counts.toVector.sortBy(_._3).reverse
  }
}


/** Create a [[LemmatizedCorpus]] object from a raw parser output string and a
* histogram of token occurrences.
*
* @parseString A string of parser output.
* @tokenHisto Histogram of occurrences of each token in the corpus.
*/
def lemmatizedFromParses(parseString: String, tokenHisto: Vector[(String,Int)]) : LemmatizedCorpus = {

  //compute lemmaMappings:
  val teMapping = tokenToEntity(parseString)
  // get map of lemma to forms
  val formMap = lemmaFormMap(teMapping)

  val teMatches = tokenToEntity(parseString)
  //use apropriate token histogram (eg, mtHisto),
  // and create a LemmatizedCorpus
  LemmatizedCorpus(formMap, tokenHisto, teMatches)
}


/** Print some summary information about the data in a string of parser output.
*
* @parseString A String of parser output.
*/
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
