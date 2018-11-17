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

package csolver.model.variables.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.AbstractSConstraint;
import csolver.kernel.solver.constraints.SConstraintType;
import csolver.kernel.solver.propagation.event.TaskVarEvent;
import csolver.kernel.solver.propagation.listener.TaskPropagator;
import csolver.kernel.solver.variables.scheduling.TaskVar;

public class TaskVariableTest {

    @Test
    public void test0(){
        Model m = new CPModel();
        TaskVariable t = CSolver.makeTaskVar("task1", 10, 5);
        m.addVariable(t);

        Solver solver = new CPSolver();
        solver.read(m);
        assertNotNull("t is null", solver.getVar(t));
        assertTrue("t not well created", TaskVar.class.isInstance(solver.getVar(t)));
    }


    @Test
    public void testTaskVarEvent() throws ContradictionException {
        Model m = new CPModel();
        int[] duration = new int[]{5,5};
        TaskVariable[] tasks = CSolver.makeTaskVarArray("task", 1, 10, duration);
        m.addVariables(tasks);

        Solver solver = new CPSolver();
        solver.read(m);

        FakeTaskConstraint ftc1 = new FakeTaskConstraint(solver.getVar(tasks), 0);
        FakeTaskConstraint ftc2 = new FakeTaskConstraint(solver.getVar(tasks), 0);

        solver.post(ftc1);
        solver.post(ftc2);

        solver.propagate();
        assertEquals(1,ftc1.getVal());
        assertEquals(1,ftc2.getVal());
        ftc1.forceAwake(0);
        solver.propagate();
        assertEquals(1,ftc1.getVal());
        assertEquals(2,ftc2.getVal());
    }

    private class FakeTaskConstraint extends AbstractSConstraint<TaskVar> implements TaskPropagator{

        int value;

        /**
         * Constraucts a constraint with the priority 0.
         *
         * @param vars variables involved in the constraint
         * @param init
         */
        protected FakeTaskConstraint(TaskVar[] vars, int init) {
            super(vars);
            this.value = init;
        }

        @Override
        public int getFilteredEventMask(int idx) {
            return TaskVarEvent.HYPDOMMODbitvector;
        }

        @Override
        public SConstraintType getConstraintType() {
            return null;
        }

        /**
         * <i>Propagation:</i>
         * Propagating the constraint until local consistency is reached.
         *
         * @throws csolver.kernel.solver.ContradictionException
         *          contradiction exception
         */
        @Override
        public void propagate() throws ContradictionException {
            value++;
        }

        /**
         * tests if the constraint is consistent with respect to the current state of domains
         *
         * @return wether the constraint is consistent
         */
        @Override
        public boolean isConsistent() {
            return true;
        }

        /**
         * <i>Semantic:</i>
         * Testing if the constraint is satisfied.
         * Note that all variables involved in the constraint must be
         * instantiated when this method is called.
         *
         * @return true if the constraint is satisfied
         */
        @Override
        public boolean isSatisfied() {
            return true;
        }

        public void forceAwake(int idx){
            vars[idx].updateHypotheticalDomain(cIndices[idx], this, false);
        }

        public final int getVal(){
            return value;
        }

        /**
         * Default propagation on improved hypothetical domain: propagation on domain revision.
         */
        @Override
        public void awakeOnHypDomMod(int varIdx) throws ContradictionException {
            value++;
        }

        
    }

}