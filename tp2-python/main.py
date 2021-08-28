import matplotlib.pyplot as plt
import pandas as pd

va = pd.read_csv("va.csv", sep=",")

plt.plot(va['t'], va['va'])
plt.xlabel("Iteration")
plt.ylabel("VA")

plt.show()