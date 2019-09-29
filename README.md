# Topic Variability
This repository maintain the code and datasets for topic variability, which is a novel and effective metric for topic quality evaluation.

## Run LDA
The topic variability is driven from the posterior distributions of Gibbs sampling process. Hence, our LDA implementation will start recording the gibbs samplers every 10 iterations, after 1,000 burn-in iterations. All the code for LDA is under "lda" folder

* Before running LDA, first go into the lda directory and compile with:
```
javac -cp commons-math-2.1.jar *.java
```
And then run with
```
java -cp commons-math-2.1.jar:. LearnTopicModel -model lda -iters 2000 -Z 50 -input data/input.txt -alpha 0.1
```
It will run the model for 2000 iterations (-iters) with 50 topics (-Z). Input text should put under data folder. An example input.txt file is provided. Please convert your input text file into the same format.
