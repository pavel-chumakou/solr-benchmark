# Solr Benchmark
Solr search performance benchmark tool.
## Test collection
3150 text documents (Wikipedia articles) containing 6 text fields (title, keys, summary, extractor, text, body).

All text fields have the same fieldType:
~~~
<fieldType class="solr.TextField" name="text_en" positionIncrementGap="100" omitNorms="true" autoGeneratePhraseQueries="true">
    <analyzer type="index">
        <charFilter class="solr.HTMLStripCharFilterFactory" />
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <filter class="solr.LowerCaseFilterFactory" />
        <filter class="solr.ASCIIFoldingFilterFactory" />
    </analyzer>
    <analyzer type="query">
        <tokenizer class="solr.WhitespaceTokenizerFactory" />
        <filter class="solr.LowerCaseFilterFactory" />
    </analyzer>
</fieldType>
~~~
Test collection size is 15 MB. Test collection is compatible with Solr 9.6.1 (and higher versions).
## Test queries
270 Boolean OR queries containing 3 predefined words, defType=edismax, qf=title keys summary extractor text body (all 6 text fields), q.op=OR

All test queries are executed consequentially, one after another, without delays.  Performance test is executed 20 times, result outputs to console.
Query time is taken from Solr output ***QTime*** parameter (responseHeader section).
## How to run Solr benchmark
Download and extract Solr binaries: https://archive.apache.org/dist/solr/solr/

Download test collection wikitest.zip and extract to ***SOLR/server/solr*** folder

Run Solr:
```console
$ SOLR/bin/solr start -f
```
Download Solr benchmark script: RunSolrQuery.java

Run Solr benchmark script:
```console
$ java RunSolrQuery.java
```

## Test results
***Solr 9.6.1***

Search time, ms: 1564 694 554 447 474 404 359 352 453 475 524 413 560 395 432 403 397 353 367 487

***Solr 9.7.0***

Search time, ms: 2453 1650 1447 1593 1527 1380 1335 1293 1454 1341 1747 1753 1648 1383 1296 1246 1279 1238 1205 1396

***Solr 9.8***

Search time, ms: 2764 2282 2182 2259 2129 2586 2832 2185 1967 2074 2987 1816 1772 1709 1843 1519 1754 1627 1513 1652


