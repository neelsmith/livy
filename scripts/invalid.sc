/* Convenience script to load library from "editions" directory
*/

import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.latin._

val repo = TextRepositorySource.fromCexFile("editions/livy-omar.cex")


def tokenizeCorpus (c: Corpus, alphabet: LatinAlphabet = Latin24Alphabet) = {
  def tokens = for (n <- c.nodes zipWithIndex) yield {
    LatinTextReader.nodeToTokens(n._1, alphabet)
  }
  tokens.flatten
}

val books = repo.corpus.nodes.map(_.urn.collapsePassageTo(1)).distinct


val invalidByBook = for (bk <- books) yield {
  val book = repo.corpus  ~~ bk
  println("Tokenizing Livy " + bk.passageComponent + "...")
  val tkns = tokenizeCorpus(book)
  val invalid = tkns.filter(_.category == InvalidToken)
  invalid
}
val invalids = invalidByBook.flatten
