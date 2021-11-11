package ab1.impl.Nachnamen;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.HashSet;
import java.util.Set;

public class NondeterministicFiniteAutomaton implements NFA {

    int numStates;
    Set<Character> alphabet;
    Set<Integer> acceptingStates;
    int initialState;
    Set<Transition> transitions;

    public NondeterministicFiniteAutomaton(int numStates, Set<Character> alphabet, Set<Integer> acceptingStates, int initialState) {
        this.numStates = numStates;
        this.alphabet = alphabet;
        this.acceptingStates = acceptingStates;
        this.initialState = initialState;
        transitions = new HashSet<>();
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public Set<Integer> getAcceptingStates() {
        return acceptingStates;
    }

    @Override
    public int getInitialState() {
        return initialState;
    }

    @Override
    public boolean isAcceptingState(int s) throws IllegalStateException {
        if (acceptingStates.contains(s)) {
            return true;
        }
        else throw new IllegalArgumentException();
    }

    @Override
    public Set<Character>[][] getTransitions() {
        // TODO
        return new Set[0][];
    }

    @Override
    public void setTransition(int fromState, Character c, int toState) throws IllegalStateException, IllegalCharacterException {
        if (numStates < fromState || numStates < toState) {
            throw new IllegalStateException();
        }
        if (!alphabet.contains(c)) {
            throw new IllegalCharacterException();
        }
        transitions.add(new Transition(fromState, toState, c));
    }

    @Override
    public void clearTransitions(int fromState, Character c) throws IllegalStateException {
        if (numStates < fromState) {
            throw new IllegalStateException();
        }
        for (Transition t : transitions) {
            if (t.getFromState() == fromState && t.getReading() == c) {
                transitions.remove(t);
            }
        }
    }

    @Override
    public Set<Integer> getNextStates(int state, Character c) throws IllegalCharacterException, IllegalStateException {
        if (numStates < state) {
            throw new IllegalStateException();
        }
        if (!alphabet.contains(c)) {
            throw new IllegalCharacterException();
        }
        HashSet<Integer> nextStates = new HashSet<>();
        for (Transition t : transitions) {
            if (t.getFromState() == state) {
                if (t.getReading() == c) {
                    nextStates.add(t.getToState());
                }
            }
        }
        return nextStates;
    }

    @Override
    public int getNumStates() {
        return numStates;
    }

    @Override
    public NFA union(NFA a) {
        // TODO
        return null;
    }

    @Override
    public NFA intersection(NFA a) {
        // TODO
        return null;
    }

    @Override
    public NFA minus(NFA a) {
        // TODO
        return null;
    }

    @Override
    public NFA concat(NFA a) {
        // TODO
        return null;
    }

    @Override
    public NFA complement() {
        // TODO
        return null;
    }

    @Override
    public NFA kleeneStar() {
        // TODO
        return null;
    }

    @Override
    public NFA plus() {
        // TODO
        return null;
    }

    @Override
    public DFA toDFA() {
        // TODO
        return null;
    }

    @Override
    public Boolean accepts(String w) throws IllegalCharacterException {
        // TODO
        return null;
    }

    @Override
    public Boolean acceptsNothing() {
        // TODO
        return null;
    }

    @Override
    public Boolean acceptsEpsilonOnly() {
        // TODO
        return null;
    }

    @Override
    public Boolean acceptsEpsilon() {
        // TODO
        return null;
    }

    @Override
    public boolean subSetOf(NFA b) {
        // TODO
        return false;
    }
}
