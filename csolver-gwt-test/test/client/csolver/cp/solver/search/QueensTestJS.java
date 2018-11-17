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
package test.client.csolver.cp.solver.search;


import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

import csolver.client.solver.JsInteropConstraint;
import csolver.client.solver.JsInteropIntegerExpresionVariable;
import csolver.client.solver.JsInteropIntegerVariable;
import csolver.client.solver.Model;
import csolver.client.solver.Solver;
import nitoku.log.Logger;

/* File choco.currentElement.search.QueensTest.java, last modified by flaburthe 12 janv. 2004 18:03:29 */

/**
 * A currentElement placing n-queens on a chessboard, so that no two attack each other
 */
public class QueensTestJS extends GWTTestCase{

    
	@Override
	public String getModuleName() {
		return "test.test_csolver";
	}
	
    public final static int NB_QUEENS_SOLUTION[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};
    public Model m;
    public Solver s1;
    public Solver s2;
    private JsInteropIntegerVariable[] queens;

    public void setUpGwt() {

        Logger.info(QueensTestJS.class,"Queens Testing...");
        m = new Model();
        s1 = new Solver();
        s2 = new Solver();
    }

    public void tearDownGwt() {
        m = null;
        s1 = s2 =null;
        queens = null;
    }



    public void model(int n){
        queens = new JsInteropIntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = JsInteropIntegerVariable.makeIntVar("Q" + i, 1, n);
        }
        // diagonal constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(JsInteropConstraint.neq(queens[i], queens[j]));
                m.addConstraint(JsInteropConstraint.neq(queens[i], JsInteropIntegerExpresionVariable.plus(queens[j], k)));
                m.addConstraint(JsInteropConstraint.neq(queens[i], JsInteropIntegerExpresionVariable.minus(queens[j], k)));
            }
        }
    }

  
    public void incrementalSolve(Solver solver, int n) {
    	long start = System.currentTimeMillis();
    	solver.solveAll(); //check solution is enabled by assertion
    	long end = System.currentTimeMillis();
    	long time = end - start;
    	Logger.info(QueensTestJS.class,"time currentTimeMillis: " + time);
//    	solver.solve();
//        do{
//            assertTrue("Not a solution", solver.checkSolution());
//        }while(Boolean.TRUE.equals(solver.nextSolution()));
        solver.printRuntimeStatistics();
        Logger.info(QueensTestJS.class,"Solutions: " + solver.getNbSolutions());
        Logger.info(QueensTestJS.class,"BackTrackCount: " + solver.getBackTrackCount());
        Logger.info(QueensTestJS.class,"TimeCount: " + solver.getTimeCount());
        Logger.info(QueensTestJS.class,"NodeCount: " + solver.getNodeCount());
        //assertEquals(Boolean.valueOf( NB_QUEENS_SOLUTION[n] > 0), solver.isFeasible());
        assertEquals(NB_QUEENS_SOLUTION[n], solver.getNbSolutions());
    }
    
    public void solve(int n){
        s1.read(m);
        //s1.setValIntIterator(new DecreasingDomain()); 
        incrementalSolve(s1, n);
        
        //s2.read(m);
        //s2.setValIntSelector(new MaxVal()); 
        //incrementalSolve(s2, n);

        //assertEquals(s1.getSolutionCount(), s2.getSolutionCount());
    }

    public void queen0(int n) {
        Logger.info(QueensTestJS.class,"n queens, binary model, n=" + n);
        model(n);
        solve(n);
    }

    @Test
    public void test0() {
    	setUpGwt();
        queen0(4);
        tearDownGwt();
    }

    @Test
    public void test1() {
    	setUpGwt();
        queen0(5);
        tearDownGwt();
    }

    @Test
    public void test2() {
    	setUpGwt();
        queen0(6);
        tearDownGwt();
    }

    @Test
    public void test3() {
    	setUpGwt();
        queen0(7);
        tearDownGwt();
    }
    

         
    @Test
    public void test4() {
    	setUpGwt();
        queen0(8);
        tearDownGwt();
    }
/*     
    @Test
    public void test5() {
    	setUpGwt();
        queen0(9);
        tearDownGwt();
    }    

    @Test
    public void test6() {
    	setUpGwt();
        queen0(10);
        tearDownGwt();
    }

    @Test
    public void test7() {
    	setUpGwt();
        queen0(11);
        tearDownGwt();
    }
    */

}