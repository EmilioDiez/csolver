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
package samples.tutorials.scheduling;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/01/11
 */
public class ProjectScheduling extends PatternExample {

    TaskVariable[] tasks;
    IntegerVariable[] durations;
    IntegerVariable[] resources;

    @Override
    public void printDescription() {
        super.printDescription();
        Logger.info("We have 7 tasks. Each task is defined by a duration, a set a precedence rules and also requires a given number of persons.");
        Logger.info("Knowing that we have at most 7 persons, find the earliest end");
        Logger.info("Task_1 (duration = 2), starts before Task_2, Task_3 and Task_4");
        Logger.info("Task_2 (duration = 1), starts before Task_5");
        Logger.info("Task_3 (duration = 4), starts before Task_6");
        Logger.info("Task_4 (duration = 2), starts before Task_5");
        Logger.info("Task_5 (duration = 3), starts before Task_7");
        Logger.info("Task_6 (duration = 1), starts before Task_7");
        Logger.info("Task_7 (duration = 0)");
    }

    @Override
    public void buildModel() {
        model = new CPModel();

        durations = CSolver.constantArray(new int[]{2, 1, 4, 2, 3, 1, 0});
        resources = CSolver.constantArray(new int[]{1, 3, 3, 2, 4, 6, 0});

        tasks = CSolver.makeTaskVarArray("Task", 0, 13, durations);

        model.addConstraint(CSolver.endsAfterBegin(tasks[0], tasks[1]));
        model.addConstraint(CSolver.endsAfterBegin(tasks[0], tasks[2]));
        model.addConstraint(CSolver.endsAfterBegin(tasks[0], tasks[3]));
        model.addConstraint(CSolver.endsAfterBegin(tasks[1], tasks[4]));
        model.addConstraint(CSolver.endsAfterBegin(tasks[2], tasks[5]));
        model.addConstraint(CSolver.endsAfterBegin(tasks[3], tasks[4]));
        model.addConstraint(CSolver.endsAfterBegin(tasks[4], tasks[6]));
        model.addConstraint(CSolver.endsAfterBegin(tasks[5], tasks[6]));

        model.addConstraint(CSolver.cumulative("cumu", tasks, resources, null, CSolver.constant(0), CSolver.constant(7), (IntegerVariable) null, ""));
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
    }

    @Override
    public void solve() {
        solver.minimize(solver.getVar(tasks[6].end()), true);
    }

    @Override
    public void prettyOut() {
        Logger.info("\n");

        for (int i = 0; i < tasks.length - 1; i++) {
            StringBuilder st = new StringBuilder(tasks[i].getName()).append(" [");
            int k = solver.getVar(tasks[i].start()).getVal();
            int d = solver.getVar(durations[i]).getVal();
            for (int j = 0; j < 13; j++) {
                if (j >= k && j < k + d) {
                    st.append(resources[i]);
                } else {
                    st.append("-");
                }
            }
            st.append("]");
            Logger.info(st.toString());
        }
        StringBuilder st = new StringBuilder(tasks[6].getName()).append(" [");
        int k = solver.getVar(tasks[6].start()).getVal();
        for (int j = 0; j < 13; j++) {
            if (j == k) {
                st.append("|");
            } else {
                st.append("-");
            }
        }
        st.append("]"+k);

        Logger.info(st.toString());
    }

    public static void main(String[] args) {
        new ProjectScheduling().execute();
    }
}
