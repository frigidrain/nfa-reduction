package nfa;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a deterministic finite automaton (DFA) as a subclass of NFA
 * Unlike NFA, this class has only one initial state and will only allow a state to have one
 * transition per symbol
 * @author duncan
 *
 */
public class DFA extends NFA {
	
	private int q0;			//initial state
	
	/**
	 * Constructs a new DFA
	 * @param numStates the number of states
	 * @param alphabet the alphabet as a String
	 * @param startState the start state
	 * @param finalStates the set of final states
	 */
	public DFA(int numStates, String alphabet, int startState, Set<Integer> finalStates) {
		super(numStates, alphabet, new HashSet<Integer>(1), finalStates);
		q0 = startState;
		I.add(q0);
	}
	
	/**
	 * Copy constructor, makes a deep copy of the argument DFA
	 * @param m
	 */
	public DFA(DFA m) {
		super(m);
		for(int i : initialStates())
			q0 = i;
	}

	/**
	 * Reads a DFA from file
	 * Input format: Input begins with a line containing a single integer, n, specifying the number
	 * of states (including state 0), followed by a line of characters (no space) specifying the alphabet.
	 * The next line contains one integer specifying the initial state,
	 * followed by another line containing a set of integers separated by spaces specifying the final states.
	 * The rest of lines contain two integers, a and b, and a character, c, separated by spaces,
	 * indicating that the transition function maps state a to state b through symbol c where
	 * 0 <= a, b < n and c belongs to the alphabet.
	 * @param file the filename
	 * @throws IOException
	 */
	public DFA(File file) throws IOException {
		super(file);
		if(initialStates().size() > 1)
			throw new NFAException("A DFA cannot have more than one initial state");
		for(int i : initialStates())
			q0 = i;
	}
	
	/**
	 * Returns the single initial state of the DFA
	 * @return
	 */
	public int initialState() {
		return q0;
	}
	
	public boolean isInitial(int q) {
		return q == q0;
	}
	
	protected void checkAddTransition(Transition t, int i) {
		if(t.symbol() == EPSILON)
			throw new NFAException("DFAs cannot have epsilon transitions");
		if(transitionsFrom(t.from()).contains(t))
			throw new NFAException("there is already a transition with symbol " + t.symbol() + " from " + t.from());
	}
	
}
