import scala.io.Source
import java.io.PrintWriter

val editionFile = "editions/livy-omar.cex"
val encliticQueFile = "data/enclitic-lists/livy-encl-que.txt"
val encliticVeFile = "data/enclitic-lists/livy-encl-ve.txt"
val encliticNeFile = "data/enclitic-lists/livy-encl-ne.txt"


def hyphenate(enclitic: String, re: scala.util.matching.Regex, wordList: Vector[String], corpus: String): String = {
  if (wordList.isEmpty) {
    corpus
  } else {

    val lcPattern = re.replaceFirstIn(wordList.head, enclitic)
    val lcCorpus = corpus.replaceAll("([^a-zA-Z])" + wordList.head + "([^a-zA-Z])", "$1" + lcPattern + "$2")

    println(s"\nReplace ${wordList.head} with ${lcPattern}...")

    val ucPattern = re.replaceFirstIn(wordList.head.capitalize, enclitic)
    val newCorpus = lcCorpus.replaceAll("([^a-zA-Z])" + wordList.head.capitalize + "([^a-zA-Z])","$1" + ucPattern + "$2")

    println(s"\nReplace ${wordList.head.capitalize} with ${ucPattern}...")
    println("and recurse on list.")
    hyphenate(enclitic, re, wordList.tail,  newCorpus )
  }
}



def hyphenateAll: String = {
  val src = Source.fromFile(editionFile).getLines.mkString("\n")

  val ques = Source.fromFile(encliticQueFile).getLines.toVector
  val queStripped = hyphenate("-que", "que$".r, ques, src)

  val ves = Source.fromFile(encliticVeFile).getLines.toVector
  val veStripped = hyphenate("-ve", "ve$".r, ves, queStripped)

  val nes = Source.fromFile(encliticNeFile).getLines.toVector
  val allRemoved = hyphenate("-ne", "ne$".r, nes, veStripped)

  allRemoved
}

def writeCorpus(f: String = "livy-enclitics-omar.cex") = {
  val txt = hyphenateAll
  new PrintWriter(f){write (txt); close;}
}
