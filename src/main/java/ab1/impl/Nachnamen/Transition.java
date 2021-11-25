package ab1.impl.Nachnamen;

public class Transition {

    private final int fromState;
    private final int toState;
    private final Character reading;

    public Transition(int fromState, int toState, Character reading) {
        this.fromState = fromState;
        this.toState = toState;
        this.reading = reading;
    }

    public int getFromState() {
        return fromState;
    }

    public int getToState() {
        return toState;
    }

    public Character getReading() {
        return reading;
    }
}
