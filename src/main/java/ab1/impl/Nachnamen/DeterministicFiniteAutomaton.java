package ab1.impl.Nachnamen;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.HashSet;
import java.util.Set;

public class DeterministicFiniteAutomaton extends NondeterministicFiniteAutomaton implements DFA {


    public DeterministicFiniteAutomaton(int numStates, Set<Character> alphabet, Set<Integer> acceptingStates, int initialState) {
        super(numStates, alphabet, acceptingStates, initialState);
    }

    @Override
    public void reset() {
        currentState = super.initialState;
    }

    @Override
    public int getCurrentState() {
        return currentState;
    }

    /**
     * Get the next state and set the current state to the next state, return -1 if there is no transition(Trap)
     * @param c das abzuarbeitende Zeichen
     * @return the current State
     * @throws IllegalCharacterException
     * @throws IllegalStateException
     */
    @Override
    public int doStep(char c) throws IllegalCharacterException, IllegalStateException {
        currentState = getNextState(currentState, c);
        if (currentState == null) {
            return -1;
        }
        return currentState;
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
        // Null (Trap) when there is no existing transition for this character and currentState
        return null;
    }

    public void setNumStates(int numberOfStates) {
        super.numStates = numberOfStates;
    }

    @Override
    public boolean isInAcceptingState() {
        return super.acceptingStates.contains(currentState);
    }

    /**
     * Read a word character by character and follow transitions, if ending in an accepting state return true
     * @param w das abzuarbeitende Wort
     * @return Returns true if the DFA accepts this word
     * @throws IllegalCharacterException
     */
    @Override
    public Boolean accepts(String w) throws IllegalCharacterException {
        this.reset();
        char[] word = w.toCharArray();
        for (int i = 0; i <= word.length; i++) {
            if (i == word.length && isInAcceptingState()) {
                return true;
            }
            if (currentState.equals(trapState)) {
                return false;
            } else if (i < word.length) {
                try {
                    if (doStep(word[i]) == -1) {
                        return false;
                    }
                } catch (IllegalCharacterException | IllegalStateException e) {
                    return false;
                }

            }
        }
        return false;
    }

    /**
     * Add a trap State and its transitions to this DFA. Used for complement.
     */
    public void addTrapState() {
        super.trapState = this.getNumStates();
        this.setNumStates(this.getNumStates() + 1);
        for (int i = 0; i < this.getNumStates() - 1; i++) {
            Set<Character> tempAlphabet = new HashSet<>(this.alphabet);
            tempAlphabet.remove(null);
            for (Transition t : transitions) {
                if (t.getFromState() == i) {
                    tempAlphabet.remove(t.getReading());
                }
            }
            setTransitionToTrapState(i, tempAlphabet, trapState);
        }
    }

    private void setTransitionToTrapState(int state, Set<Character> characters, int trapState) {
        for (Character c : characters) {
            this.setTransition(state, c, trapState);
        }
        for (Character c : alphabet) {
            if (c != null) {
                this.setTransition(trapState, c, trapState);
            }
        }
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
     * See NFA.equals()
     */
    @Override
    public boolean equals(Object b) {
        DFA dfaB;
        if (b instanceof DFA) {
            dfaB = ((DFA) b);
        } else if (b instanceof NFA) {
            dfaB = ((NFA) b).toDFA();
        } else {
            throw new IllegalArgumentException("b should be of type NFA");
        }

        NFA diffAB = this.minus(dfaB);
        NFA diffBA = dfaB.minus(this);

        return diffAB.acceptsNothing() && diffBA.acceptsNothing();
    }

    @Override
    public Boolean acceptsNothing() {
        if (this.getAcceptingStates() == null || this.getAcceptingStates().isEmpty()) {
            return true;
        }
        Set<Integer> startToEnd = traverseAutomaton(initialState, new HashSet<>());
        for (Integer i : startToEnd) {
            if (getAcceptingStates().contains(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean acceptsEpsilonOnly() {
        if (!acceptingStates.contains(initialState)) {
            return false;
        }
        for (Transition t : transitions) {
            if (t.getFromState() == initialState && t.getToState() == initialState) {
                return false;
            }
        }
        boolean result = true;
        Set<Integer> reachableStates = traverseAutomaton(initialState, new HashSet<>());
        for (Integer reachable : reachableStates) {
            if (acceptingStates.contains(reachable) && reachable != initialState) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Traverse the FA and find all reachable states. (Used for acceptsEpsilonOnly() and acceptsNothing())
     * @param initialState
     * @param resultStates
     * @return All reachable states of this FA
     */
    public Set<Integer> traverseAutomaton(Integer initialState, Set<Integer> resultStates) {
        if (resultStates.contains(initialState)) {
            return resultStates;
        }
        if (transitions.isEmpty()) {
            resultStates.add(this.initialState);
            return resultStates;
        }
        for (Transition t : transitions) {
            if (t.getFromState() == initialState) {
                resultStates.add(initialState);
                traverseAutomaton(t.getToState(), resultStates);

            } else {
                resultStates.add(initialState);
            }
        }
        return resultStates;
    }

    @Override
    public Boolean acceptsEpsilon() {
        return accepts("");
    }
}
