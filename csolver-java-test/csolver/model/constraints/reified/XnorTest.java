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
package csolver.model.constraints.reified;

import static csolver.kernel.CSolver.and;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeBooleanVar;
import static csolver.kernel.CSolver.makeBooleanVarArray;
import static csolver.kernel.CSolver.or;
import static csolver.kernel.CSolver.reifiedXnor;
import static csolver.kernel.CSolver.xnor;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.valselector.RandomIntValSelector;
import csolver.cp.solver.search.integer.varselector.RandomIntVarSelector;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;

public class XnorTest {


    @Test
    public void test1(){
        for(int i = 0; i < 50; i++){
            Model m1 = new CPModel();
            Solver s1 = new CPSolver();
            Model m2 = new CPModel();
            Solver s2 = new CPSolver();


            IntegerVariable[] bool = makeBooleanVarArray("b", 2);

            Constraint c = xnor(bool[0], bool[1]);

            m1.addConstraint(c);
            s1.read(m1);

            m2.addConstraint(or(and(eq(bool[0], 1), eq(bool[1], 1)), and(eq(bool[1], 0), eq(bool[0], 0))));
            s2.read(m2);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));


            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solve();
            do{
                assertEquals(s1.getVar(bool[0]).getVal(), s1.getVar(bool[1]).getVal());
            }while(s1.nextSolution());
            s2.solveAll();

            assertEquals("solutions", s2.getNbSolutions() , s1.getNbSolutions());
        }
    }

    @Test
    public void test2(){
        Random r;
        for(int i = 0; i < 50; i++){
            r = new Random(i);
            Model m1 = new CPModel();
            Model m2 = new CPModel();
            Solver s1 = new CPSolver();
            Solver s2 = new CPSolver();

            IntegerVariable bin = makeBooleanVar("bin");
            IntegerVariable[] bool = makeBooleanVarArray("b", 2);

            Constraint c = reifiedXnor(bin, bool[0], bool[1]);

            m1.addConstraint(c);
            m1.addConstraint(eq(bin,0));

            m2.addConstraint(c);
            m2.addConstraint(eq(bin,1));


            s1.read(m1);
            s2.read(m2);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));

            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solve();
            do{
                assertEquals(s1.getVar(bool[0]).getVal(), Math.abs(s1.getVar(bool[1]).getVal()-1));
            }while(s1.nextSolution());

            s2.solve();
            do{
                assertEquals(s2.getVar(bool[0]).getVal(), s2.getVar(bool[1]).getVal());
            }while(s2.nextSolution());

            assertEquals("solutions", 2 , s1.getNbSolutions());
            assertEquals("solutions", 2, s2.getNbSolutions());
        }
    }
}