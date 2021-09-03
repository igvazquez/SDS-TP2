# Simulacion de Sistemas - TP 2
Off-Latice Automata Simulation

Para correr la simulacion, el comando que se debe ejecutar es
```
mvn compile exec:java -Dexec.mainClass=AutomataSimulation
```
Existen 3 clases posibles para ejecutar:
- AutomataSimulation: corre la simulacion con los parametros pasados a config.yml
- DensityBenchmark: genera los datos para graficar va vs densidad. Parametros necesarios: maxDensity y simulations
- NoiseBenchmark: genera los datos para graficar va vs ruido para las particulas que indiquemos. Parametros necesarios: particles y simulations

```yaml
randomize: true

# Total time steps
iterations: 2500

# N, L, eta
totalParticles: 30
boardLength: 20.0
eta: 0.5
absV: 0.03

# Archivos de entrada
staticFile: "sds-tp2/src/main/resources/newStatic.txt"
dynamicFile: "sds-tp2/src/main/resources/newDynamic.txt"

# Rc y condición de borde
radius: 1.0
periodicOutline: true

# Configuraciones para los benchmark
benchmark:
  particles:
    - 10
    - 100
    - 400
  simulations: 50

densityBenchmark:
  maxDensity: 10.0
  simulations: 50

# Dejar "" para el default
fileName: "output"
```

Para graficar, es necesario tener instalado matplotlib, numpy, pandas y pyyaml

podemos correr los scipts solo con
```
python3 <script>
```

y la configuracion esta dada por el arhivo config-py.yaml
```yaml
# density vs va
densityBenchmark:
  maxDensity: 10.0
  simulations: 50
  fileName: "va-vs-density-noise-0.5.csv"

noiseBenchmark:
  particles:
    - 10
    - 100
    - 400
  simulations: 50
  fileName: "noiseBenchmark.csv"

vaBenchmark:
  iterations: 2500
  fileName: "noiseBenchmark.csv"
  ```
