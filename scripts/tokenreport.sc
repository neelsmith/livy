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

case class Report(urn: CtsUrn, nodeCount: Int,  lexicalCount: Int, praenomenCount: Int, numericCount: Int, punctCount: Int, invalidCount: Int)

val reports = for (bk <- books) yield {
  val book = repo.corpus  ~~ bk
  val tkns = tokenizeCorpus(book)

  val lex = tkns.filter(_.category == LexicalToken)
  val praenomina = tkns.filter(_.category == Praenomen)
  val numerics = tkns.filter(_.category == NumericToken)
  val punct = tkns.filter(_.category == Punctuation)
  val invalid = tkns.filter(_.category == InvalidToken)
  Report(bk,book.size,lex.size, praenomina.size, numerics.size, punct.size, invalid.size)
}

println("TOTALS:")
println(s"${reports.size} books, ${reports.map(_.nodeCount).sum} citable nodes")
println("Token distribution: ")
println("\tlexical: " + reports.map(_.lexicalCount).sum)
println("\tpraenomina: " + reports.map(_.praenomenCount).sum)
println("\tnumerics: " + reports.map(_.numericCount).sum)
println("\tpunctuation: " + reports.map(_.punctCount).sum)
println("\tinvalid: " + reports.map(_.invalidCount).sum)
