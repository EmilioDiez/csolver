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

package csolver.model.constraints.global;

import static csolver.kernel.CSolver.allDifferent;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.makeIntVarArray;
import static csolver.kernel.CSolver.sorting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.global.SortingSConstraint;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.cp.solver.search.integer.varselector.StaticVarOrder;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import nitoku.log.Logger;

public class SortingTest {

	

	@Test
	public void testSorting() {
		CPModel m = new CPModel();
		IntegerVariable[] x = {
				makeIntVar("x0", 1, 16),
				makeIntVar("x1", 5, 10),
				makeIntVar("x2", 7, 9),
				makeIntVar("x3", 12, 15),
				makeIntVar("x4", 1, 13)
		};
		IntegerVariable[] y = {
				makeIntVar("y0", 2, 3),
				makeIntVar("y1", 6, 7),
				makeIntVar("y2", 8, 11),
				makeIntVar("y3", 13, 16),
				makeIntVar("y4", 14, 18)
		};
		Constraint c = sorting(x, y);
		m.addConstraint(c);
		CPSolver s = new CPSolver();
		s.read(m);
		try {
			((SortingSConstraint)s.getCstr(c)).boundConsistency();
		}
		catch (ContradictionException e) {
			assertTrue(false);
			e.printStackTrace();
		}
	}

	@Test
	public void testSorting2() {
		for (int seed = 0; seed < 1; seed++) {
			CPModel m = new CPModel();
			int n = 3;
			IntegerVariable[] x = makeIntVarArray("x", n, 0, n);
			IntegerVariable[] y = makeIntVarArray("y", n, 0, n);
			Constraint c = sorting(x, y);
			m.addConstraint(c);
			m.addConstraint(allDifferent(x));
			CPSolver s = new CPSolver();
			s.read(m);
			//            s.setValIntSelector(new RandomIntValSelector(seed));
			//            s.setVarIntSelector(new RandomIntVarSelector(s, seed + 2));
			s.solve();
			HashSet<String> sols = new HashSet<String>();
			if(s.isFeasible()){
				do{
					StringBuffer st = new StringBuffer();
					st.append(s.getVar(x[0]).getVal());
					for(int i = 1; i < n; i++){
						st.append(",").append(s.getVar(x[i]).getVal());
					}
					//                    st.append(" - ").append(s.getVar(y[0]).getVal());
					//                    for(int i = 1; i < n; i++){
					//                        st.append(",").append(s.getVar(y[i]).getVal());
					//                    }
					sols.add(st.toString());
					Logger.info(SortingTest.class,st.toString());
				}while(s.nextSolution());
			}

			Logger.info(SortingTest.class,"---------------");
			CPSolver s1 = new CPSolver();
			s1.read(m);
			//            s.setValIntSelector(new RandomIntValSelector(seed));
			//            s.setVarIntSelector(new RandomIntVarSelector(s, seed + 2));
			s1.setVarIntSelector(new StaticVarOrder(s1, s1.getVar((IntegerVariable[]) ArrayUtils.append(x,y))));
			s1.setValIntIterator(new IncreasingDomain());
			s1.solve();
			if(s1.isFeasible()){
				do{
					StringBuffer st = new StringBuffer();
					st.append(s1.getVar(x[0]).getVal());
					for(int i = 1; i < n; i++){
						st.append(",").append(s1.getVar(x[i]).getVal());
					}
					//                    st.append(" - ").append(s1.getVar(y[0]).getVal());
					//                    for(int i = 1; i < n; i++){
					//                        st.append(",").append(s1.getVar(y[i]).getVal());
					//                    }
					sols.remove(st.toString());
					Logger.info(st.toString());
				}while(s1.nextSolution());
			}
			if(Logger.isLoggable(Level.INFO)) {
				Logger.info(SortingTest.class,"########");
				Logger.info(SortingTest.class,Arrays.toString(sols.toArray()));
				Logger.info(SortingTest.class,n + " - " +s.getNbSolutions()+":" + s1.getNbSolutions());
			}
			assertEquals(s.getNbSolutions(), s1.getNbSolutions());
			//            assertEquals(840, s1.getNbSolutions());
		}

	}

	@Test
	public void testName() {
		CPModel m = new CPModel();
		int n = 3;
		IntegerVariable[] x = makeIntVarArray("x", n, 0, n);
		IntegerVariable[] y = makeIntVarArray("y", n, 0, n);
		m.addConstraint(sorting(x, y));
		m.addConstraint(allDifferent(x));
		CPSolver s = new CPSolver();
		s.read(m);
		s.setVarIntSelector(new StaticVarOrder(s, s.getVar(x)));
		s.setValIntIterator(new IncreasingDomain());
		s.solve();
		if(s.isFeasible()){
			do{
				StringBuffer st = new StringBuffer();
				st.append(s.getVar(x[0]).getVal());
				for(int i = 1; i < n; i++){
					st.append(",").append(s.getVar(x[i]).getVal());
				}
				Logger.info(SortingTest.class,st.toString());
			}while(s.nextSolution());
		}
		Logger.info(SortingTest.class,""+s.getNbSolutions());
	}
}
