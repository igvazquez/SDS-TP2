import java.util.Objects;

public class Particle {

    private final long id;
    private State state;
    private final double radius;

    public Particle(long id, double x, double y, double radius) {
        this.id = id;
        this.radius = radius;
        this.state = new State(x, y);
    }

    public Particle(long id, double x, double y, double radius, double v, double theta) {
        this.id = id;
        this.radius = radius;
        this.state = new State(x, y, v, theta);
    }

    public double calculateDistance(Particle p, double L, boolean periodicOutline) {
        double x = distanceFromAxis(getX(), p.getX(), L, periodicOutline);
        double y = distanceFromAxis(getY(), p.getY(), L, periodicOutline);

        return Math.sqrt(x*x + y*y) - radius - p.getRadius();
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

    public void nextState(double newTheta, double L, boolean periodicOutline){
        this.state = new State(
          nextPosition(state.x + getVX(), L, periodicOutline),
          nextPosition(state.y + getVY(), L, periodicOutline),
          state.v,
          newTheta < 0 ? newTheta + Math.PI : newTheta
        );
    }

    public double getVX() {
        return Math.cos(state.theta) * state.v;
    }

    public double getVY() {
        return Math.sin(state.theta) * state.v;
    }

    public double getVMod() { return Math.sqrt(Math.pow(getVX(), 2) + Math.pow(getVY(), 2)); }

    private double nextPosition(double coordinate, double L, boolean periodicOutline){
        double ret = coordinate;

        if(periodicOutline) {
            if(coordinate >= L) {
                ret = coordinate - L;
            } else if(coordinate < 0) {
                ret = coordinate + L;
            }
        }
        return ret;
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

    public double getRadius() {
        return radius;
    }

    public double getTheta(){
        return state.theta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;
        Particle particle = (Particle) o;
        return id == particle.id && radius == particle.radius && Objects.equals(state, particle.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, radius);
    }

    @Override
    public String toString() {
        return "Particle{" +
                "id=" + id +
                ", x=" + state.x +
                ", y=" + state.y +
                ", radius=" + radius +
                '}';
    }

    private static class State{

        private double x;
        private double y;
        private double v;
        private double theta;

        public State(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public State(double x, double y, double v, double theta) {
            this(x,y);
            this.v = v;
            this.theta = theta;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getV() {
            return v;
        }

        public double getVY() {
            return theta;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setVX(double vx) {
            this.v = vx;
        }

        public void setVY(double vy) {
            this.theta = vy;
        }
    }
}
