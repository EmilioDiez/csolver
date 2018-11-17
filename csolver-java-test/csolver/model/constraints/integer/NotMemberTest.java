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

import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.makeBooleanVar;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.notMember;
import static csolver.kernel.CSolver.reifiedConstraint;
import static csolver.kernel.CSolver.sum;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.valselector.RandomIntValSelector;
import csolver.cp.solver.search.integer.varselector.RandomIntVarSelector;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;

public class NotMemberTest {


    private static int[] buildValues(Random r, int low, int up){
        int nb = r.nextInt(up-low+1);
        HashSet<Integer> set = new HashSet<Integer>(nb);
        for(int i = 0 ; i < nb; i++){
            set.add(low + r.nextInt(up - low + 1));
        }
        int[] values = ArrayUtils.convertIntegers(set); //set.toArray();
        Arrays.sort(values);
        return values;
    }

    @Test
    public void test1(){
        Random r;
//        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        for(int i = 0; i < 200; i++){
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;
            int n = up - low + 1;
            int[] values = buildValues(r, low, up);

            IntegerVariable v = makeIntVar("v", low, up);

            Model m1 = new CPModel();
            Constraint disjoint = notMember(v, values);

            m1.addConstraint(disjoint);

            Solver s = new CPSolver();
            s.read(m1);

            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i));

            s.solveAll();
            int nbSol = s.getSolutionCount();
            assertEquals("seed:"+i, n - values.length, nbSol);
            if(nbSol == 0){
                assertEquals("seed:"+i, 0, s.getNodeCount());
            }else if(nbSol == 1){
                assertEquals("seed:"+i, 1, s.getNodeCount());
            }else{
                assertEquals("seed:"+i, nbSol + 1, s.getNodeCount());
            }
        }
    }

    @Test
    public void test2(){
        Random r;
        for(int i = 0; i < 200; i++){
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;
            int n = up - low + 1;
            int[] values = buildValues(r, low, up);

            IntegerVariable v = makeIntVar("v", low, up);

            Model m1 = new CPModel();
            Constraint disjoint = notMember(v, values);

            m1.addConstraint(disjoint);

            Solver s = new CPSolver();
            s.read(m1);
            try {
                s.propagate();
                assertEquals("seed:"+i, n - values.length, s.getVar(v).getDomainSize());
            } catch (ContradictionException e) {
                assertEquals("seed:"+i, values.length, s.getVar(v).getDomainSize());
            }
        }
    }

    @Test
    public void test3(){
        Random r;
        for(int i = 0; i < 200; i++){
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;
            int[] values = buildValues(r, low, up);

            IntegerVariable v = makeIntVar("v", low, up);

            Model m1 = new CPModel();
            Constraint disjoint = notMember(v, values);
            m1.addConstraint(disjoint);

            Model m2 = new CPModel();
            m2.addVariable(v);
            IntegerVariable[] bools = new IntegerVariable[values.length];
            for(int j = 0; j< values.length; j++){
                bools[j] = makeBooleanVar("b"+i);
                m2.addConstraint(reifiedConstraint(bools[j], neq(v, values[j])));
            }
            m2.addConstraint(eq(sum(bools), values.length));

            Solver s1 = new CPSolver();
            s1.read(m1);
            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));

            Solver s2 = new CPSolver();
            s2.read(m2);
            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solveAll();
            s2.solveAll();

            assertEquals("seed:"+i, s2.getSolutionCount(), s1.getSolutionCount());
        }
    }

    @Test
    public void test4() {
        Random r;
        for (int i = 0; i < 1000; i++) {
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;

            IntegerVariable v = makeIntVar("v", low, up);

            int lower, upper;
            lower = low + r.nextInt(up - low + 1);
            upper = lower + r.nextInt(up - low + 1);


            Model m1 = new CPModel();
            Constraint among = notMember(v, lower, upper);

            m1.addConstraint(among);

            int min = Math.max(low, lower);
            int max = Math.min(up, upper);
            int n = up - low + 1;

            Solver s = new CPSolver();
            s.read(m1);
            try {
                s.worldPush();
                s.propagate();
                assertEquals("seed:"+i, n - (max - min +1), s.getVar(v).getDomainSize());
            } catch (ContradictionException e) {
                s.worldPopUntil(0);
                assertEquals("seed:"+i, min, s.getVar(v).getInf());
                assertEquals("seed:"+i, max, s.getVar(v).getSup());
            }
        }
    }

    @Test
    public void test5() {
        Random r;
        for (int i = 0; i < 200; i++) {
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;

            IntegerVariable v = makeIntVar("v", low, up);

            int lower, upper;
            lower = low + r.nextInt(up - low + 1);
            upper = lower + r.nextInt(up - low + 1);


            Model m1 = new CPModel();
            Constraint among = notMember(v, lower, upper);

            m1.addConstraint(among);

            Model m2 = new CPModel();
            IntegerVariable[] bools = new IntegerVariable[upper - lower +1];
            for (int j = 0; j < bools.length; j++) {
                bools[j] = makeBooleanVar("b" + j);
                m2.addConstraint(reifiedConstraint(bools[j], neq(v, lower + j)));
            }
             m2.addConstraint(eq(sum(bools), upper - lower +1));


            Solver s1 = new CPSolver();
            s1.read(m1);
            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));

            Solver s2 = new CPSolver();
            s2.read(m2);
            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solveAll();
            s2.solveAll();

            assertEquals("seed:" + i, s2.getSolutionCount(), s1.getSolutionCount());
        }
    }

}