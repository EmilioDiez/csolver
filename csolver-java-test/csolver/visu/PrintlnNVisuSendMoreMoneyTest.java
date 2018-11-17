package csolver.visu;

import static csolver.kernel.CSolver.allDifferent;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;
import static csolver.kernel.CSolver.scalar;

import java.util.logging.Level;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.visu.NVisu;
import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.common.util.tools.StringUtils;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.timer.nitoku.ITimerAction;
import csolver.kernel.visu.components.IVisuVarHandler;
import csolver.kernel.visu.components.VisuVar;
import csolver.kernel.visu.events.VarChangeEvent;
import nitoku.log.Logger;

public class PrintlnNVisuSendMoreMoneyTest implements ITimerAction {

    IntegerVariable S, E, N, D, M, O, R, Y;
    IntegerVariable[] SEND, MORE, MONEY;
    public static final boolean LINKED = false;
    public CPSolver solver;
	public CPModel m;
	public CPSolver s;
	int n;
	long start;
	
	public static Logger Logger = null;
	
	public final void execute_send_more_money(){
		//Logger.log(NLevel.INFO,"\nexecute {0} ...", getClass().getName());
		this.buildModel_send_more_money();
		this.buildSolver_send_more_money();
		this.solve_send_more_money();
		this.prettyOut_send_more_money();
		if(Logger.isLoggable(Level.INFO)) {
			if( s == null) {
				Logger.info("\n***********\n solver object is null.");
			}else {
				Logger.info("\n***********\n#sol : "+s.getSolutionCount()+"\n"+s.runtimeStatistics());
			}
		}
	}


    public void buildModel_send_more_money() {
        m = new CPModel();
        S = makeIntVar("S", 0, 9);
        E = makeIntVar("E", 0, 9);
        N = makeIntVar("N", 0, 9);
        D = makeIntVar("D", 0, 9);
        M = makeIntVar("M", 0, 9);
        O = makeIntVar("0", 0, 9);
        R = makeIntVar("R", 0, 9);
        Y = makeIntVar("Y", 0, 9);
        m.addConstraints(neq(S, 0), neq(M, 0));
        m.addConstraint(allDifferent(S, E, N, D, M, O, R, Y));
        SEND = new IntegerVariable[]{S, E, N, D};
        MORE = new IntegerVariable[]{M, O, R, E};
        MONEY = new IntegerVariable[]{M, O, N, E, Y};
        m.addConstraints(
                eq(plus(scalar(new int[]{1000, 100, 10, 1}, SEND),
                        scalar(new int[]{1000, 100, 10, 1}, MORE)),
                        scalar(new int[]{10000, 1000, 100, 10, 1}, MONEY))
        );
    }

    public void buildSolver_send_more_money() {
        s = new CPSolver();
        s.read(m);
    }

    //update brick
	private void refreshGUI(VarChangeEvent event) {
		VisuVar mBrick = (VisuVar) event.getVisuVar();
		System.out.println("XXXXXXXXX Low Bound:: " + mBrick.getLowBound() + " Up Bound::" + mBrick.getUppBound());
	}
	
    public void solve_send_more_money() {
    	
    	
    	NVisu v = new NVisu();
    	VisuVar printBrick = new VisuVar(R);
    	
    	// Create a handler for the sendButton and nameField
		class MyMBrickHandler implements IVisuVarHandler {
			/**
			 * Fired when the var is modified.
			 */
			public void onRefresh(VarChangeEvent event) {
				refreshGUI(event);
			}

		}        
        MyMBrickHandler mBrickHandler = new MyMBrickHandler();
        printBrick.addBrickHandler(mBrickHandler);
        
        v.addBrick(printBrick);       
        s.setFirstSolution(true);
        s.generateSearchStrategy();
        s.visualize(v);
        s._launch(null);
        
    }

    public void prettyOut_send_more_money() {
        Logger.info(StringUtils.pretty(s.getVar(SEND)));
        Logger.info(" + " + StringUtils.pretty(s.getVar(MORE)));
        Logger.info(" = " + StringUtils.pretty(s.getVar(MONEY)));
    }

    @Test
    public void send_more_money_test() {
        execute_send_more_money();
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
		return Boolean.TRUE;
	}
  
}
