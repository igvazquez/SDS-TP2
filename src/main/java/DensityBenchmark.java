import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DensityBenchmark {

    private static double rc;

    public static void main(String[] args) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/resources/config.yml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        if(data.isEmpty()) {
            throw new IllegalArgumentException("No se han detectado argumentos.");
        }

        rc = (double) data.get("radius");
        boolean per = (boolean) data.get("periodicOutline");
        double v = (double) data.get("absV");

        Double length = (Double) data.get("boardLength");
        Double eta = (Double) data.get("eta");
        Integer iterations = (Integer) data.get("iterations");
        String fileName = (String) data.get("fileName");
        if (fileName.equals("")){
            fileName = "densityBenchmark.csv";
        }
        LinkedHashMap<String, Object> benchmarkData = (LinkedHashMap<String, Object>) data.get("densityBenchmark");
        Double maxDensity = (Double) benchmarkData.get("maxDensity");
        Integer simulations = (Integer) benchmarkData.get("simulations");

        double step = maxDensity/simulations;
        double density = step;

        FileWriter benchmark = new FileWriter(fileName+".csv", false);
        BufferedWriter buffer = new BufferedWriter(benchmark);
        buffer.write("density,va,stdDev\n");

        for (int i = 1; i <= simulations; i++) {
            int totalParticles = (int) (density*length);
            System.out.println("Density = " + density);
            System.out.println("Particles = " + totalParticles);
            Board board = Board.getRandomBoard(totalParticles,length,optM(length),0, v);
            OffLatticeAutomata automata = new OffLatticeAutomata(board.getL(), eta, rc, per, board, v, iterations);
            automata.run();
            double mean = automata.getMean();
            System.out.println("Mean = " + mean);
            try {
                buffer.write(density + ",");
                buffer.write(mean + ",");
                buffer.write(automata.getVaStdDev(mean) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            density += step;
        }

        buffer.flush();
        buffer.close();
        benchmark.close();
    }

    private static int optM(double l) {
        return (int)Math.floor(l/rc);
    }
}
