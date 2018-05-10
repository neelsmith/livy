import edu.holycross.shot.ohco2._

val catalog = "ctscatalog.cex"
val citation = "citationconfig.cex"
val xmldir = "."
val repo = TextRepositorySource.fromFiles(catalog,citation,xmldir)

val cex = repo.cex("#")

import java.io.PrintWriter
new PrintWriter("periochae-xml.cex"){write(cex); close;}
