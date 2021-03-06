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
package samples.tutorials;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import csolver.cp.solver.search.integer.valselector.MaxVal;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.cp.solver.variables.integer.IntVarEvent;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.model.variables.integer.IntegerVariable;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 12/06/12
 */
public class StressTest2 extends PatternExample {

    //@Option(name = "-k", usage = "number of times round the loop.", required = false)
    int k = 100;

    //@Option(name = "-n", usage = "number of iterations of change per loop .", required = false)
    int n = 100;

    //@Option(name = "-m", usage = "m^2 propagators per change of loop.", required = false)
    int m = 100;

    IntegerVariable[] x, y;

    @Override
    public void buildModel() {
        model = new CPModel();

        y = CSolver.makeIntVarArray("y", n+1, 0, k * n, Options.V_BOUND);
        x = CSolver.makeIntVarArray("x", m+1, 0, k * n, Options.V_BOUND);

        for (int i = 1; i < n; i++) {
//            y[i-1] - y[i] <= 0
            model.addConstraint(CSolver.leq(y[i], y[i + 1]));
        }
        for (int i = 1; i <= n; i++) {
//            y[0] - y[i] <= n - i + 1
            model.addConstraint(CSolver.leq(CSolver.minus(y[0], y[i]), n - i + 1));
        }
//        y[n] - x[0] <= 0;
        model.addConstraint(CSolver.leq(y[n], x[0]));

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j <= m; j++) {
//                x[i] - x[j] <= 0
                model.addConstraint(CSolver.leq(x[i], x[j]));
            }
        }
//        x[m] - y[0] <= - 2;
        model.addConstraint(CSolver.geq(y[0], CSolver.plus(x[m], 2)));

    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.addGoal(new AssignOrForbidIntVarVal(new MinDomain(solver, solver.getVar(x)), new MaxVal()));
    }

    @Override
    public void solve() {
        IntVarEvent.propagations = 0;
        solver.solve();
    }

    @Override
    public void prettyOut() {
        System.out.printf("%dms\n", solver.getReadingTimeCount());
        System.out.printf("%d prop.\n", IntVarEvent.propagations);
    }

    public static void main(String[] args) {
        new StressTest2().execute(args);
    }
}
