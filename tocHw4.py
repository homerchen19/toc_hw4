import re
import sys
#coding:utf-8

infile = open(sys.argv[1], 'r')
query = sys.argv[2]

nofound1, nofound2 = 0, 0
num_jpg, num_html, num_php, num_gif, num_png, num_css, num_js = 0, 0, 0, 0, 0, 0, 0

def findtype(list_tmp):
	global nofound1, nofound2, query
	global num_jpg, num_html, num_php, num_gif, num_png, num_css, num_js
	for item in list_tmp: #item = each elements in list_href. Its type is string
		if(item.find(query) < 0): # if item doesn't contain query
			#print 'item___1 : '  + item
			item = re.findall('(.)(jpg|html|php|gif|png|css|js)"', item) #parse the last type
			item = str(item)
			#print 'item___2 : '  + item
			jpg = re.findall('jpg', item)
			if(len(jpg) != 0):
				#print 'item : '  + item
				nofound2 = 1
				num_jpg += 1
				continue
			html = re.findall('html', item)
			if(len(html) != 0):
				nofound2 = 1
				num_html += 1
				continue
			php = re.findall('php', item)
			if(len(php) != 0):
				nofound2 = 1
				num_php += 1
				continue
			gif = re.findall('gif', item)
			if(len(gif) != 0):
				nofound2 = 1
				num_gif += 1
				continue
			png = re.findall('png', item)
			if(len(png) != 0):
				nofound2 = 1
				num_png += 1
				continue
			css = re.findall('css', item)
			if(len(css) != 0):
				nofound2 = 1
				num_css += 1
				continue
			js = re.findall('js', item)
			if(len(js) != 0):
				nofound2 = 1
				num_js += 1
				continue

for line in infile:
	url = re.findall('"WARC-Target-URI":"[^"]*"', line) 
	str_url = ''.join(url)
	url2 = re.findall('"([^"]*)"', str_url)
	str_url = ''.join(url2)

	if (str_url.find(query) >= 0): # compare web pages & query
		nofound1 = 1
		#print
		#print 'str_url:'+ str_url
		#print query

		list_links = re.findall('"Links":((?=\[)\[[^]]*\]|(?=\{)\{[^\}]*\}|\"[^"]*\")', line)
		str_links = ''.join(list_links)

		list_href = re.findall('"href":+"http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),])+[?]?"', str_links) #parse 'http(s):'
		findtype(list_href)

		list_url = re.findall('"url":+"http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),])+[?]?"', str_links)
		findtype(list_url)
		
if nofound1 == 0:
	print'Page not found!'
elif nofound2 == 0:
	print'Type not found!'
else:
	if num_jpg != 0:
		print "jpg : " + str(num_jpg)
	if num_html != 0:
		print "html : " + str(num_html)
	if num_php != 0:
		print "php : " + str(num_php)
	if num_gif != 0:
		print "gif : " + str(num_gif)
	if num_png != 0:
		print "png : " + str(num_png)
	if num_css != 0:
		print "css : " + str(num_css)
	if num_js != 0:
		print "js : " + str(num_js)