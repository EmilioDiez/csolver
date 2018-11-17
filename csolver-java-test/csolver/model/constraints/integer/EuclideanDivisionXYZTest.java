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

package csolver.model.constraints.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;

public class EuclideanDivisionXYZTest {

    @Test
    public void test1() {
        int[] size = new int[]{2, 3, 5, 10, 20};
        for (int siz = 0; siz < size.length; siz++) {
            int max = size[siz];

            for (int seed = 0; seed < 20; seed++) {
                Random r = new Random(seed);

                int x1 = r.nextInt(max) - r.nextInt(max);
                int y1 = x1 + r.nextInt(max);
                int x2 = r.nextInt(max) - r.nextInt(max);
                int y2 = x2 + r.nextInt(max);
                int x3 = r.nextInt(max) - r.nextInt(max);
                int y3 = x3 + r.nextInt(max);

                //REFERENCE

                Model _m = new CPModel();

                IntegerVariable _x = CSolver.makeIntVar("x", x1, y1, Options.V_BOUND);
                IntegerVariable _y = CSolver.makeIntVar("y", x2, y2, Options.V_BOUND);
                IntegerVariable _z = CSolver.makeIntVar("z", x3, y3, Options.V_BOUND);

                _m.addConstraint(CSolver.eq(_z, CSolver.div(_x, _y)));
                Solver _s = new CPSolver();
                _s.read(_m);
                _s.solveAll();

                //CONSTRAINT
                Model m = new CPModel();
                m.addConstraint(CSolver.intDiv(_x, _y, _z));
                Solver s = new CPSolver();
                s.read(m);
                s.solve();
                if (s.isFeasible()) {
                    do {
                        try{
                        assertEquals("("+max+"-"+seed+") Value test", s.getVar(_z).getVal(), s.getVar(_x).getVal() / s.getVar(_y).getVal());
                        }catch (ArithmeticException a){
                            fail("("+max+"-"+seed+") Division by 0");
                        }
                    } while (s.nextSolution());
                }
                assertEquals("("+max+"-"+seed+") Nb solution test",_s.getNbSolutions(), s.getNbSolutions());
            }
        }
    }

}
