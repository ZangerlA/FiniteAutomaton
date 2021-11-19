package ab1.impl.Nachnamen;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    /**
     * i is FromState, j is ToState, value is possible characters for this transition.
     * @return Transition Matrix
     */
    @Override
    public Set<Character>[][] getTransitions() {
        Set<Character>[][] result = new Set[numStates][numStates];
        Set<Character> charSet = new HashSet<>();

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                charSet.clear();
                for (Transition t : transitions) {
                    if (t.getFromState() == i && t.getToState() == j) {
                        charSet.add(t.getReading());
                    }
                }
                result[i][j] = new HashSet<>(charSet);
            }
        }
        return result;
    }

    /**
     * Add a new transition to this FA.
     *
     * @param fromState fromState
     * @param c
     * @param toState toState
     * @throws IllegalStateException
     * @throws IllegalCharacterException
     */
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

    /**
     * Remove all Transitions with character c and fromState from transitions
     *
     * @param fromState Ausgangszustand
     * @param c das Zeichen
     * @throws IllegalStateException
     */
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

    /**
     * Get all possible next states for character c and State state from transitions.
     *
     * @param state State
     * @param c Character
     * @return
     * @throws IllegalCharacterException
     * @throws IllegalStateException
     */
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

    /**
     * Calculates the states and transitions for a DFA that equals this NFA.
     * This method is a mess. Sorry.
     *
     * @return dfaResult DFA derived from this NFA
     */
    @Override
    public DFA toDFA() {
        int numStatesDFA = 1;
        // This acts as an index for the subStates required to calculate the DFA.
        HashMap<Integer, Set<Integer>> subStateIndex = new HashMap<>();
        Set<Transition> transitionsDFA = new HashSet<>();
        Set<Integer> initialState = new HashSet<>();
        initialState.add(this.initialState);
        initialState.addAll(findReachableStatesEpsilon(this.initialState));
        subStateIndex.put(0, initialState);

        // i is the current State in the new DFA
        for (int i = 0; i < numStatesDFA; i++) {
            Set<Integer> subStates = new HashSet<>();
            // Find the possible States for every character in the alphabet from the state i.
            for (Character c : alphabet) {
                for (Integer state : subStateIndex.get(i)) {
                    subStates.addAll(findReachableStates(state, c));
                }
                // If there are any reachable states with character c from State i...
                if (!subStates.isEmpty() && c != null) {
                    // Look up if the subStates in subStates is already a state of the new DFA
                    // in order to not create duplicate states
                    if (subStateIndex.containsValue(subStates)) {
                        for (Integer key : subStateIndex.keySet()) {
                            if (subStateIndex.get(key).equals(subStates)) {
                                transitionsDFA.add(new Transition(i,key,c));
                            }
                        }
                    }
                    // If the state was not created yet, set Transition to a new state and safe in index.
                    else {
                        numStatesDFA++;
                        transitionsDFA.add(new Transition(i,numStatesDFA-1,c));
                        subStateIndex.put(numStatesDFA-1, new HashSet<>(subStates));
                    }
                }
                subStates.clear();
            }
        }
        DFA dfaResult = new DeterministicFiniteAutomaton(numStatesDFA, alphabet, getAcceptingStatesDFAFromIndex(subStateIndex), 0);
        for (Transition t : transitionsDFA) {
            dfaResult.setTransition(t.getFromState(), t.getReading(), t.getToState());
        }
        return dfaResult;
    }

    /**
     * Find the accepting states for a DFA from the index created from the NFA
     * @param subStateIndex the index for subStates
     * @return all accepting States
     */
    private Set<Integer> getAcceptingStatesDFAFromIndex(Map<Integer, Set<Integer>> subStateIndex) {
        Set<Integer> acceptingStatesResult = new HashSet<>();
        for (Integer i : subStateIndex.keySet()) {
            for (Integer state : subStateIndex.get(i)) {
                if (this.acceptingStates.contains(state)) {
                    acceptingStatesResult.add(i);
                }
            }
        }
        return acceptingStatesResult;
    }

    /**
     * Find all reachable states with the given character c from state initialState.
     * @param initialState
     * @param c
     * @return Set of states that are reachable.
     */
    private Set<Integer> findReachableStates(int initialState, Character c) {
        Set<Integer> states = new HashSet<>();
        // Find all reachable states with c or epsilon and add them to a subset for the DFA
        Set<Integer> nextStatesForC = getNextStates(initialState,c);
        states.addAll(nextStatesForC);
        for (Integer state : nextStatesForC) {
            states.addAll(findReachableStatesEpsilon(state));
        }
        for (Integer i : findReachableStatesEpsilon(initialState)) {
            states.addAll(getNextStates(i, c));
        }
        return states;
    }

    /**
     * Recursively find all reachable states via epsilon transitions from a given initialState.
     * @param initialState
     * @param resultStates
     * @return Set of states that are reachable.
     */
    private Set<Integer> findReachableStatesEpsilon(int initialState, Set<Integer> resultStates) {
        if (resultStates.contains(initialState)) {
            return resultStates;
        }
        for (Integer state : getNextStates(initialState, null)) {
            resultStates.add(state);
            findReachableStatesEpsilon(state, resultStates);
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
        return this.toDFA().acceptsNothing();
    }

    @Override
    public Boolean acceptsEpsilonOnly() {
        return this.toDFA().acceptsEpsilonOnly();
    }

    @Override
    public Boolean acceptsEpsilon() {
        return this.toDFA().acceptsEpsilon();
    }

    @Override
    public boolean subSetOf(NFA b) {
        // TODO
        return false;
    }
}
