import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OffLatticeAutomata {

    private final double L;
    private final double eta;
    private final double rc;
    private final boolean periodicOutline;
    private final CellIdxMethod cim;

    public OffLatticeAutomata(double l, double eta, double rc, boolean periodicOutline, Board initialBoard) {
        L = l;
        this.eta = eta;
        this.rc = rc;
        this.periodicOutline = periodicOutline;
        this.cim = new CellIdxMethod(initialBoard, rc, periodicOutline);
    }

    public Board nextState(Board board){

        List<Particle> nextState = new ArrayList<>();
        for(Particle p : board.getParticles()){
            List<Particle> neighbours = cim.getNeighboursOf(p);
            double avgAngle = averageAngleVelocityOfParticles(neighbours);
            p.nextState(avgAngle,L, periodicOutline);
            nextState.add(p);
        }

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
}
