from PIL import Image
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from math import log

img = Image.open('logo_itb.png')
img = img.convert('RGBA')
h, w = img.size
size = 16

r = [0 for i in range(size)]
g = [0 for i in range(size)]
b = [0 for i in range(size)]
gr = [0 for i in range(size)]

for i in range(h):
    for j in range(w):
        pixel = img.getpixel((i, j))
        r[pixel[0] // 16] += 1
        g[pixel[1] // 16] += 1
        b[pixel[2] // 16] += 1
        gr[(pixel[0]+pixel[1]+pixel[2]) // 48] += 1

# print

def draw(colors):
    data = []
    for unit in colors:
        if unit != 0:
            data.append(log(unit))
        else:
            data.append(0)
    df = pd.DataFrame({'colors logarithm': data})
    sns.barplot(y=df['colors logarithm'], x=df.index.values)
    plt.show()


draw(r)
