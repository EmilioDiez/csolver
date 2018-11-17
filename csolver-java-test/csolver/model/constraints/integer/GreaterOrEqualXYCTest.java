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

package csolver.model.constraints.integer;

import static csolver.kernel.CSolver.makeIntVar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.integer.GreaterOrEqualXYC;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.constraints.SConstraint;
import nitoku.log.Logger;

public class GreaterOrEqualXYCTest {
    
    private CPModel m;
    private CPSolver s;
    private IntegerVariable x;
    private IntegerVariable y;
    private IntegerVariable z;
    private SConstraint c1;
    private SConstraint c2;

    @Before
    public void setUp() {
        Logger.fine("GreaterOrEqualXYCTest Testing...");
        m = new CPModel();
        s = new CPSolver();
        x = makeIntVar("X", 1, 5);
        y = makeIntVar("Y", 1, 5);
        z = makeIntVar("Z", 1, 5);
        m.addVariables(Options.V_BOUND,x, y, z);
        s.read(m);
        c1 = new GreaterOrEqualXYC(s.getVar(x), s.getVar(y), 1);
        c2 = new GreaterOrEqualXYC(s.getVar(y), s.getVar(z), 2);
    }

    @After
    public void tearDown() {
        c1 = null;
        c2 = null;
        x = null;
        y = null;
        z = null;
        m = null;
        s = null;
    }

    @Test
    public void test1() {
        Logger.finer("test1");
        try {
            s.post(c1);
            s.post(c2);
            s.propagate();
            assertFalse(s.getVar(x).isInstantiated());
            assertFalse(s.getVar(y).isInstantiated());
            assertEquals(4, s.getVar(x).getInf());
            assertEquals(3, s.getVar(y).getInf());
            assertEquals(1, s.getVar(z).getInf());
            assertEquals(5, s.getVar(x).getSup());
            assertEquals(4, s.getVar(y).getSup());
            assertEquals(2, s.getVar(z).getSup());
            Logger.finest("domains OK after first propagate");
            s.getVar(z).setVal(2);
            s.propagate();
            assertTrue(s.getVar(x).isInstantiated());
            assertTrue(s.getVar(y).isInstantiated());
            assertTrue(s.getVar(z).isInstantiated());
            assertEquals(5, s.getVar(x).getVal());
            assertEquals(4, s.getVar(y).getVal());
            assertEquals(2, s.getVar(z).getVal());
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

     @Test
    public void test2() {
        Logger.finer("test2");
        CPModel model = new CPModel();
        CPSolver solver = new CPSolver();
        IntegerVariable x = makeIntVar("x",0,20);
        IntegerVariable y = makeIntVar("y",0,20);
        model.addConstraint(CSolver.gt(x,y));
        model.addConstraint(CSolver.gt(y,x));
        solver.read(model);
        try {
             solver.propagate();
        } catch (ContradictionException e) {
            Logger.info("contradiction");
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }
}
