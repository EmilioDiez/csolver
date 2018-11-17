/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *
 *  Choco Copyright (c) 1999-2010, Ecole des Mines de Nantes
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

package csolver.solver.search;

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
import csolver.cp.solver.search.integer.valselector.MaxVal;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import nitoku.log.Logger;

/**
 * A currentElement placing n-queens on a chessboard, so that no two attack each other
 */
public class QueensTest {
    public final static int NB_QUEENS_SOLUTION[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712, 365596};
    public static final boolean LINKED = false;
    
    public Model m;
    public Solver s1;
    public Solver s2;
    private IntegerVariable[] queens;

    @Before
    public void setUp() {
        Logger.fine("Queens Testing...");
        m = new CPModel();
        s1 = new CPSolver();
        s2 = new CPSolver();
    }

    @After
    public void tearDown() {
        m = null;
        s1 = s2 =null;
        queens = null;
    }

    private IntegerVariable createVar(String name, int min, int max) {
        if (LINKED) return makeIntVar(name, min, max, Options.V_LINK);
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

  
    public void incrementalSolve(Solver solver, int n) {
    	solver.solveAll(); //check solution is enabled by assertion
//    	solver.solve();
//        do{
//            assertTrue("Not a solution", solver.checkSolution());
//        }while(Boolean.TRUE.equals(solver.nextSolution()));
        solver.printRuntimeStatistics();
        assertEquals(Boolean.valueOf( NB_QUEENS_SOLUTION[n] > 0), solver.isFeasible());
        assertEquals(NB_QUEENS_SOLUTION[n], solver.getNbSolutions());
    }
    
    public void solve(int n){
        s1.read(m);
        s1.setValIntIterator(new DecreasingDomain());
        incrementalSolve(s1, n);

        s2.read(m);
        s2.setValIntSelector(new MaxVal());
        incrementalSolve(s2, n);

        assertEquals(s1.getSolutionCount(), s2.getSolutionCount());
    }

    public void queen0(int n) {
        Logger.finer("n queens, binary model, n=" + n);
        model(n);
        solve(n);
    }

    @Test
    public void test0() {
        queen0(4);
    }

    @Test
    public void test1() {
        queen0(5);
    }

    @Test
    public void test2() {
        queen0(6);
    }

    @Test
    public void test3() {
        queen0(7);
    }

    @Test
    public void test4() {
        queen0(8);
    }

    @Test
    public void test5() {
        queen0(9);
    }

    @Test
    public void test6() {
        queen0(10);
    }

    @Test
    public void test7() {
        queen0(12);
    }

//    @Test
//    @Ignore
//    public void testAll() {
//        for (int i = 4; i < 15; i++) {
//            m = new CPModel();
//            s1 = new CPSolver();
//            Logger.info("queen "+i);
//            queen0(i);
//        }
//    }

}