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
 */
package test.client.csolver.solver.search;

import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.geq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.sum;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import nitoku.log.Logger;

import com.google.gwt.junit.client.GWTTestCase;

public class CutTest extends GWTTestCase{

    CSolverLogging log;
    
	@Override
	public String getModuleName() {
		return "test.test_csolver";
	}
	
    private Model m;
    private CPSolver s;
    private IntegerVariable v1;
    private IntegerVariable v2;
    private IntegerVariable v3;

    public void setUpGwt() {
   
        Logger.fine(CutTest.class, "StoredInt Testing...");
        m = new CPModel();
        s = new CPSolver();
        v1 = makeIntVar("v1", 0, 1);
        v2 = makeIntVar("v2", 0, 1);
        m.addVariables(v1, v2);
        s.read(m);
    }

    public void tearDownGwt() {
        v1 = null;
        v2 = null;
        v3 = null;
        m = null;
        s = null;
    }

    /**
     * one cut that sets the model to the first solution -> no more solutions afterwards
     */
    @Test
    public void test1() {
    	setUpGwt();
        Logger.info("test1");
        Logger.finer("test1");
        s.solve();
        int valv1 = s.getVar(v1).getVal();
        int valv2 = s.getVar(v2).getVal();
        s.postCut(s.eq(s.getVar(v1), valv1));
        s.postCut(s.eq(s.getVar(v2), valv2));
        Boolean nxt = s.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
        tearDownGwt();
    }

    /**
     * one cut that forces each variable to be different from their value in the first solution
     * -> only one other solution (flipping all variables)
     */
    @Test
    public void test2() {
    	setUpGwt();
        Logger.finer("test1");
        Logger.info("test2");
        s.solve();
        int valv1 = s.getVar(v1).getVal();
        int valv2 = s.getVar(v2).getVal();
        s.postCut(s.neq(s.getVar(v1), valv1));
        s.postCut(s.neq(s.getVar(v2), valv2));
        Boolean nxt = s.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        assertEquals(s.getVar(v1).getVal(), 1 - valv1);
        assertEquals(s.getVar(v2).getVal(), 1 - valv2);
        nxt = s.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
        tearDownGwt();
    }

    /**
     * one cut that sets the first variable to its value in the first solution
     * -> only one more solutions afterwards
     */
    @Test
    public void test3() {
    	setUpGwt();
        Logger.info("test3");
        Logger.finer("test3");
        s.solve();
        int valv1 = s.getVar(v1).getVal();
        s.postCut(s.eq(s.getVar(v1), valv1));
        Boolean nxt = s.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        assertEquals(valv1, s.getVar(v1).getVal());
        nxt = s.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
        tearDownGwt();
    }

    /**
     * one cut that sets the first variable to its value in the first solution
     * -> only one more solutions afterwards
     */
    @Test
    public void test4() {
    	setUpGwt();
        Logger.info("test4");
        Logger.finer("test4");
        s.solve();
        int valv2 = s.getVar(v2).getVal();
        s.postCut(s.eq(s.getVar(v2), valv2));
        Boolean nxt = s.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        assertEquals(valv2, s.getVar(v2).getVal());
        nxt = s.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
        tearDownGwt();
    }

    @Test
    public void test5() {
    	setUpGwt();
        Logger.info("test5");
        Logger.finer("test5");
        IntegerVariable v3 = makeIntVar("v3", 0, 1);
        m.addVariable(v3);
        s.read(m);
        s.solve();  // first solution 0,0,0
        s.postCut(s.leq(s.plus(s.getVar(v1), s.plus(s.getVar(v2), s.getVar(v3))), 1));
        // now three more solutions
        Boolean nxt;
        nxt = s.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        assertEquals(nxt, Boolean.FALSE);
        tearDownGwt();
    }

