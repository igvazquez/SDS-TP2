from matplotlib import pyplot as plt, animation
from numpy import arctan, pi, sqrt
import pandas as pd
import numpy as np

va = pd.read_csv("va.csv", sep=",")
benchmark = pd.read_csv("benchmark.csv", sep=",")

benchmark = benchmark.query('n == 10')

plt.figure(200)
plt.plot(va['t'], va['va'])
plt.xlabel("Iteration")
plt.ylabel("VA")
plt.ylim([0,1])

plt.figure(300)
plt.plot(benchmark['eta'], benchmark['va'])
plt.xlabel("Eta")
plt.ylabel("VA")
plt.xlim([0,2*pi])
plt.ylim([0,1])

plt.show()