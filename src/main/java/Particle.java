import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Particle {

    private final long id;
    private final double radius, v;
    private final List<State> states;

    public Particle(long id, double x, double y, double radius, double v, double theta) {
        this.id = id;
        this.radius = radius;
        this.v = v;
        states = new ArrayList<>();
        states.add(new State(x,y,v,theta));
    }

    public void nextState(double newTheta, double L, boolean periodicOutline, int lastFrame) {
        State lastState = getState(lastFrame);
//      states.add(new State(nextPosition(lastState.getX() + lastState.getVX(), L, periodicOutline),
//                      nextPosition(lastState.getY() + lastState.getVY(), L, periodicOutline),
        states.add(new State(nextPosition(lastState.getX(), lastState.getVX(), L),
                        nextPosition(lastState.getY(), lastState.getVY(), L),
                        this.v,
                        newTheta));
    }

    // SIEMPRE TIENE PERIODIC OUTLINE
    private double nextPosition(double coord, double relV, double L) {
        double aux = (coord + relV) % L;
        return aux < 0 ? aux + L : aux;
    }

//    private double nextPosition(double coordinate, double L, boolean periodicOutline){
//        double ret = coordinate;
//
//        if(periodicOutline) {
//            if(coordinate >= L) {
//                ret = coordinate - L;
//            } else if(coordinate < 0) {
//                ret = coordinate + L;
//            }
//        }
//        return ret;
//    }

    private double distanceFromAxis(double ax1, double ax2, double L, boolean periodicOutline){
        double distance = Math.abs(ax1 - ax2);

        if (periodicOutline){
            if(distance > L/2){
                distance = L - distance;
            }
        }
        return distance;
    }

    public double calculateDistance(Particle p, double L, boolean periodicOutline, int timeFrame) {
        State state = getState(timeFrame);
        double x = distanceFromAxis(state.getX(), p.getState(timeFrame).getX(), L, periodicOutline);
        double y = distanceFromAxis(state.getY(), p.getState(timeFrame).getY(), L, periodicOutline);

        return Math.sqrt(x*x + y*y) - this.radius - p.getRadius();
    }


    // Getters
    public long getId() { return id; }
    public List<State> getStates() { return states; }
    public State getState(int timeFrame) { return states.get(timeFrame); }
    public State getLastState() { return states.get(states.size()-1); }
    public double getV() { return v; }
    public double getRadius() { return radius; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;
        Particle particle = (Particle) o;
        return id == particle.id && radius == particle.radius && Objects.equals(states, particle.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, states, radius);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("Particle{ ID = " + id + ", V = " + v + ", Radius = " + radius);
        buffer.append("\n\tStates = { ");
        int i=0;
        for(State s : states) {
            buffer.append("t");
            buffer.append(i++);
            buffer.append(s);
        }
        buffer.append(" }");
        return buffer.toString();
    }


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

        @Override
        public String toString() {
            return " [" +
                    "x=" + x +
                    ", y=" + y +
                    ", vx=" + vx +
                    ", vy=" + vy +
                    ", theta=" + theta +
                    "]";
        }
    }
}
