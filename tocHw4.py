# coding=UTF-8
'''
學號:F74016297
系級:資訊105甲班
姓名:陳自泓
程式簡單說明:
	一開始先做輸入檔案與topk的確認，
	分別是(1)verify_inputfile跟(2)verify_topk 這兩個function
	接著一行一行讀檔案，
	然後依序用regux抓"WARC-Target-URI","Links",
	抓"href"後去跑findtype(),
	是為了抓附檔名然後用一個dictionary去存附檔名的名稱跟其相對數量，
	抓"url"也是做一樣的動作，
	最後print出附檔名和相對的數量即可
'''
import re
import sys
import os.path
#import time
#tStart = time.time()

def verify_inputfile():
	if len(sys.argv) < 2:
		print "There is no input file and query !"
		sys.exit(0)
	inputfile = sys.argv[1]
	if os.path.exists(inputfile):
		pass
	elif not os.path.exists(inputfile):
		print "There is no " + inputfile
		sys.exit(0)
	else:
		print "There is no input file !"
		sys.exit(0)
		
	return inputfile

def verify_query():
	if len(sys.argv) > 2:
		query = sys.argv[2]
	else:
		print "There is no query"
		sys.exit(0)
	return query

valid_infile = verify_inputfile()
infile = open(valid_infile, 'r')
query = verify_query()

nofound1, nofound2 = 0, 0
typeNum = {}
lineNum = 0
def findtype(list_tmp):
	global nofound1, nofound2, query
	global typeNum, lineNum
	for item in list_tmp: #item = each elements in list_href. Its type is string
		#print item
		find_error = re.findall('/', item)
		if(len(find_error) != 0):
			if(item.find(query) < 0): # if item doesn't contain query
				item2 = re.search('[\?]', item)
				if (item2 != None):
					item2 = re.search('[\w]+[\.]([^\.^\/]+)[\?]', item)
					if (item2 != None):
						nofound2 = 1;
						tmp = item2.group(1)
						#print "line = "+ str(lineNum) + " tmp?? = " + tmp;
			
						if tmp in typeNum:
							typeNum[tmp] += 1
						else:
							typeNum[tmp] = 1
				else:
					item2 = re.search('\.([^\.^\/]+)[?:\?|"]', item) #parse the last type
					
					if (item2 != None):
						nofound2 = 1;
						tmp = item2.group(1)

						#print "item = " + item
						#print "line = "+ str(lineNum) + " tmp = " + tmp
			
						if tmp in typeNum:
							typeNum[tmp] += 1
						else:
							typeNum[tmp] = 1
						#print typeNum


for line in infile:
	lineNum+=1;
	'''url = re.findall('"WARC-Target-URI":"([^"]*)"', line) 
	str_url = ''.join(url)'''
	url = re.search('"WARC-Target-URI":"([^"]*)"', line) 
	if(url != None):
		str_url = url.group(1)

	if (str_url.find(query) >= 0): # compare web pages & query
		nofound1 = 1

		links = re.findall('"Links":\[(.+)\](,"Head"|\},"Entity-Digest")', line)
		#print "len = " + str(len(links))
		str_links = ''.join(str(i) for i in links)

		list_href = re.findall('"href":+\"http[s]?://([^"]*\")', str_links) #parse 'http(s):'
		findtype(list_href)

		list_url = re.findall('"url":+\"http[s]?://([^"]*\")', str_links)
		findtype(list_url)

if nofound1 == 0:
	print'Page not found!'
elif nofound2 == 0:
	print'Type not found!'
else:
	for x in typeNum:
		print x, ':' ,typeNum[x]

#tEnd = time.time()
#print "It cost %f sec" % (tEnd - tStart)