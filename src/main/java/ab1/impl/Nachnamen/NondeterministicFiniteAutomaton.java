package ab1.impl.Nachnamen;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.*;

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
        } else throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Character>[][] getTransitions() {
        Set<Character>[][] transitions = new Set[this.numStates][this.numStates];

        for (int i = 0; i < this.numStates; i++) {
            for (int j = 0; j < this.numStates; j++) {
                transitions[i][j] = new HashSet<>();
            }
        }

        this.transitions.forEach(transition -> transitions[transition.getFromState()][transition.getToState()].add(transition.getReading()));

        return transitions;
    }

    /**
     * Returns the transitions exactly how they're saved in the object
     *
     * @return The set of transitions
     */
    public Set<Transition> getRawTransitions() {
        return this.transitions;
    }

    /**
     * Add a new transition to this FA.
     *
     * @param fromState fromState
     * @param c         character
     * @param toState   toState
     * @throws IllegalStateException     State is not in NFA
     * @throws IllegalCharacterException Character is not in Alphabet
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
     * @param fromState Starting state
     * @param c Character
     * @throws IllegalStateException State is not in NFA
     */
    @Override
    public void clearTransitions(int fromState, Character c) throws IllegalStateException {
        if (numStates < fromState) {
            throw new IllegalStateException();
        }
        transitions.removeIf(t -> t.getFromState() == fromState && t.getReading() == c);
    }

    /**
     * Get all possible next states for character c and State state from transitions.
     *
     * @param state State
     * @param c     Character
     * @return Set of next states
     * @throws IllegalCharacterException Character is not in alphabet
     * @throws IllegalStateException State is not in NFA
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

        Set<Character> alphabet = new HashSet<>(this.alphabet);
        alphabet.addAll(a.getAlphabet());

        Set<Integer> acceptingStates = new HashSet<>();
        Set<Integer> acceptingStatesThis = new HashSet<>();
        Set<Integer> acceptingStatesA = new HashSet<>();

        this.acceptingStates.forEach(state -> acceptingStatesThis.add(state + 1));
        a.getAcceptingStates().forEach(state -> acceptingStatesA.add(state + this.numStates + 1));

        acceptingStates.addAll(acceptingStatesThis);
        acceptingStates.addAll(acceptingStatesA);

        NFA unionNFA = new NondeterministicFiniteAutomaton(this.numStates + a.getNumStates() + 1, alphabet, acceptingStates, 0);

        this.transitions.forEach(transition -> unionNFA.setTransition(transition.getFromState() + 1, transition.getReading(), transition.getToState() + 1));

        ((NondeterministicFiniteAutomaton) a).getRawTransitions().forEach(transition -> unionNFA.setTransition(transition.getFromState() + this.numStates + 1, transition.getReading(), transition.getToState() + this.numStates + 1));

        unionNFA.setTransition(0, null, this.initialState + 1);
        unionNFA.setTransition(0, null, a.getInitialState() + this.numStates + 1);

        return unionNFA;
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

        Set<Character> alphabet = new HashSet<>(this.alphabet);
        alphabet.addAll(a.getAlphabet());

        Set<Integer> acceptingStates = new HashSet<>(this.acceptingStates);
        Set<Integer> acceptingStatesA = new HashSet<>(a.getAcceptingStates());

        a.getAcceptingStates().forEach(state -> acceptingStatesA.add(state + this.numStates + 1));

        acceptingStates.addAll(acceptingStatesA);

        NFA concatNFA = new NondeterministicFiniteAutomaton(this.numStates + a.getNumStates() + 1, alphabet, acceptingStates, this.initialState);

        this.transitions.forEach(transition -> concatNFA.setTransition(transition.getFromState(), transition.getReading(), transition.getToState()));

        ((NondeterministicFiniteAutomaton) a).getRawTransitions().forEach(transition -> concatNFA.setTransition(transition.getFromState() + this.numStates + 1, transition.getReading(), transition.getToState() + this.numStates + 1));

        concatNFA.setTransition(this.numStates, null, a.getInitialState() + this.numStates + 1);

        return null;
    }

    @Override
    public NFA complement() {

        Set<Integer> acceptingStates = new HashSet<>();

        for (int i = 0; i < this.numStates; i++) {
            if (!this.acceptingStates.contains(i)) acceptingStates.add(i);
        }

        NFA complementNFA = new NondeterministicFiniteAutomaton(this.numStates, this.alphabet, acceptingStates, this.initialState);

        this.transitions.forEach(transition -> complementNFA.setTransition(transition.getFromState(), transition.getReading(), transition.getToState()));

        // TODO Add trap state

        return complementNFA;
    }

    @Override
    public NFA kleeneStar() {

        Set<Integer> acceptingStates = new HashSet<>(this.acceptingStates);

        acceptingStates.add(this.initialState);

        NFA kleeneStarNFA = new NondeterministicFiniteAutomaton(this.numStates, this.alphabet, acceptingStates, this.initialState);

        acceptingStates.forEach(state -> kleeneStarNFA.setTransition(state, null, this.initialState));

        this.transitions.forEach(transition -> kleeneStarNFA.setTransition(transition.getFromState(), transition.getReading(), transition.getToState()));

        return kleeneStarNFA;
    }

    @Override
    public NFA plus() {

        Set<Integer> acceptingStates = new HashSet<>(this.acceptingStates);

        acceptingStates.forEach(state -> this.setTransition(state, null, this.initialState));

        NFA plusNFA = new NondeterministicFiniteAutomaton(this.numStates, this.alphabet, acceptingStates, this.initialState);

        this.transitions.forEach(transition -> plusNFA.setTransition(transition.getFromState(), transition.getReading(), transition.getToState()));

        return plusNFA;
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
                    // Look up if the subState in subStates is already a state of the new DFA
                    // in order to not create duplicate states
                    if (ez.containsValue(theOGSubset)) {
                        for (Integer key : ez.keySet()) {
                            if (ez.get(key).equals(theOGSubset)) {
                                dfa.setNumStates(numStatesDFA);
                                transitionsDFA.add(new Transition(i, key, c));
                                dfa.setTransition(i, c, key);
                            }
                        }
                    }
                    // If the state was not created yet, set Transition to a new state.
                    else {
                        dfa.setNumStates(++numStatesDFA);
                        transitionsDFA.add(new Transition(i, numStatesDFA - 1, c));
                        dfa.setTransition(i, c, numStatesDFA - 1);
                    }
                }
                // Safe the subset of all created states to calculate further
                if (!theOGSubset.isEmpty() && !ez.containsValue(theOGSubset) && c != null) {
                    ez.put(i + 1 + currentChar, new HashSet<>(theOGSubset));
                }
                theOGSubset.clear();
                if (c != null) {
                    currentChar++;
                }
            }
        }
        DFA dfaResult = new DeterministicFiniteAutomaton(numStatesDFA, alphabet, getAcceptingStatesDFAFromIndex(ez), 0);
        for (Transition t : transitionsDFA) {
            //System.out.println(t.getFromState() + " " + t.getReading() + " " + t.getToState());
            dfaResult.setTransition(t.getFromState(), t.getReading(), t.getToState());
        }
        return dfaResult;
    }

    /**
     * Find the accepting states for a DFA from the index created from the NFA
     *
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
     *
     * @param initialState The start to start searching from
     * @param c            The character to check
     * @return Set of states that are reachable.
     */
    private Set<Integer> findReachableStates(int initialState, Character c) {
        // Find all reachable states with c or epsilon and add them to a subset for the DFA
        Set<Integer> nextStatesForC = getNextStates(initialState, c);
        Set<Integer> states = new HashSet<>(nextStatesForC);
        for (Integer state : nextStatesForC) {
            states.addAll(findReachableStatesEpsilon(state));
        }
        return states;
    }

    /**
     * Recursively finds all states that are reachable without epsilon transitions from a given state
     *
     * @return Set reachable states
     */
    private Set<Integer> findReachableAcceptingStatesNonEpsilon(int initialState) {
        return findReachableAcceptingStatesNonEpsilon(initialState, new HashSet<>());
    }

    private Set<Integer> findReachableAcceptingStatesNonEpsilon(int initialState, Set<Integer> resultStates) {
        if (resultStates.contains(initialState)) return resultStates;

        this.alphabet.stream().filter(Objects::nonNull).forEach(character ->
                getNextStates(initialState, character).forEach(state -> {
                    if (this.acceptingStates.contains(state)) {
                        resultStates.add(state);
                    }
                    findReachableAcceptingStatesNonEpsilon(state, resultStates);
                })
        );
        return resultStates;
    }

    /**
     * Recursively find all reachable states via epsilon transitions from a given initialState.
     *
     * @param initialState The state to start searching from
     * @param resultStates The set of reachable states
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
        return this.acceptingStates.size() == 0;
    }

    @Override
    public Boolean acceptsEpsilonOnly() {
        boolean acceptsEpsilonOnly = acceptsEpsilon();

        if (acceptsEpsilonOnly) {
            acceptsEpsilonOnly = this.acceptingStates.stream().noneMatch(state -> findReachableAcceptingStatesNonEpsilon(state).size() > 0);
        }

        return acceptsEpsilonOnly;
    }

    @Override
    public Boolean acceptsEpsilon() {

        boolean acceptsEpsilon = this.acceptingStates.contains(this.initialState);

        if (!acceptsEpsilon) {
            acceptsEpsilon = this.acceptingStates.stream().anyMatch(state ->
                    findReachableStatesEpsilon(this.initialState).contains(state)
            );
        }

        return acceptsEpsilon;
    }

    @Override
    public boolean subSetOf(NFA b) {
        // TODO
        return false;
    }
}
