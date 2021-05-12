# KSummaries

## Introduction

A Kotlin library for mergeable summarizing data structures.

## List of Algorithms implemented

### Set

- [Bloom Filter](https://en.wikipedia.org/wiki/Bloom_filter) for set membership

- Fingerprints for set equality

- HyperLogLog for set cardinality

### Item Frequency Estimation

- [Misra-Gries](https://people.csail.mit.edu/rrw/6.045-2019/encalgs-mg.pdf)

- SpaceSaving

- [Count-Min Sketch](https://en.wikipedia.org/wiki/Count–min_sketch)

- [Count Sketch](https://courses.cs.washington.edu/courses/cse522/14sp/lectures/lect05.pdf)

### L_p Norm Estimation

- [AMS Sketch for L_2 norm](http://dimacs.rutgers.edu/~graham/pubs/papers/encalgs-ams.pdf)

- L_p Sketch 

### Sampling

- [Reservoir Sampling](https://en.wikipedia.org/wiki/Reservoir_sampling)

- Sparse Vector Recovery

- Distinct Sampling / L_0 Sampling

- L_p Sampling

## Graph 

- [Graph Connectivity](https://people.cs.umass.edu/~mcgregor/papers/13-graphsurvey.pdf)
  
- [Graph Bipartiteness](https://people.cs.umass.edu/~mcgregor/papers/13-graphsurvey.pdf)

- [Graph k-connectivity](http://web.stanford.edu/class/cs369g/files/lectures/lec13.pdf)
  
- [Graph Min Cut](http://web.stanford.edu/class/cs369g/files/lectures/lec13.pdf)

## Implementation Detail

All data structures have at least three operations: update, query and merge.

Tests are provided in "src/test".

Test datasets are from 
[Frequent Itemset Mining Dataset](http://fimi.uantwerpen.be/data/) 
and [CAIDA Anonymized Internet Traces Dataset](https://www.caida.org/data/passive/passive_dataset.xml).

## Contributors

- Nikolai Karpov

- Yan Song <songyan@iu.edu>

- [Qin Zhang](http://homes.sice.indiana.edu/qzhangcs/) (Principal Investigator)
