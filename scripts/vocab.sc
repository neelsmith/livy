/* Convenience script to load library from "editions" directory
*/

import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import edu.holycross.shot.latin._

val repo = TextRepositorySource.fromCexFile("editions/livy-omar.cex")


def tokenizeCorpus (c: Corpus, alphabet: LatinAlphabet = Latin24Alphabet) = {
  def tokens = for (n <- c.nodes zipWithIndex) yield {
    //println(s"${n._2}}. TOKENIZE:")
    //println("\t" + n + "\n")
    LatinTextReader.nodeToTokens(n._1, alphabet)
  }
  tokens.flatten
}

val books = repo.corpus.nodes.map(_.urn.collapsePassageTo(1)).distinct


val lexicalByBook = for (bk <- books) yield {
  val book = repo.corpus  ~~ bk
  println("tokenizing " + bk.passageComponent)
  val tkns = tokenizeCorpus(book)
  tkns.filter(_.category == LexicalToken).map(_.text).distinct
}

val lex = lexicalByBook.flatten.distinct

import java.io.PrintWriter
