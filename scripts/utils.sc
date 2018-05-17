// Convenience functions.
//

import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.latin._
import java.io.PrintWriter

// Load OMAR edition of Livy, get URNs for each book, so that
// when tokenizing the whole thing, we can easily break the task
// down into manageable sized chunks.
val repo = TextRepositorySource.fromCexFile("editions/livy-omar.cex")
val books = repo.corpus.nodes.map(_.urn.collapsePassageTo(1)).distinct

// Tokenize one book at a time to avoid exhausting RAM,
// then flatten result into a single Vector of LatinToken objects.
def tokenize(corpus: Corpus, units: Vector[CtsUrn]) :  Vector[LatinToken]= {
    val tokensByChunk = for (chunkUrn <- units) yield {
      val chunk = repo.corpus  ~~ chunkUrn
      println("Tokenizing section " + chunkUrn.passageComponent + "...")
      LatinTextReader.corpusToTokens(chunk, Latin24Alphabet)
    }
    tokensByChunk.flatten
}

// Tokenize a corpus by book.
def tokens:  Vector[LatinToken] = tokenize(repo.corpus, books)

// Write complete tokenization to a file in CEX format.
def textIndex(tkns:  Vector[LatinToken]): Unit = {
  val cex = tkns.map(_.cex)
  new PrintWriter("tokenIndex.cex"){ write(cex.mkString("\n")); close;}
}


// Compute histogram of tokens by category.  If
// LatinLexicalCategory is None, compute for all tokens.
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
def histoIndex(histo: Map[String, Int], fName: String) : Unit =  {
  val lines = hist.map{ case (str, count) => s"${str}#${count}"}
  new PrintWriter(fName){ write(lines.mkString("\n")); close;}
}
