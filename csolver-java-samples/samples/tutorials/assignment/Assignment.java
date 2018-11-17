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
package samples.tutorials.assignment;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.BranchingFactory;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.variables.integer.IntegerVariable;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/01/11
 */
public class Assignment extends PatternExample {

    IntegerVariable[] P, C;
    IntegerVariable Cmax;


    @Override
    public void printDescription() {
        super.printDescription();
        Logger.info("We have three persons Person_1, Person_2, Person_3 and three jobs Job_1,Job_2,Job_3.");
        Logger.info("A matrix gives the cost of assigning Person_i to Job_j. ");
        Logger.info("Find the assignments and their respective cost (distinct persons should be assigned to distinct jobs).");
        Logger.info("9 4 2");
        Logger.info("1 0 6");
        Logger.info("8 8 4");
    }

    @Override
    public void buildModel() {
        int n = 3;
        model = new CPModel();

        P = CSolver.makeIntVarArray("Person", n, 1, 3);
        C = CSolver.makeIntVarArray("C", n, 0, 9, Options.V_ENUM);

        Cmax = CSolver.makeIntVar("Cmax", 0, 3 * 9, Options.V_OBJECTIVE);

        model.addConstraint(CSolver.allDifferent(P));

        model.addConstraint(CSolver.nth(Options.C_NTH_G, P[0], new int[]{9, 4, 2}, C[0], 1));
        model.addConstraint(CSolver.nth(Options.C_NTH_G, P[1], new int[]{1, 0, 6}, C[1], 1));
        model.addConstraint(CSolver.nth(Options.C_NTH_G, P[2], new int[]{8, 8, 4}, C[2], 1));

        model.addConstraint(CSolver.eq(Cmax, CSolver.sum(C)));
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.addGoal(BranchingFactory.minDomIncDom(solver, solver.getVar(C)));
    }

    @Override
    public void solve() {
        solver.minimize(true);
    }

    @Override
    public void prettyOut() {
        Logger.info("\n");
        for (int i = 0; i < P.length; i++) {
            Logger.info(P[i].getName() + " is assigned to the job_" + solver.getVar(P[i]).getVal() + " (cost : " + solver.getVar(C[i]).getVal() + ")");
        }
        Logger.info("Assignment cost is " + solver.getVar(Cmax).getVal() + ".");
    }

    public static void main(String[] args) {
        new Assignment().execute(args);
    }
}