    @Test
    public void test6() {
    	setUpGwt();
        Logger.info("test6");
        Logger.finer("test6");
        IntegerVariable v3 = makeIntVar("v3", 0, 1);
        m.addVariable(v3);
        s.read(m);
        s.solve();  // first solution 0,0,0
        s.postCut(s.geq(s.plus(s.getVar(v1), s.plus(s.getVar(v2), s.getVar(v3))), 2));
//         now three more solutions
//        while(s.nextSolution()){
//            Logger.info(s.pretty());
//        }
        Boolean nxt;
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.FALSE);
        tearDownGwt();
    }

    @Test
    public void test7() {
    	setUpGwt();
        Logger.info("test7");
        Logger.finer("test7");
        IntegerVariable v3 = makeIntVar("v3", 0, 1);
        m.addVariable(v3);
        s.read(m);
        s.solve();  // first solution 0,0,0
        s.postCut(s.eq(s.plus(s.getVar(v1), s.plus(s.getVar(v2), s.getVar(v3))), 2));
        // now three more solutions
        Boolean nxt;
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.TRUE);
        nxt = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(nxt, Boolean.FALSE);
        tearDownGwt();
    }

    @Test
    public void test8() {
    	setUpGwt();
        Logger.info("test8");
        Logger.finer("test8");
        IntegerVariable v3 = makeIntVar("v3", 0, 1);
        IntegerVariable v4 = makeIntVar("v4", 0, 1);
        IntegerVariable v5 = makeIntVar("v5", 0, 1);
        IntegerVariable v6 = makeIntVar("v6", 0, 1);
        m.addConstraint(geq(sum(new IntegerVariable[]{v1, v2, v3, v4, v5, v6}), 5));
        s.read(m);
        Boolean found;
        found = s.solve();
        Logger.info(s.pretty());
        assertEquals(found, Boolean.TRUE);

        s.postCut(s.eq(s.getVar(v1), 1));
        s.postCut(s.leq(s.sum(s.getVar(v1, v2, v3, v4, v5, v6)), 5));
        for (int i = 0; i < 5; i++) {
            found = s.nextSolution();
            Logger.info(s.pretty());
            assertEquals(found, Boolean.TRUE);
        }

        found = s.nextSolution();
        Logger.info(s.pretty());
        assertEquals(found, Boolean.FALSE);
        tearDownGwt();
    }

    @Test
    public void test9() {
    	setUpGwt();
        Logger.info("test9");
        m.removeVariables(v1, v2);
        s = new CPSolver();
        v1 = makeIntVar("v1", 0, 1);
        v2 = makeIntVar("v2", 0, 1);

        m.addVariables(v1, v2);
        s.read(m);

        SConstraint ct = s.neq(s.getVar(v1), s.getVar(v2));
        SConstraint ct2 = s.neq(s.getVar(v1), s.getVar(v2));
        s.postCut(ct);
        s.postCut(ct2);
        s.eraseConstraint(ct);
        s.eraseConstraint(ct2);

        s.solveAll();

        Logger.info("Nombre de solutions : " + s.getNbSolutions());
        assertEquals(s.getNbSolutions(),4);
        tearDownGwt();
    }

    @Test
    public void test9bis() {
    	setUpGwt();
        Logger.info("test9b");
        m.removeVariables(v1, v2);
        s = new CPSolver();
        v1 = makeIntVar("v1", 0, 1);
        v2 = makeIntVar("v2", 0, 1);
        m.addVariables(v1, v2);
        s.read(m);

        SConstraint ct = s.neq(s.getVar(v1), s.getVar(v2));
        s.postCut(ct);
        s.eraseConstraint(ct);

        s.solveAll();

        Logger.info("Nombre de solutions : " + s.getNbSolutions());
        assertEquals(s.getNbSolutions(),4);
        tearDownGwt();
    }

    @Test
    public void test10() {
    	setUpGwt();
        Logger.info("test10");
        m.removeVariables(v1, v2);
        s = new CPSolver();
        v1 = makeIntVar("v1", 0, 1);
        v2 = makeIntVar("v2", 0, 1);
        m.addVariables(v1, v2);
        s.read(m);
        int baseworld = s.getEnvironment().getWorldIndex();
        s.worldPush();

        s.solveAll();
        Logger.info("Nombre de solutions : " + s.getNbSolutions());
        assertEquals(s.getNbSolutions(),4);
        s.worldPopUntil(baseworld);
        s.solveAll();
        Logger.info("Nombre de solutions : " + s.getNbSolutions());
        assertEquals(s.getNbSolutions(),4);
        tearDownGwt();
    }


    @Test
    public void testNogood1() {
    	setUpGwt();
        Logger.info("testNogood110");
        m = new CPModel();
        s = new CPSolver();
        v1 = makeIntVar("v1", 0, 1);
        v2 = makeIntVar("v2", 0, 1);
        v3 = makeIntVar("v3", 0, 1);
        IntegerVariable v4 = makeIntVar("v4", 0, 1);
        IntegerVariable v5 = makeIntVar("v5", 0, 1);

        m.addVariables(v1, v2, v3, v4, v5);
        m.addConstraint(eq(sum(new IntegerVariable[]{v1, v2, v3, v4, v5}),3));
        s.read(m);

        s.solve();
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal() + " , " + s.getVar(v4).getVal() + " , " + s.getVar(v5).getVal());

        s.addNogood(new IntDomainVar[]{s.getVar(v1)},new IntDomainVar[]{s.getVar(v3),s.getVar(v4)});

        s.nextSolution();
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v3).getVal() == 0 ||s.getVar(v4).getVal() == 0);
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal() + " , " + s.getVar(v4).getVal() + " , " + s.getVar(v5).getVal());

        s.addNogood(new IntDomainVar[]{s.getVar(v1)},new IntDomainVar[]{s.getVar(v2)});
        s.nextSolution();
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v3).getVal() == 0 ||s.getVar(v4).getVal() == 0);
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v2).getVal() == 0 );
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal() + " , " + s.getVar(v4).getVal() + " , " + s.getVar(v5).getVal());

        s.addNogood(new IntDomainVar[]{s.getVar(v5),s.getVar(v2)},new IntDomainVar[]{});
        s.nextSolution();
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v3).getVal() == 0 ||s.getVar(v4).getVal() == 0);
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v2).getVal() == 0 );
        assertTrue(s.getVar(v5).getVal() == 1 || s.getVar(v2).getVal() == 1 );        
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal() + " , " + s.getVar(v4).getVal() + " , " + s.getVar(v5).getVal());
        tearDownGwt();
    }

    @Test
    public void testNogood2() {
    	setUpGwt();
        Logger.info("testNogood110");
        m = new CPModel();
        s = new CPSolver();
        v1 = makeIntVar("v1", 0, 1);
        v2 = makeIntVar("v2", 0, 1);
        v3 = makeIntVar("v3", 0, 1);

        m.addVariables(v1, v2, v3);
        s.read(m);

        s.solve();
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal());

        s.addNogood(new IntDomainVar[]{s.getVar(v1),s.getVar(v2)},new IntDomainVar[]{});

        s.nextSolution();
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v2).getVal() == 1);
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal());

        s.addNogood(new IntDomainVar[]{},new IntDomainVar[]{s.getVar(v1),s.getVar(v2)});
        s.nextSolution();
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal());
        assertTrue(s.getVar(v1).getVal() == 0 || s.getVar(v2).getVal() == 0);
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v2).getVal() == 1);

        s.addNogood(new IntDomainVar[]{s.getVar(v3)},new IntDomainVar[]{});
        s.nextSolution();
        Logger.info(s.getVar(v1).getVal() + " , " + s.getVar(v2).getVal() + " , " + s.getVar(v3).getVal());
        assertTrue(s.getVar(v1).getVal() == 0 || s.getVar(v2).getVal() == 0);
        assertTrue(s.getVar(v1).getVal() == 1 || s.getVar(v2).getVal() == 1);
        assertTrue(s.getVar(v3).getVal() == 1);
        tearDownGwt();

    }
}