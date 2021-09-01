import org.apache.commons.math3.stat.StatUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OffLatticeAutomata {

    private final double L;
    private final double eta;
    private final double rc;
    private final boolean periodicOutline;
    private final double v;
    private final int iterations;
    private final List<Particle> states;
    private final Board board;
    private final List<Double> vaEntries;

    public OffLatticeAutomata(double l, double eta, double rc, boolean periodicOutline, Board initialBoard, double v, int iterations) {
        L = l;
        this.v = v;
        this.rc = rc;
        this.eta = eta;
        this.board = initialBoard;
        this.vaEntries = new ArrayList<>(iterations);
        this.states = new ArrayList<>(iterations);
        this.periodicOutline = periodicOutline;
        this.iterations = iterations;
    }

    public void run() {

        CellIdxMethod cim;
        for(int timeFrame = 0; timeFrame < iterations; timeFrame++) {
            if (timeFrame%100 == 0){
                System.out.println("Iteracion: " + timeFrame);
            }
            cim = new CellIdxMethod(board, rc, periodicOutline);
            cim.calculateNeighbours();
            calculateVa(board.getParticles());
            List<Particle> newState = new ArrayList<>(board.getParticles().size());
            for(Particle p : board.getParticles()) {
                double newTheta = p.getState().getTheta();
                List<Double> angles = new ArrayList<>();
                Map<Integer, Set<Particle>> neighbours = cim.getNeighboursMap();
                for(Particle neigh : neighbours.get(p.getId())) {
                    angles.add(neigh.getState().getTheta());
                }
                if(angles.size() > 0) {
                    newTheta = averageAngleVelocityOfParticles(angles);
                }
                newState.add(p.nextState(newTheta, L, periodicOutline, timeFrame));
            }
            board.sortBoard(newState);
        }
    }

    public List<Double> getVas() {
        return vaEntries;
    }

    public void writeVaCSV() throws IOException {
        FileWriter out = new FileWriter("va" + ".csv",false);
        BufferedWriter buffer = new BufferedWriter(out);
        buffer.write("t,va\n");
        int i = 0;
        for(Double va : vaEntries) {
            buffer.write(i + "," + va);
            i++;
            buffer.newLine();
        }
        buffer.flush();
        buffer.close();
        out.close();
    }

    public double getMean(){
        return StatUtils.geometricMean(vaEntries.subList(iterations/2, iterations-1).stream()
                .mapToDouble(va -> va).toArray());
    }

    public double getVaStdDev(double mean){
        return StatUtils.variance(vaEntries.subList(iterations/2, iterations-1).stream()
                .mapToDouble(va -> va).toArray(), mean);
    }

    private double averageAngleVelocityOfParticles(final List<Double> angles) {
        double avgSin = angles.stream().mapToDouble(a -> a)
                .map(Math::sin).average().orElse(0.0);
        double avgCos = angles.stream().mapToDouble(a -> a)
                .map(Math::cos).average().orElse(0.0);

        Random rand = new Random(System.currentTimeMillis());
        return Math.atan2(
                avgSin,
                avgCos
        ) + rand.nextDouble()*eta - eta/2;
    }

    private void calculateVa(List<Particle> particles) {
        double vxSum = particles.stream().mapToDouble(p -> p.getState().getVX()).sum();
        double vySum = particles.stream().mapToDouble(p -> p.getState().getVY()).sum();
        double totalVSum = particles.stream().mapToDouble(Particle::getV).sum();

        double va = Math.hypot(vxSum, vySum) / totalVSum;

        vaEntries.add(va);
    }


//    public Board nextState(Board board){
//        double vSum = 0;
//        List<Particle> nextState = new ArrayList<>();
//        for(Particle p : board.getParticles()){
//            vSum += p.getV();
//            List<Particle> neighbours = cim.getNeighboursOf(p);
//            double avgAngle = averageAngleVelocityOfParticles(neighbours);
//            p.nextState(avgAngle,L, periodicOutline);
//            nextState.add(p);
//        }
//        calculateVa(vSum, board);
//        return new Board(L, board.getM(), nextState);
//    }
//
//    public List<Board> run(Board initialState) {
//        final List<Board> boards = new ArrayList<>();
//
//        Board prevBoard = initialState;
//        cim.calculateNeighbours();
//        boards.add(prevBoard);
//        int i = 0;
//        while(i<100) {
//            prevBoard = nextState(prevBoard);
//            cim.calculateNeighbours();
//            boards.add(prevBoard);
//            i++;
//        }
//
//        return boards;
//    }
}
