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
It will run the model for 2000 iterations (-iters) with 50 topics (-Z), -iters and -Z are arguments for number of iteration and topic number. Input text should put under the data folder. An example input.txt file is provided. Please convert your input text file into the same format.

The recorded gibbs samplers are saved under the data folder in files ending in .assign, which contains the original input but the topic assignment for each word is appended after the word (for example, "student:39" means this word token was assigned to topic 39 in the current iteration of the Gibbs sampler).

Once the sampling process is done, run topwords_posterior_avg.py to get top10 topic words and their probability for each topic:
```
python topwords_posterior_avg.py data/input.txt
```

## Topic Variability Computation
First run the file "topwords_posterior_doc.py" under the folder lda, before running this code, please first change the variable "topicNum" and "docNum" in the file to the same as your data, these two variables are hard coded in the code:
```
python topwords_posterior_doc.py data/input.txt
```
It will generate two files "topic_vec.txt" and "topic_var.txt" under the same folder. topic_vec.txt contains the LDA topic vector for each doc, topic_var.txt contains the corresponding topic distribution variance for each doc.

