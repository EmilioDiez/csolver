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

import java.util.List;
import java.util.Vector;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.global.geost.Constants;
import csolver.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import csolver.cp.solver.search.integer.valselector.MinVal;
import csolver.cp.solver.search.integer.varselector.StaticVarOrder;
import csolver.kernel.CSolver;
import csolver.kernel.model.constraints.geost.externalConstraints.IExternalConstraint;
import csolver.kernel.model.constraints.geost.externalConstraints.NonOverlappingModel;
import csolver.kernel.model.variables.geost.GeostObject;
import csolver.kernel.model.variables.geost.ShiftedBox;
import csolver.kernel.model.variables.integer.IntegerVariable;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/01/11
 */
public class MachineSchedulingLabelQuotient extends PatternExample {

    int[] durations;
    IntegerVariable[] starts;
    IntegerVariable[] q, r;
    IntegerVariable[] machines;


    @Override
    public void printDescription() {
        super.printDescription();
        Logger.info("Given 7 tasks. Each task has to run on a given machine choosen from a set of machines.");
        Logger.info("Each machine can only run one task at a time and each task should not be interrupted.");
        Logger.info("Create a specialized labeling which, for each task, creates a compulsory part of size 1.");
        Logger.info("(intersection  of the task positioned at its earliest start and of the task positioned at its latest start)");
        Logger.info("Find the earliest end.");
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

        durations = new int[]{2, 1, 4, 2, 3, 1};
        int[][] machine_set = {{1, 2}, {1, 3}, {1, 3}, {2, 3}, {1}, {1, 3}};

        starts = CSolver.makeIntVarArray("start", 7, 0, 13);
        machines = CSolver.makeIntVarArray("machine", 7, 1, 3);
        q = new IntegerVariable[6];
        r = new IntegerVariable[6];
        for (int i = 0; i < 6; i++) {
            q[i] = CSolver.makeIntVar("q_" + i, 0, (13 / durations[i]));
            r[i] = CSolver.makeIntVar("r_" + i, 0, durations[i] - 1);
            model.addConstraint(CSolver.eq(starts[i], CSolver.plus(CSolver.mult(durations[i], q[i]), r[i])));
        }


        model.addConstraint(CSolver.leq(CSolver.plus(starts[0], durations[0]), starts[1]));
        model.addConstraint(CSolver.leq(CSolver.plus(starts[0], durations[0]), starts[2]));
        model.addConstraint(CSolver.leq(CSolver.plus(starts[0], durations[0]), starts[3]));
        model.addConstraint(CSolver.leq(CSolver.plus(starts[1], durations[1]), starts[4]));
        model.addConstraint(CSolver.leq(CSolver.plus(starts[2], durations[2]), starts[5]));
        model.addConstraint(CSolver.leq(CSolver.plus(starts[3], durations[3]), starts[4]));
        model.addConstraint(CSolver.leq(CSolver.plus(starts[4], durations[4]), starts[6]));
        model.addConstraint(CSolver.leq(CSolver.plus(starts[5], durations[5]), starts[6]));


        // GEOST
        int dim = 2; // D0: time, D1: machine
        int height = 1;
        int nbOfObj = 6;

        //Create the Objects and the ShiftedBoxes and add them to corresponding shapes
        List<GeostObject> obj = new Vector<GeostObject>();
        List<ShiftedBox> sb = new Vector<ShiftedBox>();
        IntegerVariable ONE = CSolver.constant(1);
        int[] t = {0, 0};
        for (int i = 0; i < nbOfObj; i++) {
            // first create the shape
            int[] l = {durations[i], height};
            sb.add(new ShiftedBox(i, t, l));

            // then create the object corresponding to the shape
            IntegerVariable shapeId = CSolver.makeIntVar("sid", i, i);
            IntegerVariable coords[] = new IntegerVariable[dim];
            coords[0] = starts[i];
            coords[1] = machines[i];

            obj.add(new GeostObject(dim, i, shapeId, coords, ONE, ONE, ONE));

            model.addConstraint(CSolver.member(machines[i], machine_set[i]));
        }

        //Create the external constraints vector
        Vector<IExternalConstraint> ectr = new Vector<IExternalConstraint>(); //create the list of dimensions for the external constraint
        int[] ectrDim = new int[dim];
        for (int d = 0; d < dim; d++) {
            ectrDim[d] = d; //create the list of object ids for the external constraint
        }
        int[] objOfEctr = new int[nbOfObj];
        for (int d = 0; d < nbOfObj; d++) {
            objOfEctr[d] = obj.get(d).getObjectId();
        }

        //create and add one external constraint of type non overlapping
        NonOverlappingModel n = new NonOverlappingModel(Constants.NON_OVERLAPPING, ectrDim, objOfEctr);
        ectr.add(n);

        //create and post the geost constraint
        model.addConstraint(CSolver.geost(dim, obj, sb, ectr));

    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.addGoal(new AssignOrForbidIntVarVal(new StaticVarOrder(solver, solver.getVar(q)), new MinVal()));
        solver.addGoal(new AssignOrForbidIntVarVal(new StaticVarOrder(solver, solver.getVar(starts)), new MinVal()));
        Logger.info(solver.pretty());
    }

    @Override
    public void solve() {
        solver.minimize(solver.getVar(starts[6]), true);
    }

    @Override
    public void prettyOut() {
        Logger.info("\n");
        for (int i = 0; i < starts.length - 1; i++) {
            StringBuilder st = new StringBuilder("Task ").append(i + 1).append(" [");
            int k = solver.getVar(starts[i]).getVal();
            int d = durations[i];
            for (int j = 0; j < 13; j++) {
                if (j >= k && j < k + d) {
                    st.append(solver.getVar(machines[i]).getVal());
                } else {
                    st.append("-");
                }
            }
            st.append("]");
            Logger.info(st.toString());
        }
        StringBuilder st = new StringBuilder("Task ").append(7).append(" [");
        int k = solver.getVar(starts[6]).getVal();
        for (int j = 0; j < 13; j++) {
            if (j == k) {
                st.append("|");
            } else {
                st.append("-");
            }
        }
        st.append("]");
        Logger.info(st.toString());
    }

    public static void main(String[] args) {
        new MachineSchedulingLabelQuotient().execute();
    }
}
