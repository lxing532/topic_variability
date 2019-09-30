import numpy as np


topic_number = 5;  #change to your setting !!


var_sum = []; count = 0;
final_vec = []
for line in open('lda/topic_vec.txt'):
	l = line.strip().split(' ')
	tmp = [float(i) for i in l]
	norm_tmp = tmp
	final_vec.append(norm_tmp)

doc_num = len(final_vec)

doc_c = 0; spec = [[]]*topic_number
for line in open('lda/topic_var.txt'):
	l = line.strip().split(' ')
	l_float = [float(i) for i in l]

	l_float_new = l_float
	for k in range(topic_number):
		if final_vec[doc_c][k] == 0:
			continue
		else:
			spec[k] = spec[k] + [l_float_new[k]/final_vec[doc_c][k]]
	doc_c += 1

doc_coherence = [np.std(np.array(i)) for i in spec]


norm_doc = [(i-min(doc_coherence))/(max(doc_coherence)-min(doc_coherence)) for i in doc_coherence]

f = open('topic_variability.txt','w+')
for i in range(len(norm_doc)):
	f.write('Topic '+str(i)+': '+ str(norm_doc[i]))
	f.write('\n')
f.close()


