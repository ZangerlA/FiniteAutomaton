package ab1.impl.Nachnamen;

import ab1.DFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.Set;

public class DeterministicFiniteAutomaton extends NondeterministicFiniteAutomaton implements DFA {

    int currentState;

    public DeterministicFiniteAutomaton(int numStates, Set<Character> alphabet, Set<Integer> acceptingStates, int initialState) {
        super(numStates, alphabet, acceptingStates, initialState);
        currentState = 0;
    }

    @Override
    public void reset() {
        currentState = 0;
    }

    @Override
    public int getCurrentState() {
        return currentState;
    }

    @Override
    public int doStep(char c) throws IllegalCharacterException, IllegalStateException {
        return currentState = getNextState(currentState, c);
    }

    @Override
    public Integer getNextState(int s, char c) throws IllegalCharacterException, IllegalStateException {
        if (!super.alphabet.contains(c)) {
            // If alphabet does not contain character c
            throw new IllegalCharacterException();
        }
        if (super.numStates < s) {
            // If State s is not contained by this FA
            throw new IllegalStateException();
        }
        for (Transition t : super.transitions) {
            if (t.getFromState() == s && t.getReading() == c) {
                return t.getToState();
            }
        }
        // Throw when there is no existing transition for this character and currentState
        throw new IllegalStateException();
    }

    @Override
    public boolean isInAcceptingState() {
        if (super.acceptingStates.contains(currentState)) {
            return true;
        }
        else return false;
    }
}
