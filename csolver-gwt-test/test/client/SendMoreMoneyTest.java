/*
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
 * 
 */
package test.client;

import static csolver.kernel.CSolver.allDifferent;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;
import static csolver.kernel.CSolver.scalar;

import com.google.gwt.junit.client.GWTTestCase;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.common.util.tools.StringUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import nitoku.log.Logger;

/**
 * Tests BitSet class.
 */
public class SendMoreMoneyTest extends GWTTestCase {

  IntegerVariable S, E, N, D, M, O, R, Y;
  IntegerVariable[] SEND, MORE, MONEY;
  public Model _m;
  public Solver _s;
  CSolverLogging log;
	
  @Override
  public String getModuleName() {
	return "test.test_csolver";
  }

  public void testSimple(){

	  buildModel();
	  buildSolver();
	  solve();
	  String result = StringUtils.pretty(_s.getVar(SEND))+
	  " + " + StringUtils.pretty(_s.getVar(MORE)) + 
	  		" = " + StringUtils.pretty(_s.getVar(MONEY));
	  System.out.println(result);
	  assertTrue(true);
  }
  
  
  public void buildModel() {
  	
  	//log = injector.getChocoLogging();
  	
  	_m = new CPModel();
      S = makeIntVar("S", 0, 9);
      E = makeIntVar("E", 0, 9);
      N = makeIntVar("N", 0, 9);
      D = makeIntVar("D", 0, 9);
      M = makeIntVar("M", 0, 9);
      O = makeIntVar("0", 0, 9);
      R = makeIntVar("R", 0, 9);
      Y = makeIntVar("Y", 0, 9);
      _m.addConstraints(neq(S, 0), neq(M, 0));
      _m.addConstraint(allDifferent(S, E, N, D, M, O, R, Y));
      SEND = new IntegerVariable[]{S, E, N, D};
      MORE = new IntegerVariable[]{M, O, R, E};
      MONEY = new IntegerVariable[]{M, O, N, E, Y};
      _m.addConstraints(
              eq(plus(scalar(new int[]{1000, 100, 10, 1}, SEND),
                      scalar(new int[]{1000, 100, 10, 1}, MORE)),
                      scalar(new int[]{10000, 1000, 100, 10, 1}, MONEY))
      );
  }
  
  public void buildSolver() {
      _s = new CPSolver();
      _s.read(_m);
  }
  
  public void solve() {
      Logger.info(SendMoreMoneyTest.class, "PROPAGATION");
      //_s.propagate();
      Logger.info(SendMoreMoneyTest.class, "RESOLUTION");
      _s.solve();
  }
  
}
