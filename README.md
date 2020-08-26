# KSummaries

## Introduction

A Kotlin library for mergeable summarizing data structures.

## List of Algorithms implemented

### Set

[Bloom Filter](https://en.wikipedia.org/wiki/Bloom_filter) for set membership

Fingerprints for set equality

HyperLogLog for set cardinality

### Item Frequency Estimation

[Misra-Gries](https://people.csail.mit.edu/rrw/6.045-2019/encalgs-mg.pdf)

SpaceSaving

[Count-Min Sketch](https://en.wikipedia.org/wiki/Count–min_sketch)

[Count Sketch](https://courses.cs.washington.edu/courses/cse522/14sp/lectures/lect05.pdf)

### L_p Norm Estimation

[Reservoir Sampling](https://en.wikipedia.org/wiki/Reservoir_sampling)

AMS Sketch for L_2 norm

L_p Sketch 

### Sampling

Sparse Vector Recovery

Distinct Sampling / L_0 Sampling

L_p Sampling

## Implementation Detail

All data structures have at least three operations: update, query and merge.

Tests are provided in "src/test".
