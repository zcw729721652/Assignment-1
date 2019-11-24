package Assignment1;

import java.io.*;
import java.util.List;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import analysis.CFGExtractor;
import analysis.Graph;
import analysis.Node;

public class Task3 extends CFGExtractor {

	// gets the number of LOC in a method
	public static int getLOC(String owner, MethodNode mn) throws AnalyzerException {
		final Graph g = buildGraph(owner, mn);
		return g.getNodes().size();
	}

	// gets the number of Cyclomatic	Complexity in a method
	public static int getCyclomatic(String owner, MethodNode mn) throws AnalyzerException {
		final Graph g = buildGraph(owner, mn);
		int branches = 0;
		for (Node n : g.getNodes()) {
			//when a node has more than one successor, it is a branch
			if (g.getSuccessors(n).size() > 1) {
				branches++;
			}
		}
		return branches+1;
	}

	// gets the number of attributes in a class
	public int getNumInstructions(ClassNode owner) throws AnalyzerException {
		// return owner.attrs.size();
		return owner.fields.size();
	}

	// gets the number of Function Point in a class
	public int getNumFunction(ClassNode owner) throws AnalyzerException {
		return owner.methods.size();
	}

}
