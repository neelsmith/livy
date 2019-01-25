`cex` files in this directory should begin with this header line:

    StemUrn#LexicalEntity#StemString#MorphologicalClass

Subsequent lines should have four columns matching the header.  Data in the four columns should look like this example:

    plinymorph.verb1#ls.n2280#am#conj1

The `StemUrn` column is an abbreviation that is automatically expanded to a full URN when it is proposed. It has the name of a collection of morphologial stems (here, `plinymorph`) and a an identifier for this stem (here, `verb1`).

The `LexicalEntity` column is a similar abbreviation for a URN identifying a lexical item.  The collection `ls` is predefined for lexical entities that appear in Lewis-Short's Latin lexicon.  The specific identifier should be drawn from data in the `lewis-short`  directory of the `tabulae` repository.

The `StemString` is the stem to which endings are added, here `am`.

The `MorphologicalClass` is one of the values defined by `tabulae` for this type of analysis.  Possible morphological classes are listed in the `cex` directory of the `tabulae` repository.
