import scala.io.Source

val mtWordList = "mtLexIndexLc.cex"
val livy6bksList = "livy6bksIndexLc.cex"

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
