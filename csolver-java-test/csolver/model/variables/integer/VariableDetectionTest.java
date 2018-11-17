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

package csolver.model.variables.integer;

import static csolver.kernel.CSolver.allDifferent;
import static csolver.kernel.CSolver.makeIntVarArray;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.variables.integer.IntervalIntDomain;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 8, 2008
 * Time: 4:42:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariableDetectionTest {

    @Test
    public void testAutoDetectionBoundVar() {
        Model m = new CPModel();
        IntegerVariable[] intvs = makeIntVarArray("v", 10, 0, 10);
        m.addConstraint(allDifferent(Options.C_ALLDIFFERENT_BC, intvs));
        CPSolver s = new CPSolver();
        s.read(m);
        for (int i = 0; i < intvs.length; i++) {
            assertTrue(s.getVar(intvs[i]).getDomain() instanceof IntervalIntDomain);
        }
    }

    @Test
    public void testRemoveVal(){
        IntegerVariable v = CSolver.makeIntVar("v", 1, 10);
        v.removeVal(0);
        assertTrue("v can not be equal to 10",v.canBeEqualTo(10));
        v.removeVal(11);
        assertTrue("v can not be equal to 10",v.canBeEqualTo(10));
        v.removeVal(5);
        assertFalse("v can be equal to 5",v.canBeEqualTo(5));
        v.removeVal(5);
        assertFalse("v can be equal to 5",v.canBeEqualTo(5));


    }

}
