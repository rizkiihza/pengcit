from PIL import Image
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from math import log

img = Image.open('iron_man.jpg')
pixels = img.convert('RGBA').load()
h, w = img.size

K_SIZE = 16
K_DIVISOR = 256 // K_SIZE

r = [0 for i in range(K_SIZE)]
g = [0 for i in range(K_SIZE)]
b = [0 for i in range(K_SIZE)]
gs = [0 for i in range(K_SIZE)]

for i in range(h):
    for j in range(w):
        pixel = pixels[i, j]
        r[pixel[0] // K_DIVISOR] += 1
        g[pixel[1] // K_DIVISOR] += 1
        b[pixel[2] // K_DIVISOR] += 1
        gs[(pixel[0]+pixel[1]+pixel[2]) // 48] += 1

# print
def get_label(inputs):
      data = []
      for unit in inputs:
        data.append(str(K_DIVISOR*unit) + '-' + str(K_DIVISOR*(unit+1) - 1))
      return data

def to_logarithm(colors):
  data = []
  for unit in colors:
    if unit != 0:
      data.append(log(unit))
    else:
      data.append(0)
  return data

def draw():
  f, (ax1, ax2, ax3, ax4) = plt.subplots(4, 1, figsize=(20, 10), sharex=True)
  
  r_log_data = to_logarithm(r)
  g_log_data = to_logarithm(g)
  b_log_data = to_logarithm(b)
  gs_log_data = to_logarithm(gs)

  df = pd.DataFrame({'r logarithm': r_log_data,
                       'g logarithm': g_log_data,
                       'b logarithm': b_log_data,
                       'gs logarithm': gs_log_data
                      })

  index = get_label(df.index.values)
  # for unit in index:
  #     print(unit)
  sns.barplot(y=df['r logarithm'], x=index, color='red', ax=ax1)
  sns.barplot(y=df['g logarithm'], x=index, color='green', ax=ax2)
  sns.barplot(y=df['b logarithm'], x=index, color='blue', ax=ax3)
  sns.barplot(y=df['gs logarithm'], x=index, color='grey',ax=ax4)
  plt.show()


draw()
