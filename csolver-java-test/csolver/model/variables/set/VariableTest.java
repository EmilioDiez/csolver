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

package csolver.model.variables.set;

import static csolver.kernel.CSolver.constant;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.eqCard;
import static csolver.kernel.CSolver.isIncluded;
import static csolver.kernel.CSolver.makeSetVar;
import static csolver.kernel.CSolver.setDisjoint;
import static csolver.kernel.CSolver.setInter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.variables.set.SetVar;
import nitoku.log.Logger;

public class VariableTest {
	

    @Test
    public void test1() {
		Logger.finer("test1");
		CPSolver s = new CPSolver();
		SetVar x = s.createBoundSetVar("X", 1, 5);
		try {
			x.addToKernel(2, null, true);
			x.addToKernel(4, null, true);
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		DisposableIntIterator it = x.getDomain().getOpenDomainIterator();
		while (it.hasNext()) {
			int val = it.next();
			Logger.info(String.valueOf(val));
			assertTrue(val != 2);
			assertTrue(val != 4);
		}
	}

    @Test
    public void test2() {
		CPSolver s = new CPSolver();
		SetVar set = s.createBoundSetVar("X", 1, 5);
		boolean bool = true;
		Logger.info(set.pretty());
		for (DisposableIntIterator it0 = set.getDomain().getEnveloppeIterator();
		     it0.hasNext();) {
			int x = it0.next();
			bool = !bool;
			try {
				if (bool) {
					set.remFromEnveloppe(x, null, false);
				}
			} catch (ContradictionException e) {

			}
		}
		Logger.info(set.pretty());
		assertTrue(!set.isInDomainKernel(2));
		assertTrue(!set.isInDomainKernel(4));
	}

    @Test
    public void test3(){
        Model m = new CPModel();
        CPSolver s = new CPSolver();

        SetVariable sv1 = makeSetVar("s1", 0, 1);
        IntegerVariable card = sv1.getCard();
        m.addConstraint(eq(card, 1));
        m.addConstraint(eqCard(sv1, 1));
        s.read(m);
        s.solve();
        do{
            Logger.info(s.getVar(sv1).pretty());
            Logger.info("---------------------");
        }while(s.nextSolution());
        assertEquals("nb solution", 2, s.getNbSolutions());
    }


    @Test
    public void testEmptySet(){
        Model m1 = new CPModel();
        Solver so1 = new CPSolver();
        Model m2 = new CPModel();
        Solver so2 = new CPSolver();

        SetVariable s1 = makeSetVar("s1", 0, 2);
        SetVariable s2 = makeSetVar("s2", 0, 2);
        SetVariable empty = constant(new int[0]);

        m1.addConstraint(setInter(s1, s2, empty));

        m2.addConstraint(setDisjoint(s1, s2));

        so1.read(m1);
        so1.solveAll();
        so2.read(m2);
        so2.solveAll();
        assertEquals("Not same number of solutions", so1.getNbSolutions(), so2.getNbSolutions());
    }

    @Test
    public void testConstantSet(){
        Model m1 = new CPModel();
        Solver so1 = new CPSolver();
        Model m2 = new CPModel();
        Solver so2 = new CPSolver();

        SetVariable s1 = makeSetVar("s1", 0, 2);
        SetVariable s2 = makeSetVar("s2", 1, 1);
        SetVariable one = constant(new int[1]);

        m1.addConstraint(isIncluded(one, s1));

        m2.addConstraint(isIncluded(s2, s1));
        m2.addConstraint(eqCard(s2, 1));


        so1.read(m1);
        so1.solveAll();
        so2.read(m2);
        so2.solveAll();
        assertEquals("Not same number of solutions", so1.getNbSolutions(), so2.getNbSolutions());
    }

    @Test
    public void test4(){
        Solver so1 = new CPSolver();
        Model m1 = new CPModel();
        SetVariable s1 = CSolver.emptySet();
        SetVariable s2 = CSolver.emptySet();

        m1.addConstraint(eq(s1, s2));

        so1.read(m1);
        so1.solveAll();
    }    

}

