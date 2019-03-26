# About the editions and data sets in this repository

Data sets in this directory are derived from CEX editions in the `editions` directory of this repository.


`enclitic-lists`:  files in this directory list occurrences of enclitic *ne*, *que* and *ve* in the edition of Livy in `editions/livy-omar.cex`.  These files are used by the functions in `scripts/mark-enclitics.sc` to generate a new CEX corpus with enclitic boundaries explicitly marked.  The resulting edition is written to `editions/livy-omar-enclitics.cex`.

`livyLexIndex.cex`:  a two-column file giving CTS URN and string value for every lexical token in `editions/livy-omar-enclitics.cex`.

`livyLexIndexLc.cex`:  a two-column file giving CTS URN and string value for every lexical token in `editions/livy-omar-enclitics.cex` with string values in lower case only.



`mtLexIndex.cex`:  a two-column file giving CTS URN and string value for every lexical token in selections in Minkova and Tunberg's textbook.

`livyWordListLc.txt`:  unique string values found in `LivyLexIndexLc.cex` (suitable for morphological parsing)

`mtWordList.txt`:  unique string values found in `mtLexIndex.cex`
