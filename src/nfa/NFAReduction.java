package nfa;

import graph.*;

import java.io.*;
import java.util.*;

import utils.*;


/**
 * Reduces an NFA given the sets of left- and right-equivalent states
 * @author duncan
 *
 */
public class NFAReduction {
	
	/**
	 * Reduces an NFA and relabels all merged states
	 * @param m the NFA
	 * @return
	 */
	public static void reduce(NFA m) {
		reduce(m, true);
	}
	
	/**
	 * Reduces an NFA
	 * @param m the NFA
	 * @param relabel if true, relabels all merged states
	 * @return
	 */
	public static void reduce(NFA m, boolean relabel) {
		NFAEquivalence E = new NFAEquivalence(m);
		DisjointSets left = E.getLeft();
		DisjointSets right = E.getRight();
		
		ArrayList<Set<Integer>> sets = left.getSets();
		int n = sets.size();
		sets.addAll(right.getSets());
		sets.add(0, null);			//1-index for matching
		int nSets = sets.size();
		
		//build the bipartite graph
		UndirectedGraph<Edge> bipartite = new UndirectedGraph<>(nSets);
		for(int i = 0; i < m.numStates(); i++)
			bipartite.addEdge(new Edge(left.find(i) + 1, n + right.find(i) + 1));
		
		//find the maximum matching & vertex cover
		BipartiteMatching maxMatch = new BipartiteMatching(bipartite, n + 1);
		boolean[] marked = findVertexCover(bipartite, maxMatch);
		
		//merge the sets together and remove duplicates
		boolean[] inMinSet = new boolean[m.numStates()];
		
//		System.out.println("Merged states: ");
		for(int i = 1; i < nSets; i++)
			if(!marked[i] && i <= n || marked[i] && i > n) {
				Set<Integer> set = sets.get(i);
				Iterator<Integer> it = set.iterator();
				while(it.hasNext()) {	//remove duplicates
					int q = it.next();
					if(inMinSet[q])
						it.remove();
					else
						inMinSet[q] = true;
				}
				if(set.size() > 1) {
					if(i <= n)
						mergeLeft(m, sets.get(i));
					else
						mergeRight(m, sets.get(i));
//					System.out.println(set);
				}
			}
		if(relabel)
			m.relabel();
	}
	
	/**
	 * Given a bipartite graph and a maximum matching, finds the vertex cover
	 * @param bipartite
	 * @param maxMatch
	 * @return a boolean array marked where the vertex cover is v s.t. !marked[v] and v <= n
	 * or marked[v] and v > n
	 */
	private static boolean[] findVertexCover(UndirectedGraph<Edge> bipartite,
			BipartiteMatching maxMatch) {
		int nSets = bipartite.order();
		Set<Edge> matching = maxMatch.getMatching();
		//create the residual graph
		Stack<Integer> stack = new Stack<Integer>();
		DirectedGraph<Edge> residual = new DirectedGraph<Edge>(nSets);
		for(Edge uv : bipartite.edges()) {
			int u = uv.from();
			int v = uv.to();
			if(matching.contains(uv))
				residual.addEdge(new Edge(v, u));
			else
				residual.addEdge(uv);
			if(!maxMatch.isMatched(u))
				stack.add(u);
		}
		//dfs on free vertices
		boolean[] marked = new boolean[nSets];
		while(!stack.isEmpty()) {
			int q = stack.pop();
			marked[q] = true;
			for(Edge uv : residual.edgesOf(q)) {
				if(!marked[uv.to()])
					stack.push(uv.to());
			}
		}
		return marked;
	}
	
	/**
	 * Given a collection of right-equivalent states, merges them together
	 */
	public static void mergeRight(NFA M, Collection<Integer> q) {
		int v = -1;
		for(int w : q) {
			if(v == -1)
				v = w;
			else
				M.mergeRight(v, w);
		}
	}
	
	/**
	 * Given a collection of left-equivalent states, merges them together
	 */
	private static void mergeLeft(NFA M, Collection<Integer> q) {
		int v = -1;
		for(int w : q) {
			if(v == -1)
				v = w;
			else
				M.mergeLeft(v, w);
		}
	}
	
	public static void writeToFile(String contents, String fileName) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
		br.write(contents);
		br.close();
	}
	
	public static void main(String[] args) {
		try {
			Reader in;
			if(args.length == 0)
				in = new InputStreamReader(System.in);
			else
				in = new FileReader(new File(args[0]));
			NFA M = new NFA(in);
			NFAReduction.reduce(M);
			//output
			String output = M.toString();
			System.out.print(output);
		} catch(IOException e) {
			System.out.println(e);
		} catch(NFAException e) {
			System.out.println(e);
		}
	}
	
}
