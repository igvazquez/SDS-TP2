from matplotlib import pyplot as plt
import pandas as pd
import yaml
from yaml.loader import SafeLoader

# Open the file and load the file
with open('tp2-python/config-py.yml') as f:
    data = yaml.load(f, Loader=SafeLoader)
    benchmarkdf = pd.read_csv(data['densityBenchmark']['fileName'], sep=",")

    plt.errorbar(benchmarkdf['density'], benchmarkdf['va'], yerr=benchmarkdf['stdDev'], capsize=3)
    plt.grid()
    plt.xlabel("Density")
    plt.ylabel("VA")
    plt.xlim([0,data['densityBenchmark']['maxDensity']])
    plt.ylim([0.2,1])

    plt.show()
