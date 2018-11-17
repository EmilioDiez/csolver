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
package samples.tutorials.puzzles;


import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;

import java.text.MessageFormat;
import java.util.List;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.branching.AssignVar;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solution;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;

public class Queen extends PatternExample {

    //@Option(name = "-n", usage = "Number of queens (default: 8)", required = false)
    protected int n = 10;
    IntegerVariable[] queens;

    @Override
    public void printDescription() {
        Logger.info("The queen's problem asks how to place n queens on an n x n chess board ");
        Logger.info("so that none of them can hit any other in one move.");
        Logger.info("(http://www.csplib.org/)");
        Logger.info(MessageFormat.format("Here n = {0}\n\n", n));
    }

    @Override
    public void buildModel() {
        model = new CPModel();

        // create variables
        queens = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = makeIntVar("Q" + i, 1, n);
        }
        model.addConstraint(Options.C_ALLDIFFERENT_BC, CSolver.allDifferent(queens));

        // all different constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                model.addConstraint(neq(queens[i], plus(queens[j], k)));
                model.addConstraint(neq(queens[i], minus(queens[j], k)));
            }
        }
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.addGoal(new AssignVar(new MinDomain(solver, solver.getVar(queens)), new IncreasingDomain()));
    }

    @Override
    public void solve() {
        solver.solveAll();
    }

    @Override
    public void prettyOut() {
        //	Logger.info("feasible: " + solver.isFeasible());
        //	Logger.info("nbSol: " + solver.getNbSolutions());
        // Display
        // -------
        StringBuffer ret = new StringBuffer();
        List<Solution> sols = solver.getSearchStrategy().getStoredSolutions();
        ret.append(MessageFormat.format("The {0} last solutions (among {1} solutions) are:\n", sols.size(), solver.getNbSolutions()));
        String line = "+";
        for (int i = 0; i < n; i++) {
            line += "---+";
        }
        line += "\n";
        for (int sol = 0; sol < sols.size(); sol++) {
            Solution solution = sols.get(sol);
            ret.append(line);
            for (int i = 0; i < n; i++) {
                ret.append("|");
                for (int j = 0; j < n; j++) {
                    ret.append((solution.getIntValue(i) == j + 1) ? " * |" : "   |");
                }
                ret.append(MessageFormat.format("\n{0}", line));
            }
            ret.append("\n\n\n");
        }
//        Logger.info(ret.toString());
        System.out.printf("%s\n", ret.toString());
        solver.printRuntimeStatistics();
    }

    public static void main(String[] args) {
        new Queen().execute(args);
    }

	public void readArgs(int q) {
		n = q;
		
	}
}
