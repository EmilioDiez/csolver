/**
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package test.client.csolver.visu;

import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.valiterator.DecreasingDomain;
import csolver.cp.solver.visu.NVisu;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.timer.nitoku.ITimerAction;
import nitoku.log.Logger;

public class GwtVisuTest extends GWTTestCase implements ITimerAction  {

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
 
    //////////////////////// NQUEENS START ////////////////////////////////
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
    	System.out.println("XXXXXXXXXXXXXXTESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
    	solver = _solver;
    	start = System.currentTimeMillis();
    	NVisu v = new NVisu();
        //v.addBrickManager(new CanvasManager("FullDomain", queens, new GwtCanvasFullDomain(""), null));
        //to calculate all the solutions
        solver.setFirstSolution(false);
        solver.generateSearchStrategy();
        solver.visualize(v);
    	System.out.println("2222222222222222222TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        solver._launch(this);
    }
    
	@Override
	public Boolean runTimerAction() {
		System.out.println("3333333333333333333333333TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
    	long end = System.currentTimeMillis();
    	long time = end - start;
    	Logger.info(GwtVisuTest.class, "time currentTimeMillis: " + time);
        solver.printRuntimeStatistics();
        Logger.info(GwtVisuTest.class,"Solutions: " + solver.getNbSolutions());
        Logger.info(GwtVisuTest.class,"BackTrackCount: " + solver.getBackTrackCount());
        Logger.info(GwtVisuTest.class,"TimeCount: " + solver.getTimeCount());
        Logger.info(GwtVisuTest.class,"NodeCount: " + solver.getNodeCount());
        assertEquals(Boolean.valueOf( NB_QUEENS_SOLUTION[n] > 0), solver.isFeasible());
        assertEquals(NB_QUEENS_SOLUTION[n], 11);//solver.getNbSolutions());
        System.out.println("TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		return Boolean.TRUE;
	}
    
    public void solve(int n){
        s1.read(m);
        s1.setValIntIterator(new DecreasingDomain()); 
        incrementalSolve(s1, n);      
    }

    public void queen0(int n) {
        Logger.info(GwtVisuTest.class,"n queens, binary model, n=" + n);
        model(n);
        solve(n);
    }
    
    public void setUpGwt() {
    	
        Logger.info(GwtVisuTest.class,"Queens Testing...");
        m = new CPModel();
        s1 = new CPSolver();
        //s2 = new CPSolver();
    }

    public void tearDownGwt() {
        m = null;
        s1 = s2 =null;
        queens = null;
    }

    @Test
    public void test1() {
    	setUpGwt();
        queen0(7);
        tearDownGwt();
    }

	@Override
	public String getModuleName() {
		return "test.test_csolver";
	}


}
