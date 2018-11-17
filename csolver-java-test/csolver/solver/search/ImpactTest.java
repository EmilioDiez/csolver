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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.branching.ImpactBasedBranching;
import csolver.cp.solver.search.integer.branching.domwdeg.DomOverWDegBranchingNew;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;

public class ImpactTest {

    

    @Test
    public void test1() {
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] vs1 = CSolver.makeIntVarArray("v1", 10, 2, 10, Options.V_ENUM);

        for (int i = 0; i < vs1.length; i++) {
            for (int j = i + 1; j < vs1.length; j++) {
                m.addConstraint(CSolver.neq(vs1[i], vs1[j]));
            }
        }

        s.read(m);
        ImpactBasedBranching ibb = new ImpactBasedBranching(s);
        ibb.getImpactStrategy().initImpacts(100);
        s.addGoal(ibb);
        s.solve();
        assertEquals(0, s.getSolutionCount());
        assertEquals(260650, s.getNodeCount());

    }

    @Test
    public void test2() {
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable[] vs1 = CSolver.makeIntVarArray("v1", 10, 2, 10, Options.V_BOUND);

        for (int i = 0; i < vs1.length; i++) {
            for (int j = i + 1; j < vs1.length; j++) {
                m.addConstraint(CSolver.neq(vs1[i], vs1[j]));
            }
        }
        s.read(m);

        ImpactBasedBranching ibb = new ImpactBasedBranching(s);
        ibb.getImpactStrategy().initImpacts(100);
        s.addGoal(ibb);

        s.solve();
        assertEquals(0, s.getSolutionCount());
        assertEquals(260650, s.getNodeCount());
    }

    @Test
    public void testMagicSquare() {
        testMagicSquare(4, 9);
        testMagicSquare(7, 289);
        testMagicSquare(9, 1005);

        testMagicSquareRestartDwdeg(4, 9);
        testMagicSquareRestartDwdeg(7, 9487);
    }

    public void testMagicSquare(int n, int nnodes) {
        Model m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] vars = CSolver.makeIntVarArray("C", n * n, 1, n * n);
        m.addVariables(Options.V_ENUM, vars);

        IntegerVariable sum = CSolver.makeIntVar("sum", 1, n * n * (n * n + 1) / 2);
        m.addVariable(sum);

        m.addConstraint(Options.C_ALLDIFFERENT_BC, CSolver.allDifferent(vars));
        for (int i = 0; i < n; i++) {
            IntegerVariable[] col = new IntegerVariable[n];
            IntegerVariable[] row = new IntegerVariable[n];

            for (int j = 0; j < n; j++) {
                col[j] = vars[i * n + j];
                row[j] = vars[j * n + i];
            }

            m.addConstraint(CSolver.eq(CSolver.sum(col), sum));
            m.addConstraint(CSolver.eq(CSolver.sum(row), sum));
        }

        m.addConstraint(CSolver.eq(sum, n * (n * n + 1) / 2));

        CPSolver s = new CPSolver();
        s.read(m);
        ImpactBasedBranching ibb = new ImpactBasedBranching(s);
        ibb.getImpactStrategy().initImpacts(1000000);

        s.setTimeLimit(65000);
        s.setGeometricRestart(14, 1.5d);
        s.addGoal(ibb);
        s.solve();
        assertEquals(nnodes, s.getNodeCount());
    }

    public void testMagicSquareRestartDwdeg(int n, int nnodes) {
        Model m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] vars = CSolver.makeIntVarArray("C", n * n, 1, n * n);
        m.addVariables(Options.V_ENUM, vars);

        IntegerVariable sum = CSolver.makeIntVar("sum", 1, n * n * (n * n + 1) / 2);
        m.addVariable(sum);

        m.addConstraint(Options.C_ALLDIFFERENT_BC, CSolver.allDifferent(vars));
        for (int i = 0; i < n; i++) {
            IntegerVariable[] col = new IntegerVariable[n];
            IntegerVariable[] row = new IntegerVariable[n];

            for (int j = 0; j < n; j++) {
                col[j] = vars[i * n + j];
                row[j] = vars[j * n + i];
            }

            m.addConstraint(CSolver.eq(CSolver.sum(col), sum));
            m.addConstraint(CSolver.eq(CSolver.sum(row), sum));
        }

        m.addConstraint(CSolver.eq(sum, n * (n * n + 1) / 2));

        CPSolver s = new CPSolver();
        s.read(m);

        s.setGeometricRestart(14, 1.5d);
        s.addGoal(new DomOverWDegBranchingNew(s, s.getVar(vars), new IncreasingDomain(), 0));
        s.solve();

        assertEquals(nnodes, s.getNodeCount());
    }

}
