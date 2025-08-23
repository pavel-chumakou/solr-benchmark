# Solr Benchmark
Solr performance benchmark tool.
## Test data
3150 text documents (Wikipedia articles) containing 6 text fields.
wikitest-docs-1000.json size is about 10.2 Mb.

## Test queries
120 Boolean OR queries containing 3 predefined words, defType=edismax, qf contains all 6 text fields, q.op=OR

All test queries are executed consequentially, one after another, without delays.  Performance test is executed 10 times, result outputs to console.
Query time is taken from Solr output ***QTime*** parameter (responseHeader section).
## How to run Solr benchmark
1. Download and extract Solr binaries: https://archive.apache.org/dist/solr/solr/

2. Create **wikitetest** collection using prvided Solr configs [wikitest](https://github.com/pavel-chumakou/solr-benchmark/tree/main/configs/wikitest)

3. Run Solr:
```console
$ SOLR/bin/solr start -f
```
4. Run Solr benchmark script:
```console
$ java SolrBenchamrk.java
```
## Usage
```
java SolrBenchmark.java <OPTIONS>
OPTIONS:
    -h
        Print this message
    -u <baseUrl>
        Use the specified URL, for example:  http://localhost:8983/solr/wikitest/select?q.op=OR&q=
    -d <testDocsPath>
        Path to the test data file
    -a
        Add documents to collection (refeed)
    -r
        Run test queries
```

## Test results


***Solr 8.11.4***

Search time, ms: 566 245 238 254 241 248 238 205 259 306 139 161 150 146 148 156 134 160 168 143

***Solr 9.7.0***

Search time, ms: 982 511 494 461 444 387 373 363 351 334 318 350 340 324 299 318 312 343 308 288

***Solr 9.8.0***

Search time, ms: 918 617 489 441 461 365 364 363 343 339 308 334 334 352 341 334 296 319 311 290

***Solr 9.9.0***

Search time, ms: 596 226 254 263 239 189 174 186 163 171 126 124 136 134 104 112 97 105 95 110


