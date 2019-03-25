import scala.io.Source
import java.io.PrintWriter

val editionFile = "editions/livy-omar.cex"
val src = Source.fromFile(editionFile).getLines.mkString("\n")

val encliticQueFile = "data/livy-encl-que.txt"
val ques = Source.fromFile(encliticQueFile).getLines.toVector

val encliticVeFile = "data/livy-encl-ve.txt"
val ves = Source.fromFile(encliticVeFile).getLines.toVector

val encliticNeFile = "data/livy-encl-ne.txt"
val nes = Source.fromFile(encliticNeFile).getLines.toVector


val queRe = "que$".r


def hyphenateQue(wordList: Vector[String] = ques, corpus: String = src): String = {
  if (wordList.isEmpty) {
    corpus
  } else {
    val replacementVal = queRe.replaceFirstIn(" " + wordList.head, "-que")
    println(s"\nReplace ${wordList.head} with ${replacementVal}...")
    println("and recurse on list.")
    hyphenateQue(wordList.tail, corpus.replaceAll(wordList.head, replacementVal))
  }
}


val veRe = "ve$".r
def hyphenateVe(wordList: Vector[String] = ves, corpus: String = src): String = {
  if (wordList.isEmpty) {
    corpus
  } else {

    val replacementVal = veRe.replaceFirstIn(" " + wordList.head, "-ve")
    println(s"\nReplace ${wordList.head} with ${replacementVal}...")
    println("and recurse on list.")
    hyphenateQue(wordList.tail, corpus.replaceAll(wordList.head, replacementVal))
  }
}


val neRe = "ne$".r
def hyphenateNe(wordList: Vector[String] = nes, corpus: String = src): String = {
  if (wordList.isEmpty) {
    corpus
  } else {
    val replacementVal = neRe.replaceFirstIn(" " + wordList.head, "-ve")
    println(s"\nReplace ${wordList.head} with ${replacementVal}...")
    println("and recurse on list.")
    hyphenateQue(wordList.tail, corpus.replaceAll(wordList.head, replacementVal))
  }
}

def hyphenateAll: String = {
  val stripQue = hyphenateQue()
  val stripVe = hyphenateNe(corpus = stripQue)
  val allRemoved = hyphenateNe(corpus = stripVe)

  allRemoved
}
