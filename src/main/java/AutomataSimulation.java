import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class AutomataSimulation {

    private static double rc;
    private static int iterations;

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
        double eta = (double) data.get("eta");
        boolean per = (boolean) data.get("periodicOutline");
        double v = (double) data.get("absV");
        iterations = (int) data.get("iterations");

        Board board;
        if((boolean) data.get("randomize")) {
            int n = (int) data.get("totalParticles");
            double l = (double) data.get("boardLength");
            int m = optM(l);
            board = Board.getRandomBoardFile(n,l,m,0, v, iterations);
        } else {
            board = inputBoard((String) data.get("staticFile"), (String) data.get("dynamicFile"));
        }
//        List<Particle> particles = new ArrayList<>();
//        particles.add(new Particle(0, 0.5, 0.5, 0, 0.03, 0));
//        particles.add(new Particle(1, 0.01, 0.01, 0, 0.03, 0));
//        particles.add(new Particle(2, 0.55, 0.5, 0, 0.03, 0));
//        particles.add(new Particle(3, 0.99, 0.99, 0, 0.03, 0));
//        particles.add(new Particle(4, 1.5, 1.5, 0, 0.03, 0));
//        board = new Board(4,4,particles);

        assert board != null;
        final OffLatticeAutomata automata = new OffLatticeAutomata(board.getL(), eta, rc, per, board, v, iterations);

        automata.run();
        visual(board.getParticles(), iterations);
        automata.writeVaCSV();
    }

    private static int optM(double l) {
        return (int)Math.floor(l/rc);
    }

    private static Board inputBoard(String staticFile, String dynamicFile) {
        Scanner st=null, din=null;
        try {
            st = new Scanner(new File(staticFile));
            din = new Scanner(new File(dynamicFile));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if(st!=null && din!=null){
            int n = 0;
            double l = 0;
            if (st.hasNextInt()) {
                n = st.nextInt();
                if (st.hasNextDouble()) {
                    l = Double.parseDouble(st.next());
                }
            }

            if (din.hasNext("t.")) {     // t0
                din.nextLine();
            } else {
                throw new IllegalArgumentException("No se encontraron las condiciones iniciales.");
            }

            List<Particle> particles = new ArrayList<>();
            for (int i = 0; i < n && st.hasNextLine() && din.hasNextLine(); i++) {
                double x = 0, y = 0, v = 0, r = 0, theta = 0;
                if (din.hasNextLine()) {
                    x = din.nextDouble();
                    y = din.nextDouble();
                    double vx = din.nextDouble();
                    double vy = din.nextDouble();
                    v = Math.sqrt(Math.pow(vx,2) + Math.pow(vy,2));
                    theta = Math.atan2(vy,vx);
                    din.nextLine();
                }
                particles.add(new Particle(i, x, y, r, v, theta));
            }
            return new Board(l, optM(l), particles);
        }
        return null;
    }

    private static void visual(List<Particle> particles, int frames) {
        String fileName = "positions";
        try {
            FileWriter pos = new FileWriter(fileName + ".xyz", false);
            BufferedWriter buffer = new BufferedWriter(pos);
            for(int timeFrame=0; timeFrame<=frames; timeFrame++) {
                buffer.write(String.valueOf(particles.size()));
                buffer.newLine();
                buffer.newLine();
                for(Particle p : particles) {
                    buffer.write(p.getId() + " " + p.getState().getX() + " " + p.getState().getY() + " " + p.getState().getVX() + " " + p.getState().getVY() + " " + p.getState().getTheta());
                    buffer.newLine();
                }
            }
            buffer.flush();
            buffer.close();
            pos.close();
            System.out.println("Resultados en "+ fileName + ".xyz");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
