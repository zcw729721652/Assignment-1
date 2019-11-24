package Assignment1;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import analysis.*;

/**
 * Created by Hakeem on 06/11/2019.
 */

public class ProgramDependenceGraphTest {

	/**
	 * This is a sample test to check the results of your implementation. You can
	 * modify it to suit your taste
	 */
	Graph graph;
	ProgramDependenceGraph pdg;
	public static String s = null;

	public static String result = null;
	public static double s2 = 0;
	public static double s3 = 0;

	public ProgramDependenceGraphTest(String classPath) {
		try {
			File folder = new File(classPath);

			FileInputStream in;
			ClassReader classReader;
			ClassNode cn;

			for (File f : folder.listFiles()) {
				cn = new ClassNode(Opcodes.ASM4);
				in = new FileInputStream(f.getPath());
				classReader = new ClassReader(in);
				classReader.accept(cn, 0);

				for (MethodNode mn : (List<MethodNode>) cn.methods) {
					pdg = new ProgramDependenceGraph(cn, mn);
					graph = pdg.computeResult();
					Set<Node> nodeList = graph.getNodes();
					List<Node> list = new ArrayList<Node>(nodeList);
					Node n = list.get(list.size() / 2); // For example, let us get the backward slice of the middle Node
					System.out.println(cn.name + ", " + mn.name + ", " + testTightness() + ", " + testOverlap() + ", "
							+ testBackwardSlice(n));
					// System.out.println(graph); // this prints the digraph
				}
				in.close();
			}
			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Set<Node> testBackwardSlice(Node n) {
		return pdg.backwardSlice(n);
	}

	private double testTightness() {
		return pdg.computeTightness();
	}

	private double testOverlap() {
		return pdg.computeOverlap();
	}

	/**
	 * Returns the number of nodes in the CFG.
	 * 
	 * @param cfg
	 * @return
	 */
	private static int getNodeCount(Graph cfg) {
		return cfg.getNodes().size();
	}

	/**
	 * Returns the Cyclomatic Complexity by counting the number of branches and
	 * adding 1.
	 * 
	 * @param cfg
	 * @return
	 */
	private static int getCyclomaticComplexity(Graph cfg) {
		int branchCount = 0;
		for (Node n : cfg.getNodes()) {
			if (cfg.getSuccessors(n).size() > 1) {
				branchCount++;
			}
		}
		return branchCount + 1;
	}

	public static void main(String[] args) throws IOException {
		// Set the path to point to the class you want to test
		String path = "assignment-1-zcw729721652/build/net/sf/freecol/tools/TranslationReport$LanguageStatsRecord.class";

		// print the header to the CSV file
		System.out.println("ClassName, MethodName, Tighness, Overlap, BackWardSlice");

		File file = new File(path);

		System.out.println(new File(path).exists());

		ClassNode cn = new ClassNode(Opcodes.ASM4);
		InputStream in = new FileInputStream(file);
		ClassReader classReader = new ClassReader(in);
		classReader.accept(cn, 0);
		MethodNode mn = new MethodNode();
		for (MethodNode mn1 : (List<MethodNode>) cn.methods) {
			mn = mn1;
			System.out.println("2.1================CFG FOR: " + cn.name + "." + mn.name + " =================");

			// 1
			ProgramDependenceGraph pdg = new ProgramDependenceGraph(cn, mn);
			Graph graph = pdg.computeResult();
			System.out.println("2.1-------graph--------" + graph);
			s = graph.toString();

			// 2
			Node node = null;
			for (Node n : graph.getNodes()) {
				node = n;
				Set<Node> preds = pdg.backwardSlice(n);
				System.out.println("2.2-------node--------" + node.toString());
				System.out.println("2.2---------preds" + preds.size());

				result += node.toString() + ",";
			}
			result = "{" + result + "}";

			// 3
			double a = pdg.computeTightness();
			s2 = a;
			System.out.println("2.3-----" + s2);
			
			 // 4
			double b = pdg.computeOverlap();
			s3 = b;
			System.out.println("2.4-----" + s3);
		}
		// Q3
		Task3 c = new Task3();
		try {
			System.out.println("----------------Q3-------------------");
			System.out.println("name:  " + mn.name + ", getLOC: " + c.getLOC(cn.name, mn)
					+ ", getCyclomaticComplexity:  " + c.getCyclomatic(cn.name, mn) + ", getNumInstructions:  "
					+ c.getNumInstructions(cn) + ", getNumFunctionPoint:  " + c.getNumFunction(cn));
			System.out.println("------------------Q3-----------------");
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}

		// svc
		// Set up the CSV Printer
		FileWriter fw = new FileWriter("outputs/TranslationReport$LanguageStatsRecord.csv");
		CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.EXCEL);
		String record;
		record = "Method, Nodes, Cyclomatic Complexity,Q1,Q2,Q3,Q4\n";
		csvPrinter.printRecord(record);

		for (MethodNode mn1 : (List<MethodNode>) cn.methods) {
			int numNodes = -1;
			int cyclomaticComplexity = -1; // both values default to -1 if they cannot be computed.
			try {
				Graph cfg = CFGExtractor.getCFG(cn.name, mn1);
				numNodes = getNodeCount(cfg);
				cyclomaticComplexity = getCyclomaticComplexity(cfg);

			} catch (AnalyzerException e) {
				e.printStackTrace();
			}

			// Write the method details and metrics to the CSV record.
			String r_1 = null;
			String r_2 = null;
			String r_3 = null;
			String r_4 = null;

			r_1 = s;
			r_2 = result;
			r_3 = Double.toString(s2);
			r_4 = Double.toString(s3);
			// System.out.println(r_3);
			record = cn.name + "." + mn1.name + ", "; // Add method signature in first column.
			record += r_1 + ".";
			record += r_2 + " . ";
			record += r_3 + " . ";
			record += r_4 + " . ";
			record += Integer.toString(numNodes) + ", ";
			record += Integer.toString(cyclomaticComplexity) + "\n";
			csvPrinter.printRecord(record);
			// csvPrinter.printRecord(r_4);
			// System.out.println(r_4.toString());
			// System.out.println(r_1);
		}
		csvPrinter.close();
		System.out.println("Done");
		// System.out.println("asdasdsa++============"+s3);

	}

}
