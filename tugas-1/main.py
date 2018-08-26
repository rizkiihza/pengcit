from PIL import Image

img = Image.open('test.png')
h, w = img.size

r = [0 for i in range(256)]
g = [0 for i in range(256)]
b = [0 for i in range(256)]
gr = [0 for i in range(256)]

for i in range(h):
	for j in range(w):
		pixel = img.getpixel((i, j))
		r[pixel[0]] += 1
		g[pixel[1]] += 1
		b[pixel[2]] += 1
		gr[(pixel[0]+pixel[1]+pixel[2]) // 3] += 1

# print
