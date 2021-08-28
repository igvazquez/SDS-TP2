import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Board {

    private final double L;
    private final int M;
    private final List<Particle> particles;
    Map<Integer, List<Particle>> cells;

    public Board(double l, int m, List<Particle> particles) {
        L = l;
        M = m;
        this.particles = particles;
        this.cells = new HashMap<>();
        sortBoard();
    }

    public void sortBoard() {
        for (int i = 0; i < M * M; i++) {
            cells.put(i, new ArrayList<>());
        }
        divideParticles();
    }

//  public Map<Integer, List<Particle>> divideParticles(List<Particle> particles){
    public void divideParticles(){
        for(Particle p : particles){
            if (p.getLastState().getX() < 0 || p.getLastState().getX() > L || p.getLastState().getY() < 0 || p.getLastState().getY() > L){
                throw new IllegalArgumentException("Partícula fuera de los límites.");
            }
            cells.get(calculateCellIndexOnBoard(p.getLastState().getX(), p.getLastState().getY())).add(p);
        }
    }

    public Integer calculateCellIndexOnBoard(double x, double y){
        int i = (int) (x / (L/M));
        int j = (int) (y / (L/M));
        return i + M*j;
    }

    public static Board getRandomBoardFile(int n, double l, int m, double r, double v) {

        List<Particle> particles = new ArrayList<>();

        try {
            FileWriter st = new FileWriter("src/main/resources/newStatic.txt",false);
            BufferedWriter stBuffer = new BufferedWriter(st);
            FileWriter dyn = new FileWriter("src/main/resources/newDynamic.txt",false);
            BufferedWriter dynBuffer = new BufferedWriter(dyn);

            stBuffer.write(String.valueOf(n));
            stBuffer.newLine();
            stBuffer.write(String.valueOf(l));
            stBuffer.newLine();

            dynBuffer.write("t0");
            dynBuffer.newLine();

            double x, y, theta;

            int i;
            for (i = 0; i < n; i++) {
                x = Math.random() * l;
                y = Math.random() * l;
                theta = Math.random() * 2 * Math.PI;

                dynBuffer.write(String.valueOf(x));
                dynBuffer.write(" ");
                dynBuffer.write(String.valueOf(y));
                dynBuffer.newLine();

                stBuffer.write(String.valueOf(r));
                stBuffer.newLine();

                particles.add(new Particle(i, x, y, r, v, theta));
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

    public double getL() {
        return L;
    }

    public int getM() {
        return M;
    }

    public int getN() { return particles.size(); }

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
