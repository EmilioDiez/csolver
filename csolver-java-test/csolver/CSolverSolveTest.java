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

package csolver;

import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.geq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.plus;
import static csolver.kernel.CSolver.sum;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.propagation.Propagator;
import nitoku.log.Logger;
/**
 * To change this template use Options | File Templates.
 */

public class CSolverSolveTest  {
    

    private Model m;
    private Solver s;
    private IntegerVariable x;
    private IntegerVariable y;
    private IntegerVariable z;
    private Propagator Ap;
    private Constraint A;
    private Constraint B;


    @Before
    public void setUp() {
        m = new CPModel();
        s = new CPSolver();
        x = makeIntVar("X", 1, 5);
        y = makeIntVar("Y", 1, 5);
        z = makeIntVar("Z", 1, 5);
        m.addVariables(Options.V_BOUND, x, y, z);
        A = geq(x, plus(y, 1));
        B = geq(y, plus(z, 1));
    }

    @After
    public void tearDown() {
        A = null;
        B = null;
        x = null;
        y = null;
        z = null;
        m = null;
    }

    @Test
    public void arithmetic() {

        m.addConstraint(A);
        m.addConstraint(B);
        s.read(m);
        Ap = (Propagator) s.getCstr(A);
        Ap.setPassive();
        Ap.setActive();
        try {
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        Logger.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        Logger.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        Logger.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(3, s.getVar(x).getInf());
        assertEquals(5, s.getVar(x).getSup());
        assertEquals(2, s.getVar(y).getInf());
        assertEquals(4, s.getVar(y).getSup());
        assertEquals(1, s.getVar(z).getInf());
        assertEquals(3, s.getVar(z).getSup());

        try {
            s.getVar(z).setVal(2);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        Logger.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        assertEquals(4, s.getVar(x).getInf());
        assertEquals(5, s.getVar(x).getSup());
        Logger.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        assertEquals(3, s.getVar(y).getInf());
        assertEquals(4, s.getVar(y).getSup());
        Logger.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(2, s.getVar(z).getInf());
        assertEquals(2, s.getVar(z).getSup());

        try {
            s.getVar(x).setSup(4);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        Logger.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        assertEquals(4, s.getVar(x).getInf());
        assertEquals(4, s.getVar(x).getSup());
        Logger.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        assertEquals(3, s.getVar(y).getInf());
        assertEquals(3, s.getVar(y).getSup());
        Logger.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(2, s.getVar(z).getInf());
        assertEquals(2, s.getVar(z).getSup());
    }

    @Test
    public void arithmetic2() {
        m.addConstraint(A);
        m.addConstraint(B);
        m.addVariable(z);
        s.read(m);
        Ap = (Propagator) s.getCstr(A);
        Ap.setPassive();
        try {
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        Logger.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        assertEquals(1, s.getVar(x).getInf());
        assertEquals(5, s.getVar(x).getSup());
        Logger.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        assertEquals(2, s.getVar(y).getInf());
        assertEquals(5, s.getVar(y).getSup());
        Logger.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(1, s.getVar(z).getInf());
        assertEquals(4, s.getVar(z).getSup());
    }

    @Test
    public void testDiego() {
        int n = 2;
        CPModel m = new CPModel();
        CPSolver s = new CPSolver();
        IntegerVariable[] UP2 = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            UP2[i] = CSolver.makeIntVar("up2_" + i, 0, 1);
            m.addVariable(UP2[i]);
        }
        m.addConstraint(eq(sum(UP2), 1));
        s.read(m);
        s.addConstraint(eq(sum(UP2), 1));
    }
}
