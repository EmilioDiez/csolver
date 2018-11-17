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
package samples.tutorials.to_sort.scheduling;

import static csolver.kernel.CSolver.disjunctive;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.makeTaskVarArray;
import static csolver.kernel.Options.V_BOUND;
import static csolver.kernel.Options.V_MAKESPAN;
import static csolver.kernel.Options.V_NO_DECISION;
import static csolver.kernel.Options.V_OBJECTIVE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.configure.LimitFactory;
import csolver.cp.solver.configure.StrategyFactory;
import csolver.cp.solver.search.BranchingFactory;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.common.util.tools.MathUtils;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.search.limit.Limit;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;

public class OpenShopScheduling extends PatternExample {


    //@Option(name = "-f", usage = "Data file, format:\n\"n m\n d_1_1 d_1_2 ... d_1_m\n ...\n d_n_1 d_n_2 ... d_n_m\"", required = false)
    String filename = "";


    private int[][] durations = {
            {661, 168, 171},
            {6, 489, 505},
            {333, 343, 324}
    };

    //@Option(name = "-b", usage = "Branching choice (default: 3)", required = false)
    public int branching = 3;

    public void setDurations(int[][] durations) {
        this.durations = durations;
    }

    @Override
    public void buildModel() {
        if (!filename.equals("")) {
            durations = parse(filename);
        }

        model = new CPModel();
        //Variables
        final int h = MathUtils.sum(durations); // dummy horizon
        final TaskVariable[][] tasks = makeTaskVarArray("T", 0, h, durations, V_BOUND);
        model.addVariable(makeIntVar("makespan", 0, h, V_BOUND, V_OBJECTIVE, V_MAKESPAN, V_NO_DECISION));
        //Machines
        for (int i = 0; i < tasks.length; i++) {
            model.addConstraint(disjunctive(tasks[i]));
        }
        //Jobs
        for (int i = 0; i < tasks[0].length; i++) {
            model.addConstraint(disjunctive(ArrayUtils.getColumn(tasks, i)));
        }
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        StrategyFactory.setDoOptimize(solver, false);
        solver.getConfiguration().putBoolean(Configuration.STOP_AT_FIRST_SOLUTION, false);
        LimitFactory.setSearchLimit(solver, Limit.TIME, 5000);
        solver.clearGoals();
        switch (branching) {
            case 0:
                solver.addGoal(BranchingFactory.setTimes(solver));
                break;
            case 1:
                solver.addGoal(BranchingFactory.setTimes(solver, 0));
                break;
            case 2:
                ((CPSolver) solver).setRandomSelectors();
                break;
            default: //default branching
                break;
        }
    }

    @Override
    public void prettyOut() {
        if (Logger.isLoggable(Level.INFO) && solver.existsSolution()) {
            Logger.info(OpenShopScheduling.class,"makespan:" + solver.getObjectiveValue());
            final String title = "Disjunctive Constraints Visualization";
            //ChocoChartFactory.createAndShowGUI(title, ChocoChartFactory.createUnaryHChart(title, solver));
        }
    }

    @Override
    public void solve() {
        solver.generateSearchStrategy();
        solver.launch();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new OpenShopScheduling().execute(args);

    }

    ////////////////////////////////////////////////////

    protected int[][] parse(String fileName) {
        int[][] data = null;
        try {
            Scanner sc = new Scanner(new File(fileName));
            int nb1 = sc.nextInt();
            int nb2 = sc.nextInt();
            data = new int[nb1][nb2];
            sc.nextLine();

            for (int i = 0; i < nb1; i++) {
                for (int j = 0; j < nb2; i++) {
                    data[i][j] = sc.nextInt();
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

}
