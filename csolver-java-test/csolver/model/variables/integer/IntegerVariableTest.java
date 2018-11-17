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
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.inverseChanneling;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import csolver.cp.common.util.iterators.IntervalIntDomainIterator;
import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.integer.TimesXYZ;
import csolver.cp.solver.constraints.integer.bool.sat.ClauseStore;
import csolver.cp.solver.search.BranchingFactory;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.cp.solver.variables.integer.IntDomainVarImpl;
import csolver.cp.solver.variables.integer.IntervalIntDomain;
import csolver.kernel.Options;
import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.common.util.iterators.OneValueIterator;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import nitoku.log.Logger;

public class IntegerVariableTest {
    

    private static HashMap<Integer, String> typeList = new HashMap();

    static {
        typeList.put(0, "bound");
        typeList.put(1, "enum");
        typeList.put(2, "binTree");
        typeList.put(3, "linkedList");
    }

    @Test
    @Ignore
    public void nqueen() {
        Logger.info("===================NQUEEN=====================");
        //nbSol
        int[] sol = new int[13];
        sol[2] = 0;
        sol[4] = 2;
        sol[6] = 4;
        sol[8] = 92;
        sol[10] = 724;


        for (int n = 2; n < 11; n += 2) {
            Logger.info("=================== n = " + n + " =====================");
            for (int type = 0; type < 4; type++) {
                Model m = new CPModel();
                IntegerVariable[] queens = new IntegerVariable[n];
                IntegerVariable[] queensdual = new IntegerVariable[n];
                for (int i = 0; i < n; i++) {
                    queens[i] = createVar(type, "Q" + i, 1, n, m);
                    queensdual[i] = createVar(type, "QD" + i, 1, n, m);
                }
                nQueensNaifRed(m, type, n, queens, queensdual, sol[n]);
            }
        }
    }

    @Test
    @Ignore
    public void alldifferent() {
        Logger.info("===================ALLDIFFERENT=====================");
        //nbSol
        int[] sol = new int[]{0, 1, 2, 6, 24, 120, 720, 5040, 40320};
        for (int n = 1; n < 9; n += 1) {
            Logger.info("=================== n = " + n + " =====================");
            // BEAWRE: we do not test the linked list domain for this problem, too much time consumer
            for (int type = 0; type < 3; type++) {
                Model m = new CPModel();
                IntegerVariable[] vars = new IntegerVariable[n];
                for (int i = 0; i < n; i++) {
                    vars[i] = createVar(type, "v" + i, 1, n, m);
                }
                pbAllDifferent(m, type, sol[n], vars);
            }
        }
    }

    @Test
    @Ignore
    public void boundalldifferent() {
        Logger.info("===================BOUNDALLDIFFERENT=====================");

        for (int n = 1000; n < 2001; n += 1000) {
            Logger.info("=================== n = " + n + " =====================");
            // BEAWRE: we do not test the linked list domain for this problem, too much time consumer
            for (int type = 0; type < 3; type++) {
                Model m = new CPModel();
                IntegerVariable[] vars = new IntegerVariable[n];
                for (int i = 0; i < n; i++) {
                    vars[i] = createVar(type, "v" + i, 1, n, m);
                }
                pbBoundAllDifferent(type, vars);
            }
        }
    }


//
//    @Test
//    public void magicsquare(){
//
//    }
//
//    @Test
//    public void donaldgeraldrobert(){
//
//    }


    private IntegerVariable createVar(int type, String name, int bi, int bs, Model m) {
        IntegerVariable iv = makeIntVar(name, bi, bs);
        switch (type) {
            case 0:
                m.addVariable(Options.V_BOUND, iv);
                break;
            case 1:
                m.addVariable(Options.V_ENUM, iv);
                break;
            case 2:
                m.addVariable(Options.V_BTREE, iv);
                break;
            case 3:
                m.addVariable(Options.V_LINK, iv);
                break;
            default:
                break;
        }
        return iv;
    }


