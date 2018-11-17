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
package csolver.solver.branch;

import static csolver.kernel.CSolver.MAX_UPPER_BOUND;
import static csolver.kernel.CSolver.abs;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.minus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.preprocessor.PreProcessCPSolver;
import csolver.cp.solver.preprocessor.PreProcessConfiguration;
import csolver.cp.solver.search.integer.branching.AssignVar;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.cp.solver.search.integer.valselector.MinVal;
import csolver.cp.solver.search.integer.varselector.StaticVarOrder;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.search.integer.AbstractIntVarSelector;
import csolver.kernel.solver.variables.integer.IntDomainVar;

public class StrategyTest {


    static class IncorrectVarSelector extends AbstractIntVarSelector {
        IncorrectVarSelector(IntDomainVar[] vars) {
            super(null, vars);
        }

        public IntDomainVar selectVar() {
            if (!vars[0].isInstantiated()) {
                return vars[0];
            }
            return null;
        }
    }

    @Test
    @Ignore
    public void badSelectors() {
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = CSolver.makeIntVar("v1", 0, 2, Options.V_ENUM);
        IntegerVariable v2 = CSolver.makeIntVar("v2", 0, 3);
        m.addConstraint(CSolver.leq(v1, v2));
        s.read(m);
        s.setVarIntSelector(new IncorrectVarSelector(s.getVar(v1, v2)));
        s.setValIntSelector(new MinVal());
        try {
            s.solve();
            fail("Every variables have been instantiated");
        } catch (Exception e) {

        }
    }

    @Test
    public void testB2986005() {
        CPSolver s = new CPSolver();
        IntDomainVar v = s.createEnumIntVar("v", 1, 2);
        s.attachGoal(new AssignVar(new StaticVarOrder(s, new IntDomainVar[]{v}), new IncreasingDomain()));
        s.solveAll();
        // one ROOT node  + 1 node (v=1) + 1 node (v=2)
        assertEquals("incorrect nb of nodes", 3, s.getNodeCount());
    }

    @Test
    public void testB2986006() {
        CPModel m = new CPModel();
        int n = 99;
        IntegerVariable[] vars = new IntegerVariable[n];
        for (int i = 0; i < n - 1; i++) {
            vars[i] = CSolver.makeIntVar("v" + i, 0, n, Options.V_BOUND);
        }
        vars[n - 1] = CSolver.makeIntVar("v" + (n - 1), 0, 1, Options.V_BOUND);
        for (int i = 0; i < n - 1; i++) {
            m.addConstraint(CSolver.eq(vars[i], vars[i + 1]));
        }
//        m.addConstraint(Choco.allDifferent(Options.C_ALLDIFFERENT_BC, vars));
        CPSolver s = new CPSolver();
        s.read(m);
        s.solve();
    }

    @Test
    public void testB29860067() {
        IntegerVariable v1 = makeIntVar("v", 0, CSolver.MAX_UPPER_BOUND, Options.V_BOUND);
        IntegerVariable v2 = makeIntVar("abs(v-c)", 0, MAX_UPPER_BOUND, Options.V_BOUND);
        CPModel mod = new CPModel();
        mod.addConstraint(eq(v2, abs(minus(v1, 10))));
        mod.setDefaultExpressionDecomposition(true);
        Solver s = new PreProcessCPSolver();
        PreProcessConfiguration.cancelPreProcess(s.getConfiguration());
        s.read(mod);
        System.out.println(s.pretty());
    }


}
