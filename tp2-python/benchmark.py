from matplotlib import pyplot as plt, animation
from numpy import pi
import pandas as pd

benchmark = pd.read_csv("benchmark.csv", sep=",")

benchmark = benchmark.query('n == 40')

plt.figure(300)
plt.plot(benchmark['eta'], benchmark['va'])
plt.xlabel("Eta")
plt.ylabel("VA")
plt.xlim([0,2*pi])
plt.ylim([0,1])

plt.show()