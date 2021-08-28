import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class OffLatticeAutomata {

    private final double L;
    private final double eta;
    private final double rc;
    private final boolean periodicOutline;
    private final double v;
    private final Board board;
    private final ArrayList<VaEntry> vaEntries;

    public OffLatticeAutomata(double l, double eta, double rc, boolean periodicOutline, Board initialBoard, double v) {
        L = l;
        this.v = v;
        this.rc = rc;
        this.eta = eta;
        this.board = initialBoard;
        this.vaEntries = new ArrayList<>();
        this.periodicOutline = periodicOutline;
    }

    public void run(int maxIterations) {

        CellIdxMethod cim;
        for(int timeFrame = 0; timeFrame < maxIterations; timeFrame++) {
            if (timeFrame%100 == 0){
                System.out.println("Iteracion: " + timeFrame);
            }
            cim = new CellIdxMethod(board, rc, periodicOutline);
            cim.calculateNeighbours();
            calculateVa(board.getParticles(), timeFrame);
            for(Particle p : board.getParticles()) {
                List<Double> angles = new ArrayList<>();
                for(Particle neigh : cim.getNeighboursOf(p,timeFrame)) {
                    if (p.calculateDistance(neigh, L, periodicOutline, timeFrame) < rc){
                        angles.add(neigh.getState(timeFrame).getTheta());
                    }
                }
                double newTheta = averageAngleVelocityOfParticles(angles);
                p.nextState(newTheta, L, periodicOutline, timeFrame);
            }
            board.sortBoard();
        }
    }

    public List<VaEntry> getVas() {
        return vaEntries;
    }

    public void writeVaCSV() throws IOException {
        FileWriter out = new FileWriter("va" + ".csv",false);
        BufferedWriter buffer = new BufferedWriter(out);
        buffer.write("t,va\n");
        for(VaEntry va : vaEntries) {
            buffer.write(va.toString());
            buffer.newLine();
        }
        buffer.flush();
        buffer.close();
        out.close();
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
        ) + rand.nextDouble()*eta/2 - eta/2;
    }

    private void calculateVa(List<Particle> particles, int timeFrame) {
        double vxSum = particles.stream().mapToDouble(p -> p.getState(timeFrame).getVX()).sum();
        double vySum = particles.stream().mapToDouble(p -> p.getState(timeFrame).getVY()).sum();
        double totalVSum = particles.stream().mapToDouble(Particle::getV).sum();

        double va = Math.hypot(vxSum, vySum) / totalVSum;

        vaEntries.add(new VaEntry(timeFrame, va));
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

    private static class VaEntry {
        private final int t;
        private final double va;

        public VaEntry(int t, double va) {
            this.t = t;
            this.va = va;
        }

        public int getT() {
            return t;
        }

        public double getVa() {
            return va;
        }

        @Override
        public String toString() {
            return t + "," + va;
        }
    }
}
