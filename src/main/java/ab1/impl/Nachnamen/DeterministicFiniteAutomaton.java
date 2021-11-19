package ab1.impl.Nachnamen;

import ab1.DFA;
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

    public void setNumStates(int numberOfStates) {
        super.numStates = numberOfStates;
    }

    @Override
    public boolean isInAcceptingState() {
        if (super.acceptingStates.contains(currentState)) {
            return true;
        } else return false;
    }

    @Override
    public Boolean accepts(String w) throws IllegalCharacterException{
        char [] word = w.toCharArray();
        for (int i = 0; i <= word.length; i++) {
            if (i == word.length && isInAcceptingState()){
                return true;
            }
            else if (i < word.length){
                try {
                    doStep(word[i]);
                }
                catch (IllegalCharacterException e){
                    return false;
                }
                catch (IllegalStateException e){
                    return false;
                }

            }
        }
        return false;
    }

    @Override
    public Boolean acceptsNothing() {
        if (this.getAcceptingStates() == null || this.getAcceptingStates().isEmpty()){
            return true;
        }
        Set<Integer> starttoend = traverseAutomaton(initialState, new HashSet<>());
        for (Integer i: starttoend) {
            if (getAcceptingStates().contains(i)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean acceptsEpsilonOnly() {
        Set<Integer> reachableStates = traverseAutomaton(initialState, new HashSet<>());
        for (Integer reachable : reachableStates) {
            if (acceptingStates.contains(reachable) && reachable != initialState) {
                return false;
            }
        }
        return true;
    }

    public Set<Integer> traverseAutomaton(Integer initialState, Set<Integer> resultStates) {
        if (resultStates.contains(initialState)) {
            return resultStates;
        }
        for (Transition t : transitions) {
            if (t.getFromState() == initialState) {
                resultStates.add(initialState);
                traverseAutomaton(t.getToState(), resultStates);

            }
            else {
                resultStates.add(initialState);
            }
        }
        return resultStates;
    }

    @Override
    public Boolean acceptsEpsilon() {
        return accepts("");
    }

    public Boolean initialToEndState(){
        Set<Integer> cStates = new HashSet<Integer>();
        cStates.add(initialState);
        Set<Integer> nStates = new HashSet<Integer>();

        for (Integer i: cStates) {
            for (Character c: alphabet) {
                for (Transition t: transitions) {
                    if (t.getReading() == c && t.getFromState() == i){
                        nStates.add(t.getToState());
                    }
                }
            }
            for (Integer is: cStates) {
                if (nStates.contains(is)){
                    nStates.remove(is);
                }
            }
            cStates = nStates;
            nStates = null;
            for (Integer state: cStates) {
                if (acceptingStates.contains(state)){
                    return true;
                }
            }
        }
        return false;
    }
}
