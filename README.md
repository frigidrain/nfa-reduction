nfa-reduction
=============

Compiling
-------------

The source is compiled into build/classes using Apache Ant (you must have Apache Ant installed):

```
$ ant
```
Use the bash scripts provided to run the tool:

`min-dfa inFile` will minimize the DFA specified by inFile.

`reduce-nfa inFile` will reduce the NFA specified by inFile.  Also accepts pipe input.

Input Format
-------------

Input begins with a line containing a single integer, `n`, specifying the number of states (the states are numbered from `0 .. n - 1`), followed by a string of characters  specifying the alphabet.

The next line contains a set of integers separated by spaces specifying the initial states, followed by another line containing a set of integers separated by spaces specifying the final states.

The rest of lines contain two integers, `a` and `b`, and a character, `c`, separated by spaces, indicating that the transition function maps state `a` to state `b` through symbol `c` where 0 <= `a`, `b` < `n` and `c` belongs to the alphabet.

Sample Input
-------------

	8
	ab
	0 1 2
	7
	0 3 a
	1 3 a
	2 4 a
	3 5 a
	4 5 b
	4 6 a
	5 7 a
	6 7 a
