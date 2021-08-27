import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class AutomataSimulation {

    private final static String outputName = "output";
    private static Map<String, Object> data;
    private static Board board;
    private static double rc;

    public static void main(String[] args) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/resources/config.yml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Yaml yaml = new Yaml();
        data = yaml.load(inputStream);
        if(data.isEmpty()) {
            throw new IllegalArgumentException("No se han detectado argumentos.");
        }

        rc = (double) data.get("radius");
        double eta = (double) data.get("eta");
        boolean per = (boolean) data.get("periodicOutline");
        double v = (double) data.get("absV");

        if((boolean)data.get("randomize")) {
            int n = (int) data.get("totalParticles");
            double l = (double) data.get("boardLength");
            int m = optM(l);
            board = Board.getRandomBoardFile(n,l,m,0, v);
        } else {
            board = inputBoard((String)data.get("staticFile"), (String)data.get("dynamicFile"));
        }

//        String out = (String) data.get("fileName");
//        if(out.isEmpty()) {
//            out = outputName;
//        }

//        Particle p1 = new Particle(1,0.4,0,0,0.03,1.5708);
//        Particle p2 = new Particle(2,0.5,0,0,0.03,3.92699);
//        Particle p3 = new Particle(3,0.5,0.5,0,0.03,2.35619);
//        Particle p4 = new Particle(4,0,0.5,0,0.03,0.785398);
//        List<Particle> particles = Arrays.asList(p1,p2,p3,p4);
//        board = new Board(2,1,particles);

        final OffLatticeAutomata automata = new OffLatticeAutomata(board.getL(), eta, rc, per, board, v);

        int i = (int) data.get("iterations");
        List<Particle> result = automata.run(i);
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
}
