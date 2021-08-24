import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class AutomataSimulation {

    private final static String outputName = "output";
    private static Map<String, Object> data;
    private static Board board;
    private static int M;

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
            throw new IllegalArgumentException("No se han detectado argumentos. Ingrese 'ayuda' para más información.");
        }

        double rc = (double) data.get("radius");
        double eta = (double) data.get("eta");
        boolean per = (boolean) data.get("periodicOutline");

//        if((boolean)data.get("randomize")) {
//            int n = (int) data.get("totalParticles");
//            double l = (double) data.get("boardLength");
//            int m = optM(rc,l);
//            board = Board.getRandomBoardFile(n,l,m,0);
//        } else {
//            board = inputBoard((String)data.get("staticFile"), (String)data.get("dynamicFile"));
//        }
//
//        String out = (String) data.get("fileName");
//        if(out.isEmpty()) {
//            out = outputName;
//        }

        Particle p1 = new Particle(1,0,0,0,0.03,1.5708);
        Particle p2 = new Particle(2,0.5,0,0,0.03,3.92699);
        Particle p3 = new Particle(3,0.5,0.5,0,0.03,2.35619);
        Particle p4 = new Particle(4,0,0.5,0,0.03,0.785398);
        List<Particle> particles = Arrays.asList(p1,p2,p3,p4);
        board = new Board(2,2,particles);
        final OffLatticeAutomata automata = new OffLatticeAutomata(board.getL(), eta, rc, per, board);

        final List<Board> automataStates = automata.run(board);

        System.out.println("holi");
    }

    private static int optM(double rc, double l) {
        int m = (int)Math.floor(l/rc);
        System.out.println("M óptimo: "+m);
        return m;
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
                din.next();
            }

            List<Particle> particles = new ArrayList<>();
            for (int i = 0; i < n && st.hasNextLine() && din.hasNextLine(); i++) {
                double x = 0, y = 0, r = 0;
                if (din.hasNextLine()) {
                    x = din.nextDouble();
                    y = din.nextDouble();
                    din.nextLine(); // el resto de los datos no los usamos por ahora
                }
                if (st.hasNextLine()) {
                    r = st.nextDouble();
                }
                particles.add(new Particle(i, x, y, r));
            }
//            System.out.println("L "+l);
            return new Board(l, M, particles);
        }
        return null;
    }
}
