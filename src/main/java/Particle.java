import java.util.Objects;

public class Particle {

    private final long id;
    private final State state;

    public Particle(long id, double x, double y, double vx, double vy) {
        this.id = id;
        this.state = new State(x, y, vx, vy);
    }

    public double calculateDistance(Particle p, double L, boolean periodicOutline) {
        double x = distanceFromAxis(getX(), p.getX(), L, periodicOutline);
        double y = distanceFromAxis(getY(), p.getY(), L, periodicOutline);

        return Math.sqrt(x*x + y*y);
    }

    private double distanceFromAxis(double ax1, double ax2, double L, boolean periodicOutline){
        double distance = Math.abs(ax1 - ax2);

        if (periodicOutline){
            if(distance > L/2){
                distance = L - distance;
            }
        }
        return distance;
    }

    public long getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public double getX(){
        return state.x;
    }

    public double getY(){
        return state.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;
        Particle particle = (Particle) o;
        return id == particle.id && Objects.equals(state, particle.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state);
    }

    @Override
    public String toString() {
        return "Particle{" +
                "id=" + id +
                ", x=" + state.x +
                ", y=" + state.y +
                '}';
    }

    private static class State{

        private double x;
        private double y;
        private double vx;
        private double vy;

        public State(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public State(double x, double y, double vx, double vy) {
            this(x,y);
            this.vx = vx;
            this.vy = vy;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getVX() {
            return vx;
        }

        public double getVY() {
            return vy;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setVX(double vx) {
            this.vx = vx;
        }

        public void setVY(double vy) {
            this.vy = vy;
        }
    }
}