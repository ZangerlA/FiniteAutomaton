package ab1.impl.Nachnamen;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NondeterministicFiniteAutomaton implements NFA {

    int numStates;
    Set<Character> alphabet;
    Set<Integer> acceptingStates;
    int initialState;
    Set<Transition> transitions;
    int currentState;

    public NondeterministicFiniteAutomaton(int numStates, Set<Character> alphabet, Set<Integer> acceptingStates, int initialState) {
        this.numStates = numStates;
        this.alphabet = alphabet;
        this.acceptingStates = acceptingStates;
        this.currentState = initialState;
        transitions = new HashSet<>();
        alphabet.add(null);
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
        int numStatesDFA = 1;
        HashMap<Integer, Set<Integer>> ez = new HashMap<>();
        Set<Integer> subStates = new HashSet<>();
        DFA dfa = new DeterministicFiniteAutomaton(numStatesDFA, alphabet, acceptingStates, 0);
        for (int i = 0; i < numStatesDFA;) {
            for (Character c : alphabet) {
                Set<Integer> subSubStates = new HashSet<>();
                for (Integer state : subStates) {
                    subSubStates = findReachableStates(state, c);
                    subStates.addAll(subSubStates);
                }
                if (!subStates.isEmpty()) {
                    // IF state in hashmap, dont create new one, set transition to existing state
                    dfa.setTransition(numStatesDFA, c, numStatesDFA++);
                }
                ez.put(i++, subStates);
            }
        }
        return new DeterministicFiniteAutomaton(numStatesDFA, alphabet, acceptingStates, 0);
    }

    private Set<Integer> findReachableStates(int initialState, Character c) {
        Set<Integer> states = new HashSet<>();
        // Find all reachable states with c or epsilon and add them to a subset for the DFA
        Set<Integer> nextStatesForC = getNextStates(initialState,c);
        states.addAll(nextStatesForC);
        for (Integer state : nextStatesForC) {
            states.addAll(findReachableStatesEpsilon(state));
        }
        return states;
    }

    private Set<Integer> findReachableStatesEpsilon(int initialState, Set<Integer> resultStates) {
        if (resultStates.contains(initialState)) {
            return resultStates;
        }
        for (Integer state : getNextStates(initialState, null)) {
            findReachableStatesEpsilon(state, resultStates);
            resultStates.add(state);
        }
        return resultStates;
    }

    private Set<Integer> findReachableStatesEpsilon(int initialState) {
        return findReachableStatesEpsilon(initialState, new HashSet<>());
    }

    @Override
    public Boolean accepts(String w) throws IllegalCharacterException {
        return this.toDFA().accepts(w);
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
