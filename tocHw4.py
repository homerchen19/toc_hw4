import re
import sys
import os.path

def validate_inputfile():
	try:
		filename = sys.argv[1]
		if os.path.exists(filename):
			pass
		else:
			raise IOError
	except IndexError:
		print "There is no input file"
		sys.exit(0)
	except IOError:
		print "There is no such file: {0}".format(filename)
		sys.exit(0)
	return filename

def validate_query():
	try:
		query = sys.argv[2]
		if os.path.exists(query):
			pass
	except IndexError:
		print "There is no query"
		sys.exit(0)

valid_infile = validate_inputfile()
infile = open(valid_infile, 'r')
validate_query()
query = sys.argv[2]

nofound1, nofound2 = 0, 0
typeNum = {}

def findtype(list_tmp):
	global nofound1, nofound2, query
	global typeNum
	for item in list_tmp: #item = each elements in list_href. Its type is string
		if(item.find(query) < 0): # if item doesn't contain query
			item = re.findall('[.]([\w]+)[\?|"]', item) #parse the last type
			if item:
				nofound2 = 1;
				tmp = ''.join(item)
				#print tmp
				if tmp in typeNum:
					typeNum[tmp] += 1
				else:
					typeNum[tmp] = 1
				#print typeNum
for line in infile:
	url = re.findall('"WARC-Target-URI":"([^"]*)"', line) 
	str_url = ''.join(url)

	if (str_url.find(query) >= 0): # compare web pages & query
		nofound1 = 1

		links = re.findall('"Links":(\[(\{([^\}])*\})(\,\{[^\}]*\})*([^\]]*)\])', line)
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