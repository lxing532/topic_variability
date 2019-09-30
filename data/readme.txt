This dataset consists 300 topics from three corpora mentioned in our paper: 
20NG: Topic 0 - 99
Wiki: Topic 100 - 199 
NYT: Topic 200 - 299

They were all manually annotated and we also release their evaluations calculated by other commonly used automatic metrics.

This dataset is in csv format and contains the fields:
========================================================
Id: Topic id
Topic: The top 10 most probable words assigned to each topic.
Human: The average of human rating scores of topics collected from five annotators.
Normed_variability: The new metric "Variability" proposed in the paper. 
Normed_stability: The baseline metric proposed in (Xing and Paul, 2018)
Coherence: The baseline metric proposed in (Mimno et al., 2011)
PMI: The baseline metric proposed in (Newman et al., 2010)
NPMI: The baseline metric proposed in (Lau et al., 2014)
DS: The baseline metric proposed in (Aletras and Stevenson, 2013)
CP: The baseline metric proposed in (Roder et al., 2015)
CV: The baseline metric proposed in (Roder et al., 2015)
========================================================