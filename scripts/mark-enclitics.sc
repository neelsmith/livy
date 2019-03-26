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

    val lcPattern = queRe.replaceFirstIn(" " + wordList.head, "-que")
    val lcCorpus = corpus.replaceAll(" " + wordList.head, lcPattern)

    println(s"\nQue: replace ${wordList.head} with ${lcPattern}...")

    val ucPattern = queRe.replaceFirstIn(" " + wordList.head.capitalize, "-que")
    val newCorpus = lcCorpus.replaceAll(" " + wordList.head.capitalize,ucPattern)

    println(s"\nReplace ${wordList.head.capitalize} with ${ucPattern}...")
    println("and recurse on list.")
    hyphenateQue(wordList.tail,  newCorpus )
  }
}


val veRe = "ve$".r
def hyphenateVe(wordList: Vector[String] = ves, corpus: String = src): String = {
  if (wordList.isEmpty) {
    corpus
  } else {

    val lcPattern = queRe.replaceFirstIn(" " + wordList.head, "-ve")
    val lcCorpus = corpus.replaceAll(" " + wordList.head, lcPattern)

    println(s"\nVe: replace ${wordList.head} with ${lcPattern}...")

    val ucPattern = queRe.replaceFirstIn(" " + wordList.head.capitalize, "-ve")
    val newCorpus = lcCorpus.replaceAll(" " + wordList.head.capitalize,ucPattern)
    println(s"\nReplace ${wordList.head.capitalize} with ${ucPattern}...")
    println("and recurse on list.")

    hyphenateVe(wordList.tail,  newCorpus )
/*
    val replacementVal = veRe.replaceFirstIn(" " + wordList.head, "-ve")
    println(s"\nReplace ${wordList.head} with ${replacementVal}...")
    println("and recurse on list.")
    hyphenateVe(wordList.tail, corpus.replaceAll(" " + wordList.head, replacementVal))
    */
  }
}


val neRe = "ne$".r
def hyphenateNe(wordList: Vector[String] = nes, corpus: String = src): String = {
  if (wordList.isEmpty) {
    corpus
  } else {
    val lcPattern = queRe.replaceFirstIn(" " + wordList.head, "-ne")
    val lcCorpus = corpus.replaceAll(" " + wordList.head, lcPattern)
    println(s"\nNe: replace ${wordList.head} with ${lcPattern}...")

    val ucPattern = queRe.replaceFirstIn(" " + wordList.head.capitalize, "-ne")
    val newCorpus = lcCorpus.replaceAll(" " + wordList.head.capitalize,ucPattern)
    println(s"\nReplace ${wordList.head.capitalize} with ${ucPattern}...")
    println("and recurse on list.")
    hyphenateNe(wordList.tail,  newCorpus )
    /*
    val replacementVal = neRe.replaceFirstIn(" " + wordList.head, "-ne")
    println(s"\nReplace ${wordList.head} with ${replacementVal}...")
    println("and recurse on list.")
    hyphenateNe(wordList.tail, corpus.replaceAll(" " + wordList.head, replacementVal))*/
  }
}

def hyphenateAll: String = {
  val queStripped = hyphenateQue()
  val veStripped = hyphenateVe(corpus = queStripped)
  val allRemoved = hyphenateNe(corpus = veStripped)

  allRemoved
}

def writeCorpus(f: String = "livy-enclitics-omar.cex") = {
  val txt = hyphenateAll
  new PrintWriter(f){write (txt); close;}
}
