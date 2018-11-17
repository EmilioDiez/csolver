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

package csolver.model.constraints.reified;

import static csolver.kernel.CSolver.FALSE;
import static csolver.kernel.CSolver.TRUE;
import static csolver.kernel.CSolver.geq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import nitoku.log.Logger;

public class NegationTest {

    
    private Model m;
    private Solver s;
    private IntegerVariable x;

@After
    public void tearDown() throws Exception {
        x = null;
        m = null;
        s = null;
    }

    @Before
    public void setUp() throws Exception {
        Logger.fine("choco.currentElement.reified.NegationTest Testing...");
        m = new CPModel();
        s = new CPSolver();
        x = makeIntVar("X", 1, 10);
        m.addVariable(Options.V_BOUND, x);
    }

    @Test
    public void test1() {
        Logger.info("NegationTest.test1");
        Constraint c0 = geq((x), (3));

        Constraint not = not(c0);
        Logger.info(not.pretty());
        m.addConstraint(not);
        s.read(m);
        Logger.info(s.getCstr(not).pretty());
        try {
            s.propagate();
        } catch (ContradictionException e) {
            Logger.severe(NegationTest.class,"Test1#propagate() "+e.getMessage());
            fail();
        }
         s.solve();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 2);
        Logger.info("OK");
    }

    @Test
    public void test1Decomp() {
        Logger.info("NegationTest.test1");
        Constraint c0 = geq((x), (3));

        m.setDefaultExpressionDecomposition(true);

        Constraint not = not(c0);
        Logger.info(not.pretty());
        m.addConstraint(not);
        s.read(m);
        Logger.info(s.getCstr(not).pretty());

        try {
            s.propagate();
        } catch (ContradictionException e) {
            Logger.severe(NegationTest.class," Test1#propagate() "+e.getMessage());
            fail();
        }
         s.solve();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 2);
        Logger.info("OK");
    }

    @Test
    public void testNotTrue(){
        m = new CPModel();
        s = new CPSolver();
        m.addConstraint(not(TRUE));
        s.read(m);
        s.solve();
        assertEquals(Boolean.FALSE, s.isFeasible());
    }

    @Test
    public void testNotFalse(){
        m = new CPModel();
        s = new CPSolver();
        m.addConstraint(not(FALSE));
        s.read(m);
        s.solve();
        assertEquals(Boolean.TRUE, s.isFeasible());
    }
}