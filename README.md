# Topic Variability
This repository maintain the code and datasets for topic variability, which is a novel and effective metric for topic quality evaluation.

## Run LDA
The topic variability is driven from the posterior distributions of Gibbs sampling process. Hence, our LDA implementation will start recording the gibbs samplers every 10 iterations, after 1,000 burn-in iterations. All the code for LDA is under "lda" folder

* Before running LDA, first go into the lda directory and:
```
javac -cp commons-math-2.1.jar *.java
```

