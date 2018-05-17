// Convenience functions for working with digital texts
// in this repository.

import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.latin._
import java.io.PrintWriter

// Load OMAR edition of Livy, get URNs for each book, so that
// when tokenizing the whole thing, we can easily break the task
// down into manageable sized chunks.
val repo = TextRepositorySource.fromCexFile("editions/livy-omar.cex")
val books = repo.corpus.nodes.map(_.urn.collapsePassageTo(1)).distinct

// Tokenize a corpus one unit at a time to avoid exhausting RAM,
// then flatten result into a single Vector of LatinToken objects.
def tokenize(corpus: Corpus, units: Vector[CtsUrn] = Vector.empty[CtsUrn], unitLabel: String = "book") :  Vector[LatinToken]= {

  if (units.isEmpty) {
    LatinTextReader.corpusToTokens(corpus, Latin24Alphabet)
  } else {
    val tokensByChunk = for (chunkUrn <- units) yield {
      val chunk = repo.corpus  ~~ chunkUrn
      println(s"Tokenizing ${unitLabel} " + chunkUrn.passageComponent + "...")
      LatinTextReader.corpusToTokens(chunk, Latin24Alphabet)
    }
    tokensByChunk.flatten
  }
}

// Tokenize our Livy corpus by book.
def tokens:  Vector[LatinToken] = tokenize(repo.corpus, books)

// Write complete tokenization to a file in CEX format.
def textIndex(tkns:  Vector[LatinToken], fName: String = "tokenIndex.cex"): Unit = {
  val cex = tkns.map(_.cex)
  new PrintWriter(fName){ write(cex.mkString("\n")); close;}
}


// Compute histogram of tokens by category.  If
// lexicalCategory is None, compute for all tokens.
def histo(tkns: Vector[LatinToken], lexicalCategory: Option[LatinLexicalCategory] = Some(LexicalToken)) : Map[String, Int] = {
  lexicalCategory match {
    case None => {
      val txt = tkns.map(_.text)
      txt.groupBy(s => s).map { m => (m._1,m._2.size)}.toSeq.sortBy(_._2).reverse.toMap
    }
    case cat: Option[LatinLexicalCategory] => {
      val txt = tkns.filter(_.category == cat.get).map(_.text)
      txt.groupBy(s => s).map { m => (m._1,m._2.size)}.toSeq.sortBy(_._2).reverse.toMap
    }
  }
}

// Write complete histogram to a file in CEX format
def histoIndex(histo: Map[String, Int], fName: String = "tokensHistogram.cex") : Unit =  {
  val lines = histo.map{ case (str, count) => s"${str}#${count}"}
  new PrintWriter(fName){ write(lines.mkString("\n")); close;}
}

// Screen dump summarizing contents of vector of LatinToken objects.
def corpusOverview(corpus: Corpus, sections: Vector[CtsUrn] = Vector.empty): Unit = {
  val tkns = tokenize(corpus, sections)

  val books = repo.corpus.nodes.map(_.urn.collapsePassageTo(1)).distinct
  println("TOTALS:")
  println(s"${corpus.size} citable nodes.")
  println(s"${tkns.size} tokens.")
  println("Token distribution: ")
  println("\tlexical: " + tkns.filter(_.category == LexicalToken).size)
  println("\tpraenomina: " + tkns.filter(_.category == Praenomen).size)
  println("\tnumerics: " +  tkns.filter(_.category == NumericToken).size)
  println("\tpunctuation: " +  tkns.filter(_.category == Punctuation).size)
  println("\tinvalid: " +  tkns.filter(_.category == InvalidToken).size)
}

// write out reports as CEX files:
// 1.  index of tokens
// 2.  histogram of tokens
//
def reports(corpus: Corpus, sections: Vector[CtsUrn] = Vector.empty): Unit = {
  val tkns = tokenize(corpus, sections)
  println("Indexing tokens...")
  textIndex(tkns)
  println("Computing histogram...")
  histoIndex( histo(tkns, None))
  println("Done.")
}
