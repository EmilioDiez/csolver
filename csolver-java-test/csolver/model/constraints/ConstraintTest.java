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

package csolver.model.constraints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.AbstractSConstraint;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.SConstraintType;
import csolver.kernel.solver.propagation.event.ConstraintEvent;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import nitoku.log.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 23 sept. 2008
 * Time: 14:56:33
 */
public class ConstraintTest   {
    

    @Test
    /**
     * John Horan bug on PartiallyStoredVector#staticRemove(int idx)
     */
    public void eraseConstraintTest() {
        Solver s = new CPSolver();
        IntDomainVar v = s.createEnumIntVar("v", 0, 3);
        SConstraint c1 = s.lt(v, 1);
        SConstraint c2 = s.lt(v, 2);
        SConstraint c3 = s.lt(v, 3);
        s.postCut(c1);
        s.postCut(c2);
        s.postCut(c3);
        s.eraseConstraint(c2);
        s.eraseConstraint(c3);
        try {
            Logger.info(s.pretty());
        } catch (Exception e) {
            fail();
        }
    }


    private static final class MockConstraint extends AbstractSConstraint {

        StringBuilder st;
        String name;

        public MockConstraint(String name, StringBuilder st, int priority) {
            super(priority, new IntDomainVar[0]);
            this.st = st;
            this.name = name;
        }

        @Override
        public void propagate() throws ContradictionException {
            st.append(name);
        }

        @Override
        public boolean isConsistent() {
            return false;
        }

        @Override
        public boolean isSatisfied() {
            return true;
        }

        @Override
        public SConstraintType getConstraintType() {
            return null;
        }
    }


    private static void orderTest(int order, String expected) {
        Configuration conf = new Configuration();
        conf.putInt(Configuration.CEQ_ORDER, order);

        Solver s = new CPSolver(conf);
        StringBuilder st = new StringBuilder();
        s.post(new MockConstraint("U", st, ConstraintEvent.UNARY));
        s.post(new MockConstraint("B", st, ConstraintEvent.BINARY));
        s.post(new MockConstraint("T", st, ConstraintEvent.TERNARY));
        s.post(new MockConstraint("L", st, ConstraintEvent.LINEAR));
        s.post(new MockConstraint("Q", st, ConstraintEvent.QUADRATIC));
        s.post(new MockConstraint("C", st, ConstraintEvent.CUBIC));
        s.post(new MockConstraint("S", st, ConstraintEvent.VERY_SLOW));

        try {
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        assertEquals(expected, st.toString());


    }

    @Test
    public void test1() {
        orderTest(1234567, "UBTLQCS");
        orderTest(7654321, "SCQLTBU");
        orderTest(3214765, "TBULSCQ");
        orderTest(1111777, "LTBUSCQ");
        orderTest(7777777, "SCQLTBU");
    }

    @Test
    public void stynesTest1() {
        Model m = new CPModel();
        IntegerVariable x = CSolver.makeIntVar("X", 0, 5);
        IntegerVariable y = CSolver.makeIntVar("Y", 0, 5);

        Constraint[] constraints = new Constraint[2];

        constraints[0] = CSolver.geq( x, 0);
        constraints[1] = CSolver.eq(x, 3);
        m.addConstraint( CSolver.ifOnlyIf( CSolver.eq(y, 1), CSolver.and(constraints) )  );
        // this modification must not be taken into account, but it take it into account after 
        // deleting the clone() approach on MetaConstrains 
        // constraints[1] = Choco.eq(x, 4);
        

        m.addConstraint(CSolver.eq(y,1));
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        assertEquals(s.getSolutionCount(), 1);
        assertEquals(s.getVar(x).getVal(), 3);

    }


}
