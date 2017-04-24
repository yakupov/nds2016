# Introduction
This repository contains implementations of various non-dominated sorting algorithms:
* "A Provably Asymptotically Fast Version of the Generalized Jensen Algorithm for Non-dominated Sorting" (Buzdalov et al, 2014);
* ENLU (Li et al.);
* Cartesian tree-based data structure, that maintains a correct set of non-domination layers in two-dimensional case (Yakupov, Buzdalov, 2015);
* A new (Inspired by the Buzdalov/Jensen sorter and ENLU) efficient non-dominated sorting algorithm (Yakupov, Buzdalov, 2017).

This repository also contains benchmarks, whose results are used in the article "Improved Incremental Non-dominated Sorting for Steady-State Evolutionary Multiobjective Optimization".

# Artifacts
## NDSUtils
Helper library, used for obtaining, storage and processing of test data.

## 2015-gecco-nsga-ii-ss
Original implementation of the Buzdalov's sorter from 2014 (used to test the correctness and the performance of the new implementation).

## NSGA2Tests
A test code, that was used to collect test data sets for non-dominated sorting from the MOEA's implementation of NSGA-II.

## IncrementalPPSN
Implementations of the new NDS.
Specifically:
* ru.itmo.nds.PPSN2014 - re-implementated Buzdalov's sorter (mentioned in the article as DC1);
* ru.itmo.nds.IncrementalPPSN - improvement of the PPSN2014 with provable O(N) worst case running time complexity of Sweep procedures (DC2);
* package ru.itmo.nds.layers_ppsn - IncrementalPPSN on a data set, that is split by rank (Level).

## PPSN2016Test
This project contains benchmarks, whose results were used in the article. These benchmarks utilize the JMH framework (http://openjdk.java.net/projects/code-tools/jmh/).
This project also contains reference implementations of ENLU and Cartesian tree-based 2D sorter, which are used for performance comparison:
* ru.itmo.nds.reference.ENLUSorter;
* ru.itmo.nds.reference.treap2015.TreapPopulation.

# How to build
We recommend to build these artifacts using Apache Maven.
In short, you should:
* Install Maven;
* Checkout this repository;
* Execute 'mvn install' in the directory 'NDSUtils';
* Execute 'mvn install' in the directory 'IncrementalPPSN';
* Execute 'mvn install' in the directory 'PPSN2016Test'.

As a result, in the 'target' subdirectory of PPSN2016Test a file named 'benchmarks.jar' will be created.
In order to execute benchmarks, you should execute this jar using Java (e.g. java -jar benchmarks.jar).