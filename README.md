# MetaRecSys

This project was born as part of the following paper:

D. Valcarce, J. Parapar and Á. Barreiro. Combining Top-N Recommenders with Metasearch Algorithms. In *Proceedings of the 40th Annual International ACM SIGIR Conference on Research and Development in Information Retrieval*, SIGIR '17, Tokyo, Japan.

> Given the diversity of recommendation algorithms, choosing one technique is becoming increasingly difficult. In this paper, we explore methods for combining multiple recommendation approaches. We studied rank aggregation methods that have been proposed for the metasearch task (i.e., fusing the outputs of different search engines) but have never been applied to merge top-N recommender systems. These methods require no training data nor parameter tuning. We analysed two families of methods: voting-based and score-based approaches. These rank aggregation techniques yield significant improvements over state-of-the-art top-N recommenders. In particular, score-based methods yielded good results; however, some voting techniques were also competitive without using score information, which may be unavailable in some recommendation scenarios. The studied methods not only improve the state of the art of recommendation algorithms but they are also simple and efficient.

## Instructions

The idea of this project is to combine the output of multiple recommender systems. To this purpose, the output of those recommendation techniques should be stored in a text file following the TREC format.


#### TREC format

This project reads multiple TREC runs and fuse them in one run per fold. The TREC format is as follows:

`user_id \t QO \t item_id \t score \t rank \t description`

where:

`user_id`: user identifier
`QO`: just the constant `Q0`
`item_id`: item identifier
`score`: recommender score for the given user and item
`rank`: rank position of the item in the user recommendations (starting from zero)
`description`: name of the technique (only for documentation purposes)


### Installation

This is a maven project. To compile, go to the root folder of the project:

```bash
$ mvn package
```

This will generate a file the file `metarecsys-1.0.0-jar-with-dependencies.jar` inside the `target` folder.

### Usage

Without any arguments the jar will print the information:
```bash
$ java -jar target/metarecsys-1.0.0-jar-with-dependencies.jar
Missing required options: alg, run, out, norm
usage: metarecsys
 -alg,--algorithm <algorithm_name>   the metarecsys algorithm to use
                                     (borda, condorcet, copeland, combANZ,
                                     combSum, combMNZ)
 -h,--help                           show help
 -max,--max_rank <num>               maximum number of recommended items
                                     per user (100 by default)
 -norm,--normalisation <norm_name>   the normalisation technique to use
                                     (none, standard, sum, zmuv, zmuv1,
                                     zmuv2)
 -out,--output <folder>              path to the output folder
 -run,--runs <folder>                path to the runs folder

```

For example, the following command will run borda and condorcet algorithms without normalisation using the runs found in folder `inputFolder` and writing the results in `outputFolder`:

```bash
$ java -server -jar target/metarecsys-1.0.0-jar-with-dependencies.jar -alg borda -alg condorcet -norm none -run inputFolder -out outputFolder
```


## Acknowledgments

This work has received financial support from the i) *Ministerio de Economía y Competitividad* of the Government of Spain and the ERDF (project TIN2015-64282-R), ii) Xunta de Galicia – *Consellería de Cultura, Educación e Ordenación Universitaria* (project GPC ED431B 2016/035), iii) Xunta de Galicia – *Consellería de Cultura, Educación e Ordenación Universitaria* and the ERDF (*Centro Singular de Investigación de Galicia* accreditation 2016-2019 ED431G/01) and iv) *Ministerio de Educación, Cultura y Deporte* of the Government of Spain (grant FPU014/01724).


## Author

Daniel Valcarce  
daniel [dot] valcarce [at] udc [dot] es  
http://www.dc.fi.udc.es/~dvalcarce  
Information Retrieval Lab  
University of A Coruña  
Spain
