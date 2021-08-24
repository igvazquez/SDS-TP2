import java.util.*;

public class CellIdxMethod {
    private final Board board;
    private final double rc;
    private final int M;
    private final boolean per;
    private final Map<Integer, Set<Particle>> neighboursMap;

    private static final int OUT_OF_BOUNDS = -1;

    public CellIdxMethod(Board board, double rc, boolean per) {
        this.board = board;
        this.rc = rc;
        this.per = per;
        M = board.getM();

        neighboursMap = new HashMap<>();
        for (int i = 0; i < M * M; i++) {
            neighboursMap.put(i, new HashSet<>());
        }
    }

    private int getRightIndex(int currentCellIndex, int row, int col, boolean periodicOutline) {
        int rightCol;
        if(periodicOutline) {
            rightCol = Math.floorMod(currentCellIndex + 1, M);
            return rightCol + row*M;
        } else {
            rightCol = col + 1;
            return rightCol >= M ? OUT_OF_BOUNDS : rightCol + row*M;
        }
    }

    private int getUpperRightIndex(int row, int col, boolean periodicOutline) {
        int upperRightRow;
        int upperRightCol;

        if(periodicOutline) {
            upperRightRow = Math.floorMod(row - 1, M);
            upperRightCol = Math.floorMod(col + 1, M);
            return upperRightCol + upperRightRow*M;
        } else {
            upperRightCol = col + 1;
            upperRightRow = row - 1;
            return (upperRightRow < 0 || upperRightCol >= M) ? OUT_OF_BOUNDS : upperRightCol + upperRightRow*M;
        }
    }

    private int getLowerRightIndex(int row, int col, boolean periodicOutline) {
        int lowerRightRow;
        int lowerRightCol;

        if(periodicOutline) {
            lowerRightRow = Math.floorMod(row + 1, M);
            lowerRightCol = Math.floorMod(col + 1, M);
            return lowerRightCol + lowerRightRow*M;
        } else {
            lowerRightRow = row + 1;
            lowerRightCol = col + 1;
            return (lowerRightRow >= M || lowerRightCol >= M) ? OUT_OF_BOUNDS : lowerRightCol + lowerRightRow*M;
        }
    }

    private int getLowerIndex(int row, int col, boolean periodicOutline) {
        int lowerRow;

        if(periodicOutline) {
            lowerRow = Math.floorMod(row + 1, M);
            return col + lowerRow*M;
        } else {
            lowerRow = row + 1;
            return lowerRow >= M ? OUT_OF_BOUNDS : col + lowerRow*M;
        }
    }

    private void addNeighboursToCells(Map<Integer, Set<Particle>> neighbours, int currentIdx, int neighbourIdx) {
        if(neighbourIdx != OUT_OF_BOUNDS) {
            List<Particle> currentCell = board.getCell(currentIdx);
            if(currentIdx != neighbourIdx) {
                neighbours.get(currentIdx).addAll(board.getCell(neighbourIdx));
                if(currentCell != null)
                    neighbours.get(neighbourIdx).addAll(currentCell);
            } else if(currentCell != null) {
                neighbours.get(currentIdx).addAll(currentCell);
            }
        }
    }

    public List<Particle> getNeighboursOf(Particle particle) {
        if (!board.getParticles().contains(particle)){
            throw new IllegalArgumentException("Particle does not belong to this board");
        }
        List<Particle> ret = new ArrayList<>();
        int idx = board.calculateCellIndexOnBoard(particle.getX(), particle.getY());
        Set<Particle> neighbours = neighboursMap.get(idx);
        for (Particle n : neighbours){
            if (particle.calculateDistance(n, board.getL(), per) < rc){
                ret.add(n);
            }
        }
        return ret;
    }

    public Map<Integer, Set<Particle>> calculateNeighbours(){
        for (int i=0; i<M*M; i++) {
            int row = i/M;
            int col = i%M;
            //add this cell particles as neighbours
            addNeighboursToCells(neighboursMap, i, i);
            //add right neighbours
            addNeighboursToCells(neighboursMap, i, getRightIndex(i, row, col, per));
            //add upper right neighbours
            addNeighboursToCells(neighboursMap, i, getUpperRightIndex(row, col, per));
            //add lower right neighbours
            addNeighboursToCells(neighboursMap, i, getLowerRightIndex(row, col, per));
            //add lower neighbours
            addNeighboursToCells(neighboursMap, i, getLowerIndex(row, col, per));
        }

        return neighboursMap;
    }

    public Board getBoard() {
        return board;
    }
}