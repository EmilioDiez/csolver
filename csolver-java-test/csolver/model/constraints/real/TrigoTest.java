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

package csolver.model.constraints.real;

import static csolver.kernel.CSolver.cos;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeRealVar;
import static csolver.kernel.CSolver.sin;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.real.exp.RealCos;
import csolver.cp.solver.constraints.real.exp.RealMinus;
import csolver.cp.solver.constraints.real.exp.RealSin;
import csolver.cp.solver.search.real.AssignInterval;
import csolver.cp.solver.search.real.CyclicRealVarSelector;
import csolver.cp.solver.search.real.RealIncreasingDomain;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.real.RealVariable;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.real.RealExp;
import csolver.kernel.solver.variables.real.RealVar;
import nitoku.log.Logger;
public class TrigoTest {

    

    @Test
    public void test1() {
    	CPSolver s = new CPSolver();
        RealVar alpha = s.createRealVal("alpha", -Math.PI, Math.PI);

        RealExp exp = new RealMinus(s,
                new RealCos(s, alpha),
                new RealSin(s, alpha));
        SConstraint c = s.makeEquation(exp, s.cst(0.0));
        Logger.info("c = " + c.pretty());
        s.post(s.makeEquation(exp, s.cst(0.0)));

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue("Nb sols", s.getNbSolutions() >= 2);
        assertTrue("Precision", Math.abs(Math.cos(alpha.getInf()) - Math.sin(alpha.getInf())) < 1e-8);
    }

    @Test
    public void test1bis() {
        CPModel m = new CPModel();

        RealVariable alpha = makeRealVar("alpha", -Math.PI, Math.PI);
        Constraint exp = eq(cos(alpha), sin(alpha));
        m.addConstraint(exp);


        CPSolver s = new CPSolver();
        s.read(m);
        Logger.info("eq = " + s.getCstr(exp).pretty());

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue(s.getNbSolutions() >= 2);
        assertTrue(Math.abs(Math.cos(s.getVar(alpha).getInf()) - Math.sin(s.getVar(alpha).getInf())) < 1e-8);
    }

    @Test
    public void test2() {
        CPSolver s = new CPSolver();
        RealVar alpha = s.createRealVal("alpha", -5.5 * Math.PI, -1.5 * Math.PI);
        RealExp exp = new RealCos(s, alpha);
        s.post(s.makeEquation(exp, s.cst(1.0)));

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue(s.getNbSolutions() >= 2);
    }


    @Test
    public void test2bis() {
        CPModel m = new CPModel();

        RealVariable alpha = makeRealVar("alpha", -5.5 * Math.PI, -1.5 * Math.PI);
        m.addVariable(alpha);
        m.addConstraint(eq(cos(alpha), 1));


        CPSolver s = new CPSolver();
        s.read(m);

        boolean first = false;
        s.setFirstSolution(first);
        s.generateSearchStrategy();
        s.addGoal(new AssignInterval(new CyclicRealVarSelector(s), new RealIncreasingDomain()));
        s.launch();

        assertTrue(s.getNbSolutions() >= 2);
    }
}
