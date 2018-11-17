package csolver.model.constraints.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.set.RandomSetValSelector;
import csolver.cp.solver.search.set.RandomSetVarSelector;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.cnf.Literal;
import csolver.kernel.model.constraints.cnf.Node;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;

public class InverseSetTests {

    @Test
    public void test01() {
        Model model = new CPModel();

        SetVariable[] x = CSolver.makeSetVarArray("x", 2, 0, 2);
        SetVariable[] y = CSolver.makeSetVarArray("y", 2, 0, 1, Options.V_NO_DECISION);

        model.addConstraint(CSolver.inverseSet(x, y));

        Solver solver = new CPSolver();
        solver.read(model);
//        ChocoLogging.toSolution();
        solver.solveAll();
//        ChocoLogging.flushLogs();

        assertEquals(16, solver.getNbSolutions());

    }

    private int[] buildValues(Random r, int low, int up) {
        int nb = 2 + r.nextInt(up - low + 1);
        HashSet<Integer> set = new HashSet<Integer>(nb);
        for (int i = 0; i < nb; i++) {
            set.add(low + r.nextInt(up - low + 1));
        }
        int[] values = ArrayUtils.convertIntegers(set); //set.toArray();
        Arrays.sort(values);
        return values;
    }

    private void postInverse(CPModel model, SetVariable[] x, SetVariable[] y, int min, int max) {

        for (int valx = min; valx <= max; valx++) {
            for (int i = 0; i < x.length; i++) {
                SetVariable variable = x[i];
                if (valx < y.length) {
                    IntegerVariable[] bs = CSolver.makeBooleanVarArray("bs", 2);
                    model.addConstraint(CSolver.reifiedConstraint(bs[0], CSolver.member(valx, variable)));
                    model.addConstraint(CSolver.reifiedConstraint(bs[1], CSolver.member(i, y[valx])));
                    model.addConstraints(CSolver.clauses(Node.ifOnlyIf(Literal.pos(bs[0]), Literal.pos(bs[1]))));
                } else {
                    model.addConstraint(CSolver.notMember(valx, variable));
                }
            }
        }

        for (int valy = 0; valy <= x.length + 3; valy++) {
            for (int i = 0; i < y.length; i++) {
                SetVariable variable = y[i];
                if (valy < x.length) {
                    IntegerVariable[] bs = CSolver.makeBooleanVarArray("bs", 2);
                    model.addConstraint(CSolver.reifiedConstraint(bs[0], CSolver.member(valy, variable)));
                    model.addConstraint(CSolver.reifiedConstraint(bs[1], CSolver.member(i, x[valy])));
                    model.addConstraints(CSolver.clauses(Node.ifOnlyIf(Literal.pos(bs[0]), Literal.pos(bs[1]))));
                } else {
                    model.addConstraint(CSolver.notMember(valy, variable));
                }
            }

        }

    }


    @Test
    public void testRandom01() {
        for (int test = 0; test < 5; test++) {
            Random rgen = new Random(test * 123456);
            int size = Math.max(5 + test, 8);
            int min = rgen.nextInt(5);
            int max = min + 1 + rgen.nextInt(10);
            SetVariable[] x = new SetVariable[size];
            SetVariable[] y = new SetVariable[max]; // on purpose too little variables ;)
            for (int i = 0; i < x.length; i++) {
                x[i] = CSolver.makeSetVar("x" + i, buildValues(rgen, min, max));
            }

            for (int i = 0; i < y.length; i++) {
                y[i] = CSolver.makeSetVar("y" + i, buildValues(rgen, 0, size + 3)); // on purpose too large domains
            }

            CPModel[] models = new CPModel[2];
            for (int i = 0; i < models.length; i++) {
                models[i] = new CPModel();
            }

            models[0].addConstraint(CSolver.inverseSet(x, y));

            postInverse(models[1], x, y, min, max);


            Solver[] solvers = new CPSolver[2];
            for (int i = 0; i < solvers.length; i++) {
                solvers[i] = new CPSolver();
                Solver solver = solvers[i];
                solver.read(models[i]);
                solver.setVarSetSelector(new RandomSetVarSelector(solver, i));
                solver.setValSetSelector(new RandomSetValSelector(i));

                solver.solveAll();
            }

            assertEquals(solvers[0].getNbSolutions(), solvers[1].getNbSolutions());
        }

    }

    @Test
    public void testRandom02() {
        for (int test = 0; test < 5; test++) {
            Random rgen = new Random(test * 123456);
            int size = Math.max(5 + test, 8);
            int min = rgen.nextInt(5);
            int max = min + 1 + rgen.nextInt(10);
            SetVariable[] x = new SetVariable[size];
            SetVariable[] y = new SetVariable[max]; // on purpose too little variables ;)
            for (int i = 0; i < x.length; i++) {
                x[i] = CSolver.makeSetVar("x" + i, buildValues(rgen, min, max));
            }

            for (int i = 0; i < y.length; i++) {
                y[i] = CSolver.makeSetVar("y" + i, buildValues(rgen, 0, size + 3), Options.V_NO_DECISION); // on purpose too large domains
            }

            Model model = new CPModel();

            model.addConstraint(CSolver.inverseSet(x, y));


            Solver solver = new CPSolver();

            solver.read(model);
            solver.setVarSetSelector(new RandomSetVarSelector(solver, test));
            solver.setValSetSelector(new RandomSetValSelector(test));


            solver.solve();
            do {
                for (SetVariable var : y) {
                    assertTrue(solver.getVar(var).isInstantiated());
                }
            } while (solver.nextSolution());
        }
    }

    @Test
    public void testRandom03() {
        for (int test = 0; test < 5; test++) {
            Random rgen = new Random(test * 123456);
            int size = Math.max(5 + test, 10);
            int min = rgen.nextInt(5);
            int max = min + 1 + rgen.nextInt(10);
            SetVariable[] x = new SetVariable[size];
            SetVariable[] y = new SetVariable[max]; // on purpose too little variables ;)
            for (int i = 0; i < x.length; i++) {
                x[i] = CSolver.makeSetVar("x" + i, buildValues(rgen, min, max));
            }

            for (int i = 0; i < y.length; i++) {
                y[i] = CSolver.makeSetVar("y" + i, buildValues(rgen, 0, size + 3), Options.V_NO_DECISION); // on purpose too large domains
            }

            Model model = new CPModel();

            model.addConstraint(CSolver.inverseSet(x, y));


            Solver solver = new CPSolver();

            solver.read(model);
            solver.setVarSetSelector(new RandomSetVarSelector(solver, test));
            solver.setValSetSelector(new RandomSetValSelector(test));

            int idx = rgen.nextInt(x.length);
            try {
                solver.getVar(x[idx]).addToKernel(solver.getVar(x[idx]).getEnveloppeInf(), null, false);
                idx = rgen.nextInt(y.length);
                solver.getVar(y[idx]).addToKernel(solver.getVar(y[idx]).getEnveloppeInf(), null, false);


                if (solver.solve()) {
                    do {
                        for (SetVariable var : y) {
                            assertTrue(solver.getVar(var).isInstantiated());
                        }
                    } while (solver.nextSolution());
                }
            } catch (ContradictionException e) {
                assertTrue(true);
            }


        }
    }


}