    /**
     * ********PROBLEMES****************
     */


    public static void nQueensNaifRed(Model m, int type, int n, IntegerVariable[] queens, IntegerVariable[] queensdual, int nbSol) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
                m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queensdual[i], queensdual[j]));
                m.addConstraint(neq(queensdual[i], plus(queensdual[j], k)));  // diagonal constraints
                m.addConstraint(neq(queensdual[i], minus(queensdual[j], k))); // diagonal constraints
            }
        }
        m.addConstraint(inverseChanneling(queens, queensdual));
        Solver s = new CPSolver();
        s.read(m);
        s.setVarIntSelector(new MinDomain(s, s.getVar(queens)));
        //CPSolver.setVerbosity(CPSolver.SOLUTION);
        int timeLimit = 10000;
        //s.setTimeLimit(timeLimit);
        s.solveAll();
        Logger.info("-------------\n" + typeList.get(type) + ":");
        s.printRuntimeStatistics();
        assertEquals("nbSol incorrect", nbSol, s.getNbSolutions());
    }


    public static void pbAllDifferent(Model m, int type, int nbSol, IntegerVariable... vars) {
        Constraint c = allDifferent(vars);
        m.addConstraint(c);

        Solver s = new CPSolver();
        s.read(m);

        //int timeLimit = 10000;
        //s.setTimeLimit(timeLimit);
        s.solveAll();
        Logger.info("-------------\n" + typeList.get(type) + ":");
        s.printRuntimeStatistics();
        assertEquals("nbSol incorrect", nbSol, s.getNbSolutions());

    }

    public static void pbBoundAllDifferent(int type, IntegerVariable... vars) {
        Model m = new CPModel();
        Constraint c = allDifferent(vars);
        m.addConstraint(Options.C_ALLDIFFERENT_CLIQUE, c);

        Solver s = new CPSolver();
        s.read(m);

//        int timeLimit = 10000;
//        s.setTimeLimit(timeLimit);
        s.solve();
        Logger.info("-------------\n" + typeList.get(type) + ":");
        s.printRuntimeStatistics();

    }



    @Test
    public void testOnUndefinedVariable() {
        Model m = new CPModel();
        IntegerVariable v = makeIntVar("v", Integer.MIN_VALUE, Integer.MAX_VALUE);
        IntegerVariable w = makeIntVar("w");
        m.addVariables(v, w);
        Solver s = new CPSolver();
        try {
            s.read(m);
        } catch (OutOfMemoryError e) {
            fail("OutOfMemoryError...");
        }
        assertEquals("w wrong type", IntervalIntDomain.class, s.getVar(w).getDomain().getClass());

    }

    @Test
    public void testAddCste1() {
        CPSolver solver = new CPSolver();
        IntDomainVar x = solver.createEnumIntVar("X", 1, 3);
        IntDomainVar y = solver.createIntVarAddCste("y", x, 2);

        solver.post(solver.neq(y, 4));
        solver.solveAll();
        assertEquals(2, solver.getSolutionCount());


    }

    @Test
    public void testAddCste2() {
        CPSolver solver = new CPSolver();
        IntDomainVar x = solver.createEnumIntVar("X", 1, 4);
        IntDomainVar y = solver.createIntVarAddCste("y", x, 3);

        solver.post(solver.neq(y, 2));
        solver.solveAll();
        assertEquals(4, solver.getSolutionCount());

    }

    private Solver bijectiveAdd(int low, int upp, int coeff) {
        CPSolver solver = new CPSolver();
        IntDomainVar x = solver.createEnumIntVar("X", low, upp);
        IntDomainVar y = solver.createIntVarAddCste("y", x, coeff);

        solver.post(solver.geq(y, low + coeff - 1));
        solver.post(solver.leq(y, upp - coeff - 1));

        return solver;
    }

    private Solver constraintAdd(int low, int upp, int coeff) {
        CPSolver solver = new CPSolver();
        IntDomainVar x = solver.createEnumIntVar("X", low, upp);
        IntDomainVar y = solver.createEnumIntVar("y", low + coeff, upp + coeff);

        solver.post(solver.geq(y, low + coeff - 1));
        solver.post(solver.leq(y, upp - coeff - 1));
        solver.post(solver.eq(y, solver.plus(x, coeff)));

        return solver;
    }

    @Test
    public void testRandom1() {
        Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            rand.setSeed(i);
            int low = rand.nextInt(10);
            int upp = low + rand.nextInt(1000);
            int coeff = rand.nextInt(50);

            Solver sb = bijectiveAdd(low, upp, coeff);
            Solver sc = constraintAdd(low, upp, coeff);
            sb.solveAll();
            sc.solveAll();
            assertEquals(sc.getSolutionCount(), sb.getSolutionCount());
            assertEquals(sc.getNodeCount(), sb.getNodeCount());

        }
    }

    @Test
    public void testRandom2() {
        Solver sb = bijectiveAdd(1, 9999, 3);
        Solver sc = constraintAdd(1, 9999, 3);
//        ChocoLogging.toVerbose();
        sb.solveAll();
        sc.solveAll();
        assertEquals(sc.getSolutionCount(), sb.getSolutionCount());
        assertEquals(sc.getNodeCount(), sb.getNodeCount());

    }

    @Test
    public void testRandom3() {
        int N = 4999;
        for (int i = 1; i < 2; i++) {
            Solver sb = bijectiveAdd(1, N, 3);
            Solver sc = constraintAdd(1, N, 3);
            sb.solveAll();
            sc.solveAll();
            System.out.printf("%dms vs %dms\n", sb.getTimeCount(), sc.getTimeCount());
            assertEquals(sc.getSolutionCount(), sb.getSolutionCount());
            assertEquals(sc.getNodeCount(), sb.getNodeCount());
        }

    }

    private Solver bijectiveTime(int low, int upp, int coeff) {
        CPSolver solver = new CPSolver();
        IntDomainVar x = solver.createEnumIntVar("X", low, upp);
        IntDomainVar y = solver.createIntVarAddCste("y", x, coeff);
        return solver;
    }

    private Solver constraintTime(int low, int upp, int coeff) {
        CPSolver solver = new CPSolver();
        IntDomainVar x = solver.createEnumIntVar("X", low, upp);
        IntDomainVar y = solver.createEnumIntVar("y", low * coeff, upp * coeff);
        IntDomainVar c = solver.createEnumIntVar("c", coeff, coeff);

        solver.post(new TimesXYZ(x, c, y));

        return solver;
    }

    @Test
    public void testRandom11() {
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            rand.setSeed(i);
            int low = rand.nextInt(10);
            int upp = low + rand.nextInt(200);
            int coeff = rand.nextInt(50);

            Solver sb = bijectiveTime(low, upp, coeff);
            Solver sc = constraintTime(low, upp, coeff);
            sb.solveAll();
            sc.solveAll();
            assertEquals(sc.getSolutionCount(), sb.getSolutionCount());
            assertEquals(sc.getNodeCount(), sb.getNodeCount());

        }
    }

    @Test
    public void testRandom22() {
        Solver sb = bijectiveTime(1, 9999, 3);
        Solver sc = constraintTime(1, 9999, 3);
//        ChocoLogging.toVerbose();
        sb.solveAll();
        sc.solveAll();
        assertEquals(sc.getSolutionCount(), sb.getSolutionCount());
        assertEquals(sc.getNodeCount(), sb.getNodeCount());

    }

    @Test
    public void testRandom33() {
        int N = 4999;
        for (int i = 1; i < 2; i++) {
            Solver sb = bijectiveTime(1, N, 3);
            Solver sc = constraintTime(1, N, 3);
            sb.solveAll();
            sc.solveAll();
            System.out.printf("%dms vs %dms\n", sb.getTimeCount(), sc.getTimeCount());
            assertEquals(sc.getSolutionCount(), sb.getSolutionCount());
            assertEquals(sc.getNodeCount(), sb.getNodeCount());
        }

    }

    @Test
    public void testNotBoolVar() {
        CPSolver solver = new CPSolver();
        IntDomainVar move = solver.createBooleanVar("move");
        IntDomainVar stay = solver.createNotBooleanVar("stay", move);
        solver.solveAll();
        assertEquals(2, solver.getSolutionCount());
    }

    @Test
    public void testNotBoolVar2() {
        Random random = new Random();
        for (int seed = 0; seed < 20; seed++) {
            random.setSeed(seed);
            CPSolver solver1 = new CPSolver();
            int n = random.nextInt(20);
            IntDomainVar[] bvars = new IntDomainVar[n * 2];
            for (int i = 0; i < n; i++) {
                bvars[i] = solver1.createBooleanVar(String.format("b_%s", i));
            }
            for (int i = 0; i < n; i++) {
                bvars[i + n] = solver1.createNotBooleanVar(String.format("nb_%s", i), bvars[i]);
            }
            solver1.post(new ClauseStore(bvars, solver1.getEnvironment()));
            assertEquals(0, solver1.getSolutionCount());
        }
    }

    @Test
    public void iterationTest() {
        Random rand = new Random();
        Solver solver = new CPSolver();
        for (int seed = 3; seed < 500; seed++) {
            //System.out.printf("seed: %d\n", seed);
            rand.setSeed(seed);
            int type = rand.nextInt(4);
            int a = -200 + rand.nextInt(200);
            int b = a + rand.nextInt(200);
            IntDomainVar var = new IntDomainVarImpl(solver, "", type, a, b);
            DisposableIntIterator it = var.getDomain().getIterator();
            for (int i = var.getInf(); i <= var.getSup(); i = var.getNextDomainValue(i)) {
                assertTrue(it.hasNext());
                assertEquals(it.next(), i);
            }
            assertFalse(it.hasNext());
        }
    }

    //TAKE TOOO MUCH TIME (830.499s)
    //@Test
    @Ignore
    public void iterationTest2() {
        for (int i = 0; i < 2; i++) {
            Solver solver = new CPSolver();
            int n = 999999;
            IntDomainVar ivar = solver.createBoundIntVar("iv", -n, n);
            long t1 = -System.currentTimeMillis();
            for(int k = 0; k <999; k++){
                iterate1(ivar);
            }
            t1+= System.currentTimeMillis();

            long t2 = -System.currentTimeMillis();
            for(int k = 0; k <19999; k++){
                iterate2(ivar);
            }
            t2+= System.currentTimeMillis();

            long t3 = -System.currentTimeMillis();
            for(int k = 0; k <19999; k++){
                iterate3(ivar);
            }
            t3+= System.currentTimeMillis();

            System.out.printf("%dms, %dms, %dms\n", t1, t2, t3);
        }

    }

    //TAKE TOOO MUCH TIME (371sec)
    //@Test
    @Ignore
    public void iterationTest3() throws ContradictionException {
        for (int i = 0; i < 10; i++) {
            Solver solver = new CPSolver();
            int[] values = new int[99999];
            for(int j = 1; j < 99999; j++){
                values[j] = values[j-1] + 2;
            }
            IntDomainVar ivar = solver.createEnumIntVar("iv", values);
            ivar.remVal(0);
            long t1 = -System.currentTimeMillis();
            /*for(int k = 0; k <9999; k++){
                iterate1(ivar);
            }*/
            t1+= System.currentTimeMillis();

            long t2 = -System.currentTimeMillis();
            for(int k = 0; k <9999; k++){
                iterate2(ivar);
            }
            t2+= System.currentTimeMillis();

            long t3 = -System.currentTimeMillis();
            for(int k = 0; k <9999; k++){
                iterate3(ivar);
            }
            t3+= System.currentTimeMillis();

            System.out.printf("%dms, %dms, %dms\n", t1, t2, t3);
        }

    }

    //TAKE TOOO MUCH TIME (78sec)
    //@Test
    @Ignore
    public void iterationTest4() throws ContradictionException {
        for (int i = 64; i < 9999999; i*=2) {
            Solver solver = new CPSolver();
            IntDomainVar ivar1 = solver.createBoundIntVar("iv", 0, i);
            IntDomainVar ivar2 = solver.createEnumIntVar("iv", 0, i);
            long t2 = -System.currentTimeMillis();
            for(int k = 0; k <99; k++){
                iterate3(ivar1);
            }
            t2+= System.currentTimeMillis();

            long t3 = -System.currentTimeMillis();
            for(int k = 0; k <99; k++){
                iterate3(ivar2);
            }
            t3+= System.currentTimeMillis();

            System.out.printf("fn %d: %dms, %dms x%f\n", i, t2, t3, t3/(t2==0?.1:t2));

            t2 = -System.currentTimeMillis();
            for(int k = 0; k <99; k++){
                iterate2(ivar1);
            }
            t2+= System.currentTimeMillis();

            t3 = -System.currentTimeMillis();
            for(int k = 0; k <99; k++){
                iterate2(ivar2);
            }
            t3+= System.currentTimeMillis();

            System.out.printf("it %d: %dms, %dms x%f\n", i, t2, t3, t3/(t2==0?.1:t2));
        }

    }

    @Test
    public void iterationTest5() throws ContradictionException {
        for (int i = 0; i < 10; i++) {
            Solver solver = new CPSolver();
            IntDomainVar ivar = solver.createBoundIntVar("iv", 2, 2);
            OneValueIterator oit = OneValueIterator.getIterator(2);
            long t2 = -System.currentTimeMillis();
            int m = 0;
            for(int k = 0; k <9999999; k++){
                IntervalIntDomainIterator it= new IntervalIntDomainIterator();
                it.init((IntervalIntDomain) ivar.getDomain());
                while(it.hasNext()){
                    m += it.next();
                }
                it.dispose();
            }
            t2+= System.currentTimeMillis();

            long t3 = -System.currentTimeMillis();
            m = 0;
            for(int k = 0; k <9999999; k++){
                oit.init(2);
                while(oit.hasNext()){
                    m += oit.next();
                }
                oit.dispose();
            }
            t3+= System.currentTimeMillis();

            System.out.printf("%dms, %dms\n", t2, t3);
        }

    }

    private int iterate1(IntDomainVar var) {
        int ub = var.getSup(), k = 0;
        for (int val = var.getInf(); val <= ub; val = var.getNextDomainValue(val)) {
            k += val;
        }
        return k;
    }

    private int iterate2(IntDomainVar var) {
        int k = 0;
        DisposableIntIterator it = var.getDomain().getIterator();
        while(it.hasNext()){
            k+= it.next();
        }
        it.dispose();
        return k;
    }

    private int iterate3(IntDomainVar var) {
        int ub = var.getSup(), k = 0;
        for (int val = var.getInf(); val <= ub; val = var.fastNextDomainValue(val)) {
            k += val;
        }
        return k;
    }

    @Test
    public void testDummy(){
        Model model = new CPModel();
        int n = 999;
        IntegerVariable x = makeIntVar("x", 0, n, Options.V_BOUND);
        IntegerVariable y = makeIntVar("y", 0, n, Options.V_BOUND);
        IntegerVariable z = makeIntVar("z", 0, n, Options.V_BOUND);

        model.addConstraint(eq(x, z));
        model.addConstraint(eq(y, z));
        model.addConstraint(neq(x, y));

        Solver solver = new CPSolver();
        solver.read(model);
        solver.addGoal(BranchingFactory.lexicographic(solver, solver.getVar(new IntegerVariable[]{x,y,z})));
        solver.solve();


    }

}
