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

import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.sum;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import nitoku.log.Logger;

public class OptimizeTest {
	
    private Model m;
    private Solver s;
    private IntegerVariable v1, v2, v3, obj;

    @Before
    public void setUp() {
        Logger.fine("StoredInt Testing...");
        m = new CPModel();
        s = new CPSolver();
        obj = makeIntVar("objectif", -10, 1000);
        m.addVariable(Options.V_BOUND, obj);
        v1 = makeIntVar("v1", 1, 10);
        v2 = makeIntVar("v2", -3, 10);
        v3 = makeIntVar("v3", 1, 10);
        m.addConstraint(eq(sum(v1, v2, v3), obj));
        s.read(m);
    }

    @After
    public void tearDown() {
        v1 = null;
        v2 = null;
        v3 = null;
        obj = null;
        m = null;
        s = null;
    }

    /**
     * testing b&b search
     */
    @Test
    public void test1() {
        Logger.finer("test1");
        assertEquals(Boolean.TRUE, s.maximize(s.getVar(obj), false));
        assertTrue(s.getNbSolutions() == 32);
        assertEquals(s.getOptimumValue().intValue(), 30);
    }

    /**
     * testing search with restarts
     */
    @Test
    public void test2() {
        Logger.finer("test2");
        assertEquals(Boolean.TRUE, s.maximize(s.getVar(obj), true));
        assertTrue(s.getNbSolutions() == 32);
        assertEquals(s.getOptimumValue().intValue(), 30);
    }
    
    
  
    @Test
    public void test3() {
        Logger.finer("test3");
        s.setNodeLimit(2);
        assertNull("feasible", s.maximize(s.getVar(obj), false));
        assertEquals("solution count", 0, s.getSolutionCount());
        assertNull("objective value", s.getObjectiveValue());
    }
    

}