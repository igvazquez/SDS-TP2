from matplotlib import pyplot as plt, animation
from numpy import arctan, pi, sqrt
import pandas as pd
import numpy as np
import yaml
from yaml.loader import SafeLoader

with open('tp2-python/config-py.yml') as f:
    data = yaml.load(f, Loader=SafeLoader)
    benchmarkdf = pd.read_csv(data['vaBenchmark']['fileName'], sep=",")

    va = pd.read_csv("va.csv", sep=",")

    plt.figure(200)
    plt.plot(va['t'], va['va'])
    plt.xlabel("Iteration")
    plt.ylabel("VA")
    plt.ylim([0,1])

    plt.show()