package ab1.impl.Nachnamen;

import java.util.Set;

import ab1.Ab1;
import ab1.DFA;
import ab1.NFA;

public class Ab1Impl implements Ab1 {

	@Override
	public NFA createNFA(int numStates, Set<Character> characters, Set<Integer> acceptingStates, int initialState) {
		return new NondeterministicFiniteAutomaton(numStates, characters, acceptingStates, initialState);
	}

	@Override
	public DFA createDFA(int numStates, Set<Character> characters, Set<Integer> acceptingStates, int initialState) {
		return new DeterministicFiniteAutomaton(numStates, characters, acceptingStates, initialState);
	}
}
