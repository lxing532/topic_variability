import scipy
import sys
from operator import itemgetter
import numpy as np


topicNum = 50
docNum = 10
stdTheta = []
stdThetaList = []

basename = sys.argv[1]

#######
wordList = []
#######
count = {}
total = {}
countB = {}
flag = 0

doc_topic_stability = {}
for i in range(docNum):
    doc_topic_stability[i] = []

doc_topic_final = {}
for i in range(docNum):
    doc_topic_final[i] = np.zeros(topicNum)

for i in range(1000, 2010, 10):
    
    filename = '%s%d.assign' % (basename, i)
    infile = open(filename, "r")
    docCount = 0
    for line in infile:
        doc_topic_dis = [0 for x in range(topicNum)]
        wordCount = 0
        if line.strip() != '0 0':
            for token in line.split()[2:]:

                parts = token.split(":")
                x = int(parts.pop())
                z = int(parts.pop())
                word = ":".join(parts)
                doc_topic_dis[z] += 1

                wordCount += 1

            doc_topic_final[docCount] += np.array(doc_topic_dis)

            for d in range(len(doc_topic_dis)):
                doc_topic_dis[d] = doc_topic_dis[d]/wordCount

            doc_topic_stability[docCount].append(doc_topic_dis)
            docCount += 1
    print(i)

for k,v in doc_topic_final.items():
    doc_topic_final[k] = doc_topic_final[k]/sum(doc_topic_final[k])

f = open('./topic_vec.txt','w+')
for i in range(docNum):
    tmpp = doc_topic_final[i]
    for j in tmpp:
        f.write(str(j)+' ')
    f.write('\n')
f.close()

for k,v in doc_topic_stability.items():
    doc_topic_stability[k] = np.array(doc_topic_stability[k])
    doc_topic_stability[k] = np.mat(doc_topic_stability[k])

for k,v in doc_topic_stability.items():
    stdTheta.append(v.std(0))

for t in range(docNum):
    
    topics = [];
    norm = []
    cc = 0
    
    for i in range(topicNum):
        thistopic = doc_topic_stability[t][:,i]
        tmp = []
        for j in thistopic:
            tmp.append(np.float64(j[0]))
        tmp = sorted(tmp)
        tmpNormlized = []
        if sum(tmp) == 0:
            norm.append(0)
        else:
            for j in tmp:
                #tmpNormlized.append(j/sum(tmp))
                tmpNormlized.append(j)
            tmpNormlized = np.array(tmpNormlized)
            norm.append(tmpNormlized.std())
    stdThetaList.append(norm)


f = open('./topic_var.txt','w+')
for i in range(len(stdThetaList)):
    tmpp = stdThetaList[i]
    for j in tmpp:
        f.write(str(j)+' ')
    f.write('\n')
f.close()

