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

import static csolver.kernel.CSolver.abs;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.valselector.RandomIntValSelector;
import csolver.cp.solver.search.integer.varselector.RandomIntVarSelector;
import csolver.kernel.Options;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import nitoku.log.Logger;

public class AbsTest {

    

    @Test
    public void testPropagAbs1() {
        CPModel m = new CPModel();

        IntegerVariable x = makeIntVar("x", 2, 5);
        IntegerVariable y = makeIntVar("y", -5, 5);
        m.addConstraint(abs(x , y));
        CPSolver s = new CPSolver();
        s.read(m);
        try {
            s.propagate();
            s.getVar(y).remVal(3);
            s.getVar(y).remVal(-3);
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        Logger.info(AbsTest.class,"x " + s.getVar(x).getDomain().pretty());
        Logger.info(AbsTest.class,"y " + s.getVar(y).getDomain().pretty());
        Logger.info(AbsTest.class,""+!s.getVar(x).canBeInstantiatedTo(3));
        assertTrue(!s.getVar(x).canBeInstantiatedTo(3));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(0));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(1));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(-1));
    }

	@Test
	public void testEasy() {
		CPModel m = new CPModel();
		IntegerVariable v0 = makeIntVar("v0", 0, 10);
		IntegerVariable v1 = makeIntVar("v1", 0, 10);
		IntegerVariable w0 = makeIntVar("w0", -100, 100);
		IntegerVariable absw0 = makeIntVar("absw0", -100, 100);

		m.addConstraint(abs(absw0, w0));
		m.addConstraint(eq(minus(v0, v1), w0));
		m.addConstraint(neq(v0, v1));
		CPSolver s = new CPSolver();
		s.read(m);
		Logger.info(AbsTest.class,s.pretty());
		s.solveAll();
		int nbNode = s.getNodeCount();
		Logger.info(AbsTest.class,"solutions : " + s.getNbSolutions() + " nbNode : " + nbNode);
		assertEquals(110,s.getNbSolutions());

	}

	

    @Test
    public void testPropagAbs2() {
        CPModel m = new CPModel();

        IntegerVariable x = makeIntVar("x", 4, 7);
        IntegerVariable y = makeIntVar("y", -5, 5);
        m.addConstraint(abs(x , y));
        CPSolver s = new CPSolver();
        s.read(m);
        try {
            s.propagate();
            s.getVar(y).remVal(3);
            s.getVar(y).remVal(-3);
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        
        Logger.info(AbsTest.class,"x " + s.getVar(x).getDomain().pretty());
        Logger.info(AbsTest.class,"y " + s.getVar(y).getDomain().pretty());
        Logger.info(AbsTest.class,"" + !s.getVar(x).canBeInstantiatedTo(3));
        assertTrue(!s.getVar(x).canBeInstantiatedTo(3));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(0));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(1));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(-1));
        
    }

    @Test
    public void testPropagAbs3() {
        CPModel m = new CPModel();
        IntegerVariable x = makeIntVar("x", 0, 5);
        IntegerVariable y = makeIntVar("y", -5, 5);
        m.addConstraint(abs(x , y));
        CPSolver s = new CPSolver();
        s.read(m);
        try {
            s.propagate();
            s.getVar(x).updateSup(2, null, true);
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        Logger.info(AbsTest.class,"x " + s.getVar(x).getDomain().pretty());
        Logger.info(AbsTest.class,"y " + s.getVar(y).getDomain().pretty());
        Logger.info(AbsTest.class,""+ !s.getVar(x).canBeInstantiatedTo(3));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(3));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(-3));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(4));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(-4));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(5));
        assertTrue(!s.getVar(y).canBeInstantiatedTo(-5));
    }

    @Test
    public void testPropagAbs4() {
        CPModel m = new CPModel();
        IntegerVariable x = makeIntVar("x", 1, 10);
        IntegerVariable y = makeIntVar("y", -10, 10);
        m.addConstraint(abs(x , y));
        CPSolver s = new CPSolver();
        s.read(m);
        try {
            s.propagate();
            s.getVar(x).updateInf(7, null, true);
            //s.getVar(y).updateSup(2,-1);
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        Logger.info(AbsTest.class,"x " + s.getVar(x).getDomain().pretty());
        Logger.info(AbsTest.class,"y " + s.getVar(y).getDomain().pretty());
        for (int i = 0; i < 6; i++) {
            assertTrue(!s.getVar(y).canBeInstantiatedTo(-i));
            assertTrue(!s.getVar(y).canBeInstantiatedTo(i));
        }
    }

    @Test
    public void test1() {
        for (int i = 0; i <= 10; i++) {
            CPModel m = new CPModel();
            IntegerVariable x = makeIntVar("x", 1, 5);
            IntegerVariable y = makeIntVar("y", -5, 5);
            m.addConstraint(abs(x,y));
            CPSolver s = new CPSolver();
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i + 1));
            s.solve();
            do {
                Logger.info(AbsTest.class,"" + s.getVar(x).getVal() + "=abs(" + s.getVar(y).getVal() + ")");
            } while (s.nextSolution() == Boolean.TRUE);
            Logger.info(AbsTest.class,"" + s.getSearchStrategy().getNodeCount());
            assertEquals(10, s.getNbSolutions());
            //Logger.info("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }

    @Test
    public void test2() {
        for (int i = 0; i <= 10; i++) {
            CPModel m = new CPModel();
            IntegerVariable x = makeIntVar("x", 1, 5);
            IntegerVariable y = makeIntVar("y", -5, 5);
            m.addVariables(Options.V_BOUND, x, y);
            m.addConstraint(abs(x,y));
            CPSolver s = new CPSolver();
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i + 1));
            s.solve();
            do {
                Logger.info(AbsTest.class,"" + s.getVar(x).getVal() + "=abs(" + s.getVar(y).getVal() + ")");
            } while (s.nextSolution() == Boolean.TRUE);
            Logger.info(AbsTest.class,"" + s.getSearchStrategy().getNodeCount());
            assertEquals(10, s.getNbSolutions());
            //Logger.info("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }

    @Test
    public void test3() {
        for (int i = 0; i <= 10; i++) {
            CPModel m = new CPModel();
            IntegerVariable x = makeIntVar("x", 1, 10);
            IntegerVariable y = makeIntVar("y", -2, 10);
            m.addVariables(Options.V_BOUND, x, y);
            m.addConstraint(abs(x,y));
            CPSolver s = new CPSolver();
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i + 1));
            s.solve();
            do {
                Logger.info(AbsTest.class,"" + s.getVar(x).getVal() + "=abs(" + s.getVar(y).getVal() + ")");
            } while (s.nextSolution() == Boolean.TRUE);
            //Logger.info("" + pb.getSolver().getSearchStrategy().getNodeCount());
            assertEquals(12, s.getNbSolutions());
            //Logger.info("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }
}
