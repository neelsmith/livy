# About the editions and data sets in this repository

Data sets in this directory are derived from CEX editions in the `editions` directory of this repository.


## Identifying enclitics

`enclitic-lists`:  files in this directory list occurrences of enclitic *ne*, *que* and *ve* in the edition of Livy in `editions/livy-omar.cex`.  All tokens ending in one of these strings were collected, and manually scanned for false positives (e.g., *itaque* is treated as a single token, not as *ita* with enclitic *que*).

These files are used by the functions in `scripts/mark-enclitics.sc` to generate a new CEX corpus with enclitic boundaries explicitly marked.  The resulting edition is written to `editions/livy-omar-enclitics.cex`.



## Livy

`livyLexIndex.cex`:  a two-column file giving CTS URN and string value for every lexical token in `editions/livy-omar-enclitics.cex`.

`livyLexIndexLc.cex`:  a two-column file giving CTS URN and string value for every lexical token in `editions/livy-omar-enclitics.cex` with string values in lower case only.

`livyWordListLc.txt`:  unique string values found in `LivyLexIndexLc.cex` (suitable for morphological parsing)

## Selections from Livy in Minkova-Tunberg textbook

`mtLexIndex.cex`:  a two-column file giving CTS URN and string value for every lexical token in selections in Minkova and Tunberg's textbook.

`mtLexIndexLc.cex`:  a two-column file giving CTS URN and string value for every lexical token in selections in Minkova and Tunberg's textbook with string values in lower case only.

`mtWordListLc.txt`:  unique string values found in `mtLexIndexLc.cex`
