import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OffLatticeAutomata {

    private final double L;
    private final double eta;
    private final double rc;
    private final boolean periodicOutline;
    private final CellIdxMethod cim;
    private static final double fixedV = 0.03;
    private int t;
    private final ArrayList<VaEntry> vaEntries;

    public OffLatticeAutomata(double l, double eta, double rc, boolean periodicOutline, Board initialBoard) {
        L = l;
        t = 0;
        this.eta = eta;
        this.rc = rc;
        this.periodicOutline = periodicOutline;
        this.cim = new CellIdxMethod(initialBoard, rc, periodicOutline);
        this.vaEntries = new ArrayList<>();
    }

    public Board nextState(Board board){
        double vSum = 0;
        List<Particle> nextState = new ArrayList<>();
        for(Particle p : board.getParticles()){
            vSum += p.getVMod();
            List<Particle> neighbours = cim.getNeighboursOf(p);
            double avgAngle = averageAngleVelocityOfParticles(neighbours);
            p.nextState(avgAngle,L, periodicOutline);
            nextState.add(p);
        }
        calculateVa(vSum, board);
        return new Board(L, board.getM(), nextState);
    }

    public List<Board> run(Board initialState) {
        final List<Board> boards = new ArrayList<>();

        Board prevBoard = initialState;
        cim.calculateNeighbours();
        boards.add(prevBoard);
        int i = 0;
        while(i<100) {
            prevBoard = nextState(prevBoard);
            cim.calculateNeighbours();
            boards.add(prevBoard);
            i++;
        }

        return boards;
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

    private double averageAngleVelocityOfParticles(final List<Particle> particles) {
        double avgX = particles.stream().mapToDouble(Particle::getTheta)
                .map(Math::sin).sorted().average().orElseThrow(IllegalStateException::new);
        double avgY = particles.stream().mapToDouble(Particle::getTheta)
                .map(Math::cos).sorted().average().orElseThrow(IllegalStateException::new);

        Random rand = new Random();
        return Math.atan2(
                avgX,
                avgY
        ) + rand.nextDouble()*eta/2 - eta/2;
    }

    private void calculateVa(double vSum, Board board) {
        double va = 1.0/(board.getParticles().size() * fixedV) * vSum;
        vaEntries.add(new VaEntry(t, va));
        t++;
    }

    private static class VaEntry {
        private int t;
        private double va;

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
