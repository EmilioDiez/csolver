/**
 *  Copyright (c) 1999-2011, Ecole des Mines de Nantes
 *  All rights reserved.
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
 */
package samples.documentation;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import csolver.cp.solver.search.integer.valiterator.DecreasingDomain;
import csolver.cp.solver.search.integer.valselector.MinVal;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerExpressionVariable;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.search.ValSelector;
import csolver.kernel.solver.variables.integer.IntDomainVar;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 29/06/11
 */
public class Code4Tut1 {

    private void tgettingstarted() {
        //totex tgetstar
        //totex tgetstar1
        //constants of the problem:
        int n = 3;
        int M = n * (n * n + 1) / 2;
        // Our model
        Model m = new CPModel();
        //totex
        //totex tgetstar2
        IntegerVariable[][] cells = new IntegerVariable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = CSolver.makeIntVar("cell" + j, 1, n * n);
            }
        }
        //totex
        //totex tgetstar3
        //Constraints
        // ... over rows
        Constraint[] rows = new Constraint[n];
        for (int i = 0; i < n; i++) {
            rows[i] = CSolver.eq(CSolver.sum(cells[i]), M);
        }
        //totex
        //totex tgetstar4
        m.addConstraints(rows);
        //totex
        //totex tgetstar5
        //... over columns
        // first, get the columns, with a temporary array
        IntegerVariable[][] cellsDual = new IntegerVariable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cellsDual[i][j] = cells[j][i];
            }
        }
        //totex
        //totex tgetstar6
        Constraint[] cols = new Constraint[n];
        for (int i = 0; i < n; i++) {
            cols[i] = CSolver.eq(CSolver.sum(cellsDual[i]), M);
        }
        m.addConstraints(cols);
        //totex
        //totex tgetstar7
        //... over diagonals
        IntegerVariable[][] diags = new IntegerVariable[2][n];
        for (int i = 0; i < n; i++) {
            diags[0][i] = cells[i][i];
            diags[1][i] = cells[i][(n - 1) - i];
        }
        //totex
        //totex tgetstar8
        m.addConstraint(CSolver.eq(CSolver.sum(diags[0]), M));
        m.addConstraint(CSolver.eq(CSolver.sum(diags[1]), M));
        //totex
        //totex tgetstar9
        //All cells are different from each other
        IntegerVariable[] allVars = new IntegerVariable[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                allVars[i * n + j] = cells[i][j];
            }
        }
        m.addConstraint(CSolver.allDifferent(allVars));
        //totex
        //totex tgetstar10
        //Our solver
        Solver s = new CPSolver();
        //totex
        //totex tgetstar11
        //read the model
        s.read(m);
        //totex
        //totex tgetstar12
        //solve the problem
        s.solve();
        //totex
        //totex tgetstar13
        //Print the values
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(s.getVar(cells[i][j]).getVal() + " ");
            }
            System.out.println();
        }
        //totex
        //totex
    }

    private void tmysteriousproblem() {
        //totex tmysterious
        // Build a model
        Model m = new CPModel();

        // Build enumerated domain variables
        IntegerVariable x1 = CSolver.makeIntVar("var1", 0, 5);
        IntegerVariable x2 = CSolver.makeIntVar("var2", 0, 5);
        IntegerVariable x3 = CSolver.makeIntVar("var3", 0, 5);

        // Build the constraints
        Constraint C1 = CSolver.gt(x1, x2);
        Constraint C2 = CSolver.neq(x1, x3);
        Constraint C3 = CSolver.gt(x2, x3);

        // Add the constraints to the Choco model
        m.addConstraint(C1);
        m.addConstraint(C2);
        m.addConstraint(C3);

        // Build a solver
        Solver s = new CPSolver();

        // Read the model
        s.read(m);

        // Solve the problem
        s.solve();

        // Print the variable domains
        System.out.println("var1 =" + s.getVar(x1).getVal());
        System.out.println("var2 =" + s.getVar(x2).getVal());
        System.out.println("var3 =" + s.getVar(x3).getVal());
        //totex
    }

    private void tdonald() {
        //totex tdonald
        // Build model
        Model model = new CPModel();

        // Declare every letter as a variable
        IntegerVariable d = CSolver.makeIntVar("d", 0, 9, Options.V_ENUM);
        IntegerVariable o = CSolver.makeIntVar("o", 0, 9, Options.V_ENUM);
        IntegerVariable n = CSolver.makeIntVar("n", 0, 9, Options.V_ENUM);
        IntegerVariable a = CSolver.makeIntVar("a", 0, 9, Options.V_ENUM);
        IntegerVariable l = CSolver.makeIntVar("l", 0, 9, Options.V_ENUM);
        IntegerVariable g = CSolver.makeIntVar("g", 0, 9, Options.V_ENUM);
        IntegerVariable e = CSolver.makeIntVar("e", 0, 9, Options.V_ENUM);
        IntegerVariable r = CSolver.makeIntVar("r", 0, 9, Options.V_ENUM);
        IntegerVariable b = CSolver.makeIntVar("b", 0, 9, Options.V_ENUM);
        IntegerVariable t = CSolver.makeIntVar("t", 0, 9, Options.V_ENUM);

        // Declare every name as a variable
        IntegerVariable donald = CSolver.makeIntVar("donald", 0, 1000000, Options.V_BOUND);
        IntegerVariable gerald = CSolver.makeIntVar("gerald", 0, 1000000, Options.V_BOUND);
        IntegerVariable robert = CSolver.makeIntVar("robert", 0, 1000000, Options.V_BOUND);

        // Array of coefficients
        int[] c = new int[]{100000, 10000, 1000, 100, 10, 1};

        // Declare every combination of letter as an integer expression
        IntegerExpressionVariable donaldLetters = CSolver.scalar(new IntegerVariable[]{d, o, n, a, l, d}, c);
        IntegerExpressionVariable geraldLetters = CSolver.scalar(new IntegerVariable[]{g, e, r, a, l, d}, c);
        IntegerExpressionVariable robertLetters = CSolver.scalar(new IntegerVariable[]{r, o, b, e, r, t}, c);

        // Add equality between name and letters combination
        model.addConstraint(CSolver.eq(donaldLetters, donald));
        model.addConstraint(CSolver.eq(geraldLetters, gerald));
        model.addConstraint(CSolver.eq(robertLetters, robert));
        // Add constraint name sum
        model.addConstraint(CSolver.eq(CSolver.plus(donald, gerald), robert));
        // Add constraint of all different letters.
        model.addConstraint(CSolver.allDifferent(d, o, n, a, l, g, e, r, b, t));

        // Build a solver, read the model and solve it
        Solver s = new CPSolver();
        s.read(model);
        s.solve();

        // Print name value
        System.out.println("donald = " + s.getVar(donald).getVal());
        System.out.println("gerald = " + s.getVar(gerald).getVal());
        System.out.println("robert = " + s.getVar(robert).getVal());
        //totex
    }

    private void tsudokunaive() {
        //totex tsudokunaive
        int n = 9;
        // Build Model
        Model m = new CPModel();

        // Build an array of integer variables
        IntegerVariable[][] rows = CSolver.makeIntVarArray("rows", n, n, 1, n, Options.V_ENUM);

        // Not equal constraint between each case of a row
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                for (int k = j; k < n; k++)
                    if (k != j) m.addConstraint(CSolver.neq(rows[i][j], rows[i][k]));
        }

        // Not equal constraint between each case of a column
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++)
                for (int k = 0; k < n; k++)
                    if (k != i) m.addConstraint(CSolver.neq(rows[i][j], rows[k][j]));
        }

        // Not equal constraint between each case of a sub region
        for (int ci = 0; ci < n; ci += 3) {
            for (int cj = 0; cj < n; cj += 3)
                // Extraction of disequality of a sub region
                for (int i = ci; i < ci + 3; i++)
                    for (int j = cj; j < cj + 3; j++)
                        for (int k = ci; k < ci + 3; k++)
                            for (int l = cj; l < cj + 3; l++)
                                if (k != i || l != j) m.addConstraint(CSolver.neq(rows[i][j], rows[k][l]));
        }
        // Call solver
        Solver s = new CPSolver();
        s.read(m);
        
        s.solve();
        // print the grid
        //totex
    }

    private void tsudokualldiff() {
        //totex tsudokualldiff
        int n = 9;
        // Build model
        Model m = new CPModel();
        // Declare variables
        IntegerVariable[][] rows = CSolver.makeIntVarArray("rows", n, n, 1, n, Options.V_ENUM);
        IntegerVariable[][] cols = new IntegerVariable[n][n];

        // Channeling between rows and columns
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                cols[i][j] = rows[j][i];
        }

        // Add alldifferent constraint
        for (int i = 0; i < n; i++) {
            m.addConstraint(CSolver.allDifferent(cols[i]));
            m.addConstraint(CSolver.allDifferent(rows[i]));
        }

        // Define sub regions
        IntegerVariable[][] carres = new IntegerVariable[n][n];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    carres[j + k * 3][i] = rows[0 + k * 3][i + j * 3];
                    carres[j + k * 3][i + 3] = rows[1 + k * 3][i + j * 3];
                    carres[j + k * 3][i + 6] = rows[2 + k * 3][i + j * 3];
                }
            }
        }

        // Add alldifferent on sub regions
        for (int i = 0; i < n; i++) {
            Constraint c = CSolver.allDifferent(carres[i]);
            m.addConstraint(c);
        }

        // Call solver
        Solver s = new CPSolver();
        s.read(m);
        
        s.solve();
        // print grid
        //totex
    }

    private void tknapsack() {
        //totex tknapsack1
        Model m = new CPModel();

        IntegerVariable obj1 = CSolver.makeIntVar("obj1", 0, 5, Options.V_ENUM);
        IntegerVariable obj2 = CSolver.makeIntVar("obj2", 0, 7, Options.V_ENUM);
        IntegerVariable obj3 = CSolver.makeIntVar("obj3", 0, 10, Options.V_ENUM);
        IntegerVariable c = CSolver.makeIntVar("cost", 1, 1000000, Options.V_BOUND);

        int capacity = 34;
        int[] volumes = new int[]{7, 5, 3};
        int[] energy = new int[]{6, 4, 2};

        m.addConstraint(CSolver.leq(CSolver.scalar(volumes, new IntegerVariable[]{obj1, obj2, obj3}), capacity));
        m.addConstraint(CSolver.eq(CSolver.scalar(energy, new IntegerVariable[]{obj1, obj2, obj3}), c));

        Solver s = new CPSolver();
        s.read(m);
        //totex
        //totex tknapsack3
        s.setValIntIterator(new DecreasingDomain());
        //totex
        //totex tknapsack2
        s.maximize(s.getVar(c), false);
        //totex
    }

    private void tqueensrow() {
        //totex tqueensrow
        int n = 12;
        Model m = new CPModel();

        IntegerVariable[] queens = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = CSolver.makeIntVar("Q" + i, 1, n, Options.V_ENUM);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(CSolver.neq(queens[i], queens[j]));
                m.addConstraint(CSolver.neq(queens[i], CSolver.plus(queens[j], k)));  // diagonal
                m.addConstraint(CSolver.neq(queens[i], CSolver.minus(queens[j], k))); // diagonal
            }
        }

        Solver s = new CPSolver();
        s.read(m);
        
        s.solve();
        //totex
    }

    private void tqueensredund() {
        //totex tqueensredund
        int n = 12;
        Model m = new CPModel();

        IntegerVariable[] queens = new IntegerVariable[n];
        IntegerVariable[] queensdual = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = CSolver.makeIntVar("Q" + i, 1, n, Options.V_ENUM);
            queensdual[i] = CSolver.makeIntVar("QD" + i, 1, n, Options.V_ENUM);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(CSolver.neq(queens[i], queens[j]));
                m.addConstraint(CSolver.neq(queens[i], CSolver.plus(queens[j], k)));  // diagonal
                m.addConstraint(CSolver.neq(queens[i], CSolver.minus(queens[j], k))); // diagonal
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(CSolver.neq(queensdual[i], queensdual[j]));
                m.addConstraint(CSolver.neq(queensdual[i], CSolver.plus(queensdual[j], k)));  // diagonal
                m.addConstraint(CSolver.neq(queensdual[i], CSolver.minus(queensdual[j], k))); // diagonal
            }
        }
        m.addConstraint(CSolver.inverseChanneling(queens, queensdual));

        Solver s = new CPSolver();
        s.read(m);

        s.addGoal(new AssignOrForbidIntVarVal(new MinDomain(s, s.getVar(queens)), new MinVal()));

        
        s.solve();
        //totex
    }

    //totex tqueensel
    class NQueenValueSelector implements ValSelector<IntDomainVar> {

        // Column variable
        protected IntDomainVar[] dualVar;

        // Constructor of the value selector,
        public NQueenValueSelector(IntDomainVar[] cols) {
            this.dualVar = cols;
        }

        // Returns the "best val" that is the smallest column domain size OR -1
        // (-1 is not in the domain of the variables)
        public int getBestVal(IntDomainVar intDomainVar) {
            int minValue = Integer.MAX_VALUE;
            int v0 = -1;
            int UB = intDomainVar.getSup();
            for (int i = intDomainVar.getInf(); i <= UB; i = intDomainVar.getNextDomainValue(i)) {
                int val = dualVar[i - 1].getDomainSize();
                if (val < minValue) {
                    minValue = val;
                    v0 = i;
                }
            }
            return v0;
        }
    }
    //totex

    private void tqueensalldiff() {
        //totex tqueensalldiff
        int n = 12;
        Model m = new CPModel();

        IntegerVariable[] queens = new IntegerVariable[n];
        IntegerVariable[] queensdual = new IntegerVariable[n];
        IntegerVariable[] diag1 = new IntegerVariable[n];
        IntegerVariable[] diag2 = new IntegerVariable[n];
        IntegerVariable[] diag1dual = new IntegerVariable[n];
        IntegerVariable[] diag2dual = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = CSolver.makeIntVar("Q" + i, 1, n, Options.V_ENUM);
            queensdual[i] = CSolver.makeIntVar("QD" + i, 1, n, Options.V_ENUM);
            diag1[i] = CSolver.makeIntVar("D1" + i, 1, 2 * n, Options.V_ENUM);
            diag2[i] = CSolver.makeIntVar("D2" + i, -n, n, Options.V_ENUM);
            diag1dual[i] = CSolver.makeIntVar("D1" + i, 1, 2 * n, Options.V_ENUM);
            diag2dual[i] = CSolver.makeIntVar("D2" + i, -n, n, Options.V_ENUM);
        }

        m.addConstraint(CSolver.allDifferent(queens));
        m.addConstraint(CSolver.allDifferent(queensdual));
        for (int i = 0; i < n; i++) {
            m.addConstraint(CSolver.eq(diag1[i], CSolver.plus(queens[i], i)));
            m.addConstraint(CSolver.eq(diag2[i], CSolver.minus(queens[i], i)));
            m.addConstraint(CSolver.eq(diag1dual[i], CSolver.plus(queensdual[i], i)));
            m.addConstraint(CSolver.eq(diag2dual[i], CSolver.minus(queensdual[i], i)));
        }
        m.addConstraint(CSolver.inverseChanneling(queens, queensdual));

        m.addConstraint(CSolver.allDifferent(diag1));
        m.addConstraint(CSolver.allDifferent(diag2));
        m.addConstraint(CSolver.allDifferent(diag1dual));
        m.addConstraint(CSolver.allDifferent(diag2dual));

        Solver s = new CPSolver();
        s.read(m);

        s.addGoal(new AssignOrForbidIntVarVal(new MinDomain(s, s.getVar(queens)), new MinVal()));

        
        s.solve();
        //totex
    }

}
