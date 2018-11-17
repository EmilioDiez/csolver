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
import static csolver.kernel.CSolver.neq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.Options;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import nitoku.log.Logger;

/* File choco.currentElement.search.NotEqualXCTest.java, last modified by Francois 14 sept. 2003 16:00:54 */


public class NotEqualXCTest  {
	
  private CPModel m;
    private CPSolver s;
  private IntegerVariable x;
  private IntegerVariable y;
  private Constraint c1;
  private Constraint c2;

    @Before
  public void setUp() {
    Logger.fine("NotEqualXC Testing...");
    m = new CPModel();
        s = new CPSolver();
    x = makeIntVar("X", 1, 5);
    y = makeIntVar("Y", 1, 5);
        m.addVariable(Options.V_BOUND, y);
    c1 = neq(x, 3);
    c2 = neq(y, 3);
  }

    @After
  public void tearDown() {
    c1 = null;
    c2 = null;
    x = null;
    y = null;
    s = null;
        s = null;
  }

    @Test
  public void test1() {
    Logger.finer("test1");
    try {
      m.addConstraints(c1, c2);
        s.read(m);
        s.propagate();
      assertFalse(s.getVar(x).canBeInstantiatedTo(3));
      assertTrue(s.getVar(y).canBeInstantiatedTo(3));
      s.getVar(x).remVal(2);
      s.getVar(x).remVal(1);
      s.propagate();
      assertEquals(s.getVar(x).getInf(), 4);
      s.getVar(y).setInf(3);
      s.propagate();
      assertEquals(s.getVar(y).getInf(), 4);
    } catch (ContradictionException e) {
      assertTrue(false);
    }
  }
}
