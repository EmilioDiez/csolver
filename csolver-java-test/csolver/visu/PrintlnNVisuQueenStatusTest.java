package csolver.visu;

import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.valiterator.DecreasingDomain;
import csolver.cp.solver.visu.NVisu;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.timer.nitoku.ITimerAction;
import csolver.kernel.visu.components.ISolverStateChangeEvent;
import csolver.kernel.visu.components.ISolverStateHandler;
import csolver.kernel.visu.components.VisuSolverState;
import csolver.kernel.visu.events.SolverStateEventType;
import csolver.kernel.visu.events.VarChangeEvent;
import nitoku.log.Logger;

public class PrintlnNVisuQueenStatusTest  implements ITimerAction {

    IntegerVariable S, E, N, D, M, O, R, Y;
    IntegerVariable[] SEND, MORE, MONEY;
    public final static int NB_QUEENS_SOLUTION[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};
    public static final boolean LINKED = false;
    public CPSolver s1;
    public CPSolver s2;
    public CPSolver solver;
   
    
    private IntegerVariable[] queens;
    
	public CPModel m;
	public CPSolver s;
	int n;
	long start;
	
	public static Logger Logger = null;
	
    private IntegerVariable createVar(String name, int min, int max) {
        if (LINKED) return makeIntVar(name, min, max, "cp:link");
        return makeIntVar(name, min, max);
    }

    public void model(int n){
        queens = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = createVar("Q" + i, 1, n);
        }
        // diagonal constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));
                m.addConstraint(neq(queens[i], minus(queens[j], k)));
            }
        }
    }

  
    public void incrementalSolve(CPSolver _solver, int _n) {
    	n = _n;
    	solver = _solver;
    	start = System.currentTimeMillis();
    	
    	NVisu v = new NVisu();
		
    	// Create a handler for the sendButton and nameField
		class MySolverStateHandler implements ISolverStateHandler {
			/**
			 * Fired when the var is modified.
			 */
			public void onRefresh(ISolverStateChangeEvent event) {
				if (event.getType() == SolverStateEventType.SOLUTION_FOUND )
				System.out.println(
						"XXXXXXXXXXXXXXX  obtained " +
						event.getState().getNoSolutions() +
						" solutions of " + NB_QUEENS_SOLUTION[n]);
			}
		}
		
		//change solution numbers
		MySolverStateHandler mySolverStateHandler = new MySolverStateHandler();
		//change solution numbers
		v.addSolverStateHandler(mySolverStateHandler);  
      
       
    	solver.setFirstSolution(false);
        solver.generateSearchStrategy();
        solver.visualize(v);
        solver.launch();//with this method we can not stop the search. to stop the search we need
        //to use the _launch method
    }

	void refreshStateGUI(VarChangeEvent event) {
		if(event.getVisuVar() instanceof VisuSolverState){
			VisuSolverState sBrick = (VisuSolverState) event.getVisuVar();
			switch (sBrick.getSearchStepState()){
				case DOWN :
					System.out.println("Down state ");
					break;
				case UP :
					System.out.println("UP state ");
					break;
				case END :
					System.out.println("END state ");
					break;		
				case NONE :
					System.out.println("NONE state ");
					break;	
				case SOLUTION :
					System.out.println("Solution state ");
					break;		
				case RESTART :
					System.out.println("Restart state ");
					break;		
			}
		}
		
	}

	
	@Override
	public Boolean runTimerAction() {
    	long end = System.currentTimeMillis();
    	long time = end - start;
    	Logger.info("time currentTimeMillis: " + time);
        solver.printRuntimeStatistics();
        Logger.info("Solutions: " + solver.getNbSolutions());
        Logger.info("BackTrackCount: " + solver.getBackTrackCount());
        Logger.info("TimeCount: " + solver.getTimeCount());
        Logger.info("NodeCount: " + solver.getNodeCount());
        assertEquals(Boolean.valueOf( NB_QUEENS_SOLUTION[n] > 0), solver.isFeasible());
        assertEquals(NB_QUEENS_SOLUTION[n], solver.getNbSolutions());
		return Boolean.TRUE;
	}
    
    public void solve(int n){
        s1.read(m);
        s1.setValIntIterator(new DecreasingDomain()); 
        incrementalSolve(s1, n);
        
        //s2.read(m);
        //s2.setValIntSelector(new MaxVal()); 
        //incrementalSolve(s2, n);

        //assertEquals(s1.getSolutionCount(), s2.getSolutionCount());
       
    }

    public void queen0(int n) {
        Logger.info("n queens, binary model, n=" + n);
        model(n);
        solve(n);
    }
    
    @Before
    public void setUp() {
    	
    	Logger.info("Branching Logger Testing...");
    	Logger.info("Queens Testing...");
              
        //CSolverLogging.setEveryXNodes(1);
        //ChocoLogging.setLoggingMaxDepth(100000);
        //ChocoLogging.setVerbosity(Verbosity.FINEST);
        
        m = new CPModel();
        s1 = new CPSolver();
        s2 = new CPSolver();
    }

    @After
    public void tearDown() {
        m = null;
        s1 = s2 =null;
        queens = null;
        //System.out.println("currentTimeMillis: "+System.currentTimeMillis());
        //System.out.println("nanoTime         : "+System.nanoTime());
    }

    @Test
    public void test0() {
        queen0(4);
    }

    //@Test
    //public void test1() {
    //    queen0(5);
    //}

}
