from matplotlib import pyplot as plt
from numpy import pi
import numpy as np
import pandas as pd
import yaml
from yaml.loader import SafeLoader

# https://stackoverflow.com/questions/40642061/how-to-set-axis-ticks-in-multiples-of-pi-python-matplotlib
def multiple_formatter(denominator=2, number=np.pi, latex='\pi'):
    def gcd(a, b):
        while b:
            a, b = b, a%b
        return a
    def _multiple_formatter(x, pos):
        den = denominator
        num = np.int32(np.rint(den*x/number))
        com = gcd(num,den)
        (num,den) = (int(num/com),int(den/com))
        if den==1:
            if num==0:
                return r'$0$'
            if num==1:
                return r'$%s$'%latex
            elif num==-1:
                return r'$-%s$'%latex
            else:
                return r'$%s%s$'%(num,latex)
        else:
            if num==1:
                return r'$\frac{%s}{%s}$'%(latex,den)
            elif num==-1:
                return r'$\frac{-%s}{%s}$'%(latex,den)
            else:
                return r'$\frac{%s%s}{%s}$'%(num,latex,den)
    return _multiple_formatter

class Multiple:
    def __init__(self, denominator=2, number=np.pi, latex='\pi'):
        self.denominator = denominator
        self.number = number
        self.latex = latex

    def locator(self):
        return plt.MultipleLocator(self.number / self.denominator)

    def formatter(self):
        return plt.FuncFormatter(multiple_formatter(self.denominator, self.number, self.latex))

with open('tp2-python/config-py.yml') as f:
    data = yaml.load(f, Loader=SafeLoader)
    benchmarkdf = pd.read_csv(data['noiseBenchmark']['fileName'], sep=",")

    for n in data['noiseBenchmark']['particles']:
        b = benchmarkdf.query('n == '+str(n))
        plt.errorbar(b['eta'], b['va'], yerr=b['stdDev'], capsize=3, label="N = "+str(n))

    plt.legend()
    plt.grid()
    plt.xlabel("Eta")
    plt.ylabel("VA")
    plt.xlim([0,2*pi])
    plt.ylim([0,1])
    ax = plt.gca()
    ax.xaxis.set_major_locator(plt.MultipleLocator(np.pi / 4))
    ax.xaxis.set_major_formatter(plt.FuncFormatter(multiple_formatter(denominator=4)))

    plt.show()
