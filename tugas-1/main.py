from PIL import Image
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from math import log

img = Image.open('logo_itb.png')
img = img.convert('RGBA')
h, w = img.size

r = [0 for i in range(256)]
g = [0 for i in range(256)]
b = [0 for i in range(256)]
gs = [0 for i in range(256)]

for i in range(h):
    for j in range(w):
        pixel = img.getpixel((i, j))
        r[pixel[0]] += 1
        g[pixel[1]] += 1
        b[pixel[2]] += 1
        gs[(pixel[0]+pixel[1]+pixel[2]) // 3] += 1

# print
def to_logarithm(colors):
  data = []
  for unit in colors:
    if unit != 0:
      data.append(log(unit))
    else:
      data.append(0)
  return data

def draw():
  f, (ax1, ax2, ax3, ax4) = plt.subplots(4, 1, figsize=(7, 5), sharex=True)
  
  r_log_data = to_logarithm(r)
  g_log_data = to_logarithm(g)
  b_log_data = to_logarithm(b)
  gs_log_data = to_logarithm(gs)

  df = pd.DataFrame({'r logarithm': r_log_data,
                       'g logarithm': g_log_data,
                       'b logarithm': b_log_data,
                       'gs logarithm': gs_log_data
                      })

  sns.barplot(y=df['r logarithm'], x=df.index.values, ax=ax1)
  sns.barplot(y=df['g logarithm'], x=df.index.values, ax=ax2)
  sns.barplot(y=df['b logarithm'], x=df.index.values, ax=ax3)
  sns.barplot(y=df['gs logarithm'], x=df.index.values, ax=ax4)
  plt.show()


draw()
