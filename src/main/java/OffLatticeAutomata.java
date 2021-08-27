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

    public List<Particle> run(int lastFrame) {
        double vSum = 0;
        CellIdxMethod cim;
        for(int timeFrame = 0; timeFrame < lastFrame; timeFrame++) {
            cim = new CellIdxMethod(board, rc, periodicOutline);
            cim.calculateNeighbours();
            for(Particle p : board.getParticles()) {
                List<Double> angles = new ArrayList<>();
                vSum += p.getV();
                for(Particle neigh : cim.getNeighboursOf(p,timeFrame)) {
                    angles.add(neigh.getState(timeFrame).getTheta());
                }
                double newTheta = averageAngleVelocityOfParticles(angles);
                p.nextState(newTheta, L, periodicOutline, timeFrame);
            }
            board.sortBoard();
            calculateVa(vSum, timeFrame);
        }
        return board.getParticles();
    }

    public List<VaEntry> getVas() {
        return vaEntries;
    }

    public void writeVaCSV() throws IOException {
        FileWriter out = new FileWriter("va" + ".csv",false);
        BufferedWriter buffer = new BufferedWriter(out);
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

        Random rand = new Random();
        return Math.atan2(
                avgSin,
                avgCos
        ) + rand.nextDouble()*eta/2 - eta/2;
    }

    private void calculateVa(double vSum, int timeFrame) {
        double va = 1.0/(board.getN() * v) * vSum;
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

        @Override
        public String toString() {
            return t + "," + va;
        }
    }
}
