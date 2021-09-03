import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NoiseBenchmark {

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

        LinkedHashMap<String, Object> benchmarkData = (LinkedHashMap<String, Object>) data.get("benchmark");
        List<Integer> benchmarkParticles = (ArrayList<Integer>) benchmarkData.get("particles");
        Integer simulations = (Integer) benchmarkData.get("simulations");
        String fileName = (String) benchmarkData.get("fileName");
        if (fileName.equals("")){
            fileName = "noiseBenchmark.csv";
        }
        double etaStep = 2*Math.PI/simulations;

        double l = (double) data.get("boardLength");
        double density = benchmarkParticles.get(0)/(l*l);
        int iterations = (int) data.get("iterations");

        FileWriter benchmark = new FileWriter(fileName+".csv", false);
        BufferedWriter buffer = new BufferedWriter(benchmark);
        buffer.write("n,va,eta,stdDev\n");

        benchmarkParticles.forEach(n ->{
            double eta = 0;
            for (int i = 0; i < simulations; i++) {
                System.out.println("N = " + n);
                System.out.println("sim = " + i);
                double length = Math.sqrt(n/density);
                Board board = Board.getRandomBoard(n,length,optM(length),0, v);
                OffLatticeAutomata automata = new OffLatticeAutomata(board.getL(), eta, rc, per, board, v, iterations);
                automata.run();
                double mean = automata.getMean();
                try {
                    buffer.write(n + ",");
                    buffer.write(mean + ",");
                    buffer.write(eta + ",");
                    buffer.write(automata.getVaStdDev(mean) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                eta += etaStep;
            }
        });

        buffer.flush();
        buffer.close();
        benchmark.close();
    }

    private static int optM(double l) {
        return (int)Math.floor(l/rc);
    }
}
