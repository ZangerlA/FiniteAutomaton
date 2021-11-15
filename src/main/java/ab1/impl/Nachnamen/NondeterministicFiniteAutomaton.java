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

    @Override
    public Set<Character>[][] getTransitions() {
        // TODO
        return new Set[0][];
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
        HashMap<Integer, Set<Integer>> ez = new HashMap<>();
        Set<Transition> transitionsDFA = new HashSet<>();
        Set<Integer> initialState = new HashSet<>();
        initialState.add(0);
        ez.put(0, initialState);
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton(numStatesDFA, alphabet, acceptingStates, 0);
        // i is the current State in the new DFA
        for (int i = 0; i < numStatesDFA; i++) {
            Set<Integer> theOGSubset = new HashSet<>();
            int currentChar = 0;
            // Find the possible States for every character in the alphabet from the state i.
            for (Character c : alphabet) {
                for (Integer state : ez.get(i)) {
                    theOGSubset.addAll(findReachableStates(state, c));
                }
                // If there are any reachable states with character c from State i...
                if (!theOGSubset.isEmpty() && c != null) {
                    // Look up if the subStates in subStates is already a state of the new DFA
                    // in order to not create duplicate states
                    if (ez.containsValue(theOGSubset)) {
                        for (Integer key : ez.keySet()) {
                            if (ez.get(key).equals(theOGSubset)) {
                                dfa.setNumStates(numStatesDFA);
                                transitions.add(new Transition(i,key,c));
                                dfa.setTransition(i, c, key);
                            }
                        }
                    }
                    // If the state was not created yet, set Transition to a new state.
                    else {
                        numStatesDFA++;
                        dfa.setNumStates(numStatesDFA);
                        transitions.add(new Transition(i,i+1+currentChar,c));
                        dfa.setTransition(i, c, i+1+currentChar);
                    }
                }
                // Safe the subset of all created states to calculate further
                if (!theOGSubset.isEmpty() && !ez.containsValue(theOGSubset) && c != null) {
                    ez.put(i+1+currentChar, new HashSet<>(theOGSubset));
                }
                theOGSubset.clear();
                if (c != null) {
                    currentChar++;
                }
            }
        }
        DFA dfaResult = new DeterministicFiniteAutomaton(numStatesDFA, alphabet, getAcceptingStatesDFAFromIndex(ez), 0);
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
        for (Set<Integer> states : subStateIndex.values()) {
            for (Integer state : states) {
                if (this.acceptingStates.contains(state)) {
                    acceptingStatesResult.add(state);
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
