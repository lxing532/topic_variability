import scipy
import sys
from operator import itemgetter
import numpy as np

basename = sys.argv[1]

#######
wordList = []
#######
count = {}
total = {}
countB = {}
flag = 0
for i in np.arange(1000, 2010, 10):
	filename = '%s%d.assign' % (basename, i)
	infile = open(filename, "r")
	
	Z = 0

	#print filename	
	for line in infile:
		#print line
		for token in line.split()[2:]:
			#print token
			parts = token.split(":")
			x = int(parts.pop())
			z = int(parts.pop())
			word = ":".join(parts)
	  		#####
			if flag == 0:
				if word not in wordList:
					wordList.append(word)
	  		#####
			if x == 1:
				if z not in count:
					count[z] = {}
				if word not in count[z]:
					count[z][word] = 0
				count[z][word] += 1

				if z not in total: total[z] = 0
				total[z] += 1

				if z > Z: Z = z
			else:
				if word not in countB:
					countB[word] = 0
				countB[word] += 1

	infile.close()
	Z += 1
	flag = 1
	print(i)

print('finish stage1')

topicWordDict = {}
completeWTDict = [[] for i in range(Z)]
for z in range(Z):
	topicWordDict[z] = []
	print("Topic %d\n" % (z+1))

	for word in count[z]:
		count[z][word] /= float(total[z])
	#####
	for word in wordList:
		if word in count[z].keys():
			completeWTDict[z].append(count[z][word])
		else:
			completeWTDict[z].append(0)
	#####

	w = 0
	words = sorted(count[z].items(), key=itemgetter(1), reverse=True)
	print(len(words))
	for word, v in words:
		topicWordDict[z].append(word)
		print(word, '%0.2f' % v)
		
		w += 1
		if w >= 10: break
		
	print("\n")

	######


	######
'''
f = open('/Users/xinglinzi/Desktop/6_topicModels/11000/4/4_details.txt','w+')
for k,v in topicWordDict.items():
	f.write(str(k)+' ')
	for i in v:
		f.write(i+' ')
	f.write('\n')
f.close()


f = open('/Users/xinglinzi/Desktop/6_topicModels/11000/4/4.txt','w+')
for z in completeWTDict:
	for i in z:
		f.write(str(i)+' ')
	f.write('\n')
f.close()
'''





