package analysis;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;


public abstract class Analysis {

    protected Graph controlFlowGraph;

    protected final MethodNode mn;
    protected final ClassNode cn;

    public Analysis(ClassNode cn, MethodNode mn){
        Graph cfg = null;
        if(cn == null) {
            //This should only happen under testing conditions.
            this.cn = null;
            this.mn = null;
            this.controlFlowGraph = null;
            return;
        }
        try {
            cfg = CFGExtractor.getCFG(cn.name, mn);

        } catch (AnalyzerException e) {
            e.printStackTrace();
        }
        controlFlowGraph = cfg;
        this.mn = mn;
        this.cn = cn;
    }

    /**
     * Mainly a testability method - returns the control flow graph.
     * @return
     */
    public Graph getControlFlowGraph(){
        return controlFlowGraph;
    }
    
    public void setControlFlowGraph(Graph cfg) {
		// TODO Auto-generated method stub
    	controlFlowGraph = cfg;
		
	}

    /**
     * Create a new graph object that returns a Graph representation of the results of the analysis.
     * @return
     */
    public abstract Graph computeResult();



}
