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
import csolver.cp.solver.search.integer.valselector.MinVal;
import csolver.cp.solver.search.integer.varselector.StaticVarOrder;
import csolver.cp.solver.variables.integer.IntVarEvent;
import csolver.kernel.CSolver;
import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.model.variables.integer.IntegerVariable;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 12/06/12
 */
public class StressTest3 extends PatternExample {

    //@Option(name = "-n", usage = "number of variables.", required = false)
    int n = 10;

    IntegerVariable[] vars;

    @Override
    public void buildModel() {
        model = new CPModel();
        vars = CSolver.makeIntVarArray("p", n, 0, 1);
        for (int i = 0; i < n - 1; i++) {
            model.addConstraint(CSolver.eq(vars[i], vars[i + 1]));
        }
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.addGoal(new AssignOrForbidIntVarVal(new StaticVarOrder(solver, solver.getVar(vars)), new MinVal()));
    }

    @Override
    public void solve() {
        
        IntVarEvent.propagations = 0;
        solver.solve();
            }

    @Override
    public void prettyOut() {
        System.out.printf("%dms\n", solver.getReadingTimeCount());
        System.out.printf("%dms\n", solver.getInitialPropagationTimeCount());
        System.out.printf("%d prop.\n", IntVarEvent.propagations);
    }

    public static void main(String[] args) {
        new StressTest3().execute(args);
    }
}
