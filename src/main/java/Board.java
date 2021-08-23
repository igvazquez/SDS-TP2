import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Board {

    private final double L;
    private final int M;
    private final List<Particle> particles;
    final Map<Integer, List<Particle>> cells;

    public Board(double l, int m, List<Particle> particles) {
        L = l;
        M = m;
        this.particles = particles;
        this.cells = divideParticles(particles);
    }

    private Map<Integer, List<Particle>> divideParticles(List<Particle> particles){
        Map<Integer, List<Particle>> cells = new HashMap<>();
        for (int i = 0; i < M * M; i++) {
            cells.put(i, new ArrayList<>());
        }
        for(Particle p : particles){
            if (p.getX() < 0 || p.getX() > L || p.getY() < 0 || p.getY() > L){
//                System.out.println(L);
//                System.out.println(p.getX());
//                System.out.println(p.getY());
                throw new IllegalArgumentException();
            }
            cells.get(calculateCellIndexOnBoard(p.getX(), p.getY())).add(p);
        }
        return cells;
    }

    public Integer calculateCellIndexOnBoard(double x, double y){
        int i = (int) (x / (L/M));
        int j = (int) (y / (L/M));
        return i + M*j;
    }

    public static Board getRandomBoardFile(int n, double l, int m, double maxR) {

        List<Particle> particles = new ArrayList<>();

        try {
            FileWriter st = new FileWriter("sds-tp1/src/main/resources/newStatic.txt",false);
            BufferedWriter stBuffer = new BufferedWriter(st);
            FileWriter dyn = new FileWriter("sds-tp1/src/main/resources/newDynamic.txt",false);
            BufferedWriter dynBuffer = new BufferedWriter(dyn);

//            System.out.println(n);
            stBuffer.write(String.valueOf(n));
            stBuffer.newLine();
            stBuffer.write(String.valueOf(l));
            stBuffer.newLine();

            dynBuffer.write("t0");
            dynBuffer.newLine();

            double x, y, r;
            int i;
            for (i = 0; i < n; i++) {
                x = Math.random() * l;
                y = Math.random() * l;
                r = Math.random() * maxR;

                dynBuffer.write(String.valueOf(x));
                dynBuffer.write(" ");
                dynBuffer.write(String.valueOf(y));
                dynBuffer.newLine();

                stBuffer.write(String.valueOf(r));
                stBuffer.newLine();

                particles.add(new Particle(i, x, y, r));
            }

            stBuffer.flush();
            dynBuffer.flush();
            stBuffer.close();
            dynBuffer.close();
            st.close();
            dyn.close();
            System.out.println("Nuevo tablero en newStatic.txt y newDynamic.txt");
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error.");
            e.printStackTrace();
        }

        return new Board(l, m, particles);
    }

    public static Board getRandomBoard(int n, double l, int m, double maxR) {
        List<Particle> particles = new ArrayList<>();
        double x, y, r;
        int i;
        for (i = 0; i < n; i++) {
            x = Math.random() * l;
            y = Math.random() * l;
            r = Math.random() * maxR;
            particles.add(new Particle(i, x, y, r));
        }
        return new Board(l, m, particles);
    }

    public double getL() {
        return L;
    }

    public int getM() {
        return M;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public List<Particle> getCell(int idx) {
        return cells.get(idx);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(M*M);
        b.append("Board:\n");
        for (int i = 0; i < M * M; i++) {
            b.append(cells.get(i).size()).append(" ");
            if (i % M == M-1){
                b.append("\n");
            }
        }
        return b.toString();
    }
}
