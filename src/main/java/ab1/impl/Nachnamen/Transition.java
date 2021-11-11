package ab1.impl.Nachnamen;

public class Transition {

    private int fromState;
    private int toState;
    private char reading;

    public Transition(int fromState, int toState, char reading) {
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

    public char getReading() {
        return reading;
    }
}
