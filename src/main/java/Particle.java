import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Particle {

    private final int id;
    private final double radius, v;
    private final State state;

    public Particle(int id, double x, double y, double radius, double v, double theta) {
        this.id = id;
        this.radius = radius;
        this.v = v;
        this.state = new State(x,y,v,theta);
    }

    public Particle nextState(double newTheta, double L, boolean periodicOutline, int lastFrame) {
        return new Particle(id, nextPosition(state.getX(), state.getVX(), L),
                nextPosition(state.getY(), state.getVY(), L),
                0, this.v, newTheta);
    }

    // SIEMPRE TIENE PERIODIC OUTLINE
    private double nextPosition(double coord, double relV, double L) {
        double aux = (coord + relV) % L;
        return aux < 0 ? aux + L : aux;
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

    public double calculateDistance(Particle p, double L, boolean periodicOutline) {
        double x = distanceFromAxis(state.getX(), p.getState().getX(), L, periodicOutline);
        double y = distanceFromAxis(state.getY(), p.getState().getY(), L, periodicOutline);

        return Math.sqrt(x*x + y*y) - this.radius - p.getRadius();
    }


    // Getters
    public int getId() { return id; }
    public State getState() { return state; }
    public double getV() { return v; }
    public double getRadius() { return radius; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;
        Particle particle = (Particle) o;
        return id == particle.id && radius == particle.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, radius);
    }

//    @Override
//    public String toString() {
//        StringBuilder buffer = new StringBuilder("Particle{ ID = " + id + ", V = " + v + ", Radius = " + radius);
//        buffer.append("\n\tStates = { ");
//        int i=0;
//        for(State s : states) {
//            buffer.append("t");
//            buffer.append(i++);
//            buffer.append(s);
//        }
//        buffer.append(" }");
//        return buffer.toString();
//    }


    public static class State{

        private final double x, y;
        private final double vx, vy;
        private final double theta;

        public State(double x, double y, double v, double theta) {
            this.x = x;
            this.y = y;
            this.vx = v*Math.cos(theta);
            this.vy = v*Math.sin(theta);
            this.theta = theta;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getTheta() {
            return theta;
        }

        public double getVX() {
            return vx;
        }

        public double getVY() {
            return vy;
        }

//        @Override
//        public String toString() {
//            return " [" +
//                    "x=" + x +
//                    ", y=" + y +
//                    ", vx=" + vx +
//                    ", vy=" + vy +
//                    ", theta=" + theta +
//                    "]";
//        }
    }
}
