package csolver.model.constraints.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.set.MinEnv;
import csolver.cp.solver.search.set.RandomSetValSelector;
import csolver.cp.solver.search.set.RandomSetVarSelector;
import csolver.cp.solver.search.set.StaticSetVarOrder;
import csolver.kernel.CSolver;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.constraints.cnf.ALogicTree;
import csolver.kernel.model.constraints.cnf.Literal;
import csolver.kernel.model.constraints.cnf.Node;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.Solver;

public class SetValuePrecedeTests {


    

    SetVariable[] _svariable;

    private int[] buildValues(Random r, int low, int up) {
        int nb = 1 + r.nextInt(up - low + 1);
        HashSet<Integer> set = new HashSet<Integer>(nb);
        for (int i = 0; i < nb; i++) {
            set.add(low + r.nextInt(up - low + 1));
        }
        int[] values = ArrayUtils.convertIntegers(set);//set.toArray();
        Arrays.sort(values);
        return values;
    }


    private CPModel[] model(Random r, int size) {
        int low = r.nextInt(size);
        int up = low + 1 + r.nextInt(2 * size);

        _svariable = new SetVariable[size];
        for (int i = 0; i < _svariable.length; i++) {
            _svariable[i] = CSolver.makeSetVar("s"+i, buildValues(r, low, up)) ;
        }

        SetVariable avar = _svariable[r.nextInt(size)];
        int s = avar.getValues()[r.nextInt(avar.getValues().length)];
        int t;
        do {
            SetVariable anothervar = _svariable[r.nextInt(size)];
            t = anothervar.getValues()[r.nextInt(anothervar.getValues().length)];
        }
        while (s == t);

        CPModel[] ms = new CPModel[2];
        for (int i = 0; i < ms.length; i++) {
            CPModel m = new CPModel();
            switch (i) {
                case 0:
                    m.addConstraint(CSolver.setValuePrecede(_svariable, s, t));
                    break;
                case 1:
                    IntegerVariable[] bs = CSolver.makeBooleanVarArray("bs", 2);
                    m.addConstraint(CSolver.reifiedConstraint(bs[0], CSolver.member(s, _svariable[0])));
                    m.addConstraint(CSolver.reifiedConstraint(bs[1], CSolver.notMember(t, _svariable[0])));
                    m.addConstraints(CSolver.clauses(Node.or(Literal.pos(bs))));

                    for (int j = 1; j < size; j++) {
                        IntegerVariable[] b = CSolver.makeBooleanVarArray("b", 2);
                        m.addConstraint(CSolver.reifiedConstraint(b[0], CSolver.member(s, _svariable[0])));
                        m.addConstraint(CSolver.reifiedConstraint(b[1], CSolver.notMember(t, _svariable[0])));
                        ALogicTree tree = Node.and(Literal.pos(b[0]), Literal.pos(b[1]));

                        for (int it = 1; it < j; it++) {
                            b = CSolver.makeBooleanVarArray("b", 2);
                            m.addConstraint(CSolver.reifiedConstraint(b[0], CSolver.member(s, _svariable[it])));
                            m.addConstraint(CSolver.reifiedConstraint(b[1], CSolver.notMember(t, _svariable[it])));
                            tree = Node.or(tree, Node.and(Literal.pos(b[0]), Literal.pos(b[1])));
                        }
                        b = CSolver.makeBooleanVarArray("b", 2);
                        m.addConstraint(CSolver.reifiedConstraint(b[0], CSolver.notMember(s, _svariable[j])));
                        m.addConstraint(CSolver.reifiedConstraint(b[1], CSolver.member(t, _svariable[j])));
                        m.addConstraints(CSolver.clauses(Node.implies(Node.and(Literal.pos(b[0]), Literal.pos(b[1])), tree)));
                    }
                    break;
            }
            ms[i] = m;
        }
        return ms;
    }

    private CPSolver solve(int seed, CPModel m, boolean sta_tic) {
        CPSolver s = new CPSolver();
        s.read(m);
        if (sta_tic) {
            s.setVarSetSelector(new StaticSetVarOrder(s, s.getVar(_svariable)));
            s.setValSetSelector(new MinEnv());
        } else {
            s.setVarSetSelector(new RandomSetVarSelector(s, seed));
            s.setValSetSelector(new RandomSetValSelector(seed));
        }
        s.solveAll();
        return s;
    }




    @Test
    public void testRandom01() {
        Random r;
        for (int i = 0; i < 5; i++) {

            r = new Random(i);
            CPModel[] ms = model(r, 4);
            CPSolver[] ss = new CPSolver[ms.length];
            for (int j = 0; j < ms.length; j++) {
                ss[j] = solve(i, ms[j], false);
            }
            for (int j = 1; j < ms.length; j++) {
                assertEquals("nb solutions, seed:" + i, ss[0].getSolutionCount(), ss[j].getSolutionCount());
            }
        }
    }


    @Test
    public void test01() {  // example in section 4.2

        Model model = new CPModel();

        SetVariable[] vars = CSolver.makeSetVarArray("sv", 5, 1, 3);

        model.addConstraint(CSolver.notMember(vars[0],1));
        model.addConstraint(CSolver.notMember(vars[1],3));
        model.addConstraint(CSolver.notMember(vars[3], 1));
        model.addConstraint(CSolver.notMember(vars[4], 3));

        model.addConstraint(CSolver.member(vars[1], 2));

        model.addConstraint(CSolver.setValuePrecede(vars, 1, 2));

        Solver solver = new CPSolver();
//        ChocoLogging.toVerbose();
        solver.read(model);
        Boolean res = solver.solve();
//        ChocoLogging.flushLogs();

        assertTrue(res);

    }

    @Test
    public void test02() {
        Model model = new CPModel();
        SetVariable[] setVariables = CSolver.makeSetVarArray("sv", 2, 1, 2);

        model.addConstraint(CSolver.setValuePrecede(setVariables, 1, 2));

        Solver solver = new CPSolver();
//        ChocoLogging.toSolution();
        solver.read(model);
        solver.solveAll();
//        ChocoLogging.flushLogs();

//        System.out.println("solver.getNbSolutions() = " + solver.getNbSolutions());

        assertEquals(10, solver.getNbSolutions());
        // compter les solutions
        // pos2 = 0 => pos1 = 0 et 2 accompagne 1 partout  sols = 3 (soit 1,2 pour s1 soit pas de 2)
        // pos2 = 1 => pas de 1 en 0 ni apr�s ou bien singleton 1 en 0 (car 1,2 dŽjˆ comptŽ) sols = 2 + 2
        // pas de 2 => sols = 3
    }



    @Test
    public void test03() {

        Model m = new CPModel();
        int s = 1;
        int t = 2;

        SetVariable[] _svariable = CSolver.makeSetVarArray("sv", 2, 1 ,2);

        IntegerVariable[] bs = CSolver.makeBooleanVarArray("bs", 2);
        m.addConstraint(CSolver.reifiedConstraint(bs[0], CSolver.member(s, _svariable[0])));
        m.addConstraint(CSolver.reifiedConstraint(bs[1], CSolver.notMember(t, _svariable[0])));
        m.addConstraints(CSolver.clauses(Node.or(Literal.pos(bs))));

        for (int j = 1; j < _svariable.length; j++) {
            IntegerVariable[] b = CSolver.makeBooleanVarArray("b", 2);
            m.addConstraint(CSolver.reifiedConstraint(b[0], CSolver.member(s, _svariable[0])));
            m.addConstraint(CSolver.reifiedConstraint(b[1], CSolver.notMember(t, _svariable[0])));
            ALogicTree tree = Node.and(Literal.pos(b[0]), Literal.pos(b[1]));

            for (int it = 1; it < j; it++) {
                b = CSolver.makeBooleanVarArray("b", 2);
                m.addConstraint(CSolver.reifiedConstraint(b[0], CSolver.member(s, _svariable[it])));
                m.addConstraint(CSolver.reifiedConstraint(b[1], CSolver.notMember(t, _svariable[it])));
                tree = Node.or(tree, Node.and(Literal.pos(b[0]), Literal.pos(b[1])));
            }
            b = CSolver.makeBooleanVarArray("b", 2);
            m.addConstraint(CSolver.reifiedConstraint(b[0], CSolver.notMember(s, _svariable[j])));
            m.addConstraint(CSolver.reifiedConstraint(b[1], CSolver.member(t, _svariable[j])));
            m.addConstraints(CSolver.clauses(Node.implies(Node.and(Literal.pos(b[0]), Literal.pos(b[1])), tree)));
        }

        Solver solver = new CPSolver();
//        ChocoLogging.toSolution();
        solver.read(m);
        solver.solveAll();
        assertEquals(10, solver.getNbSolutions());
    }


    @Test
    public void test04() {
        Model model = new CPModel();
        SetVariable[] setVariables = CSolver.makeSetVarArray("sv", 3, 0, 4);

        model.addConstraint(CSolver.setValuePrecede(setVariables, 2, 4));

        model.addConstraint(CSolver.member(setVariables[0], 0));
        model.addConstraint(CSolver.member(setVariables[0], 1));
        model.addConstraint(CSolver.notMember(setVariables[0], 4));

        model.addConstraint(CSolver.notMember(setVariables[1], 0));
        model.addConstraint(CSolver.notMember(setVariables[1], 1));


        model.addConstraint(CSolver.notMember(setVariables[2], 0));
        model.addConstraint(CSolver.notMember(setVariables[2], 1));
        model.addConstraint(CSolver.notMember(setVariables[2], 2));
        model.addConstraint(CSolver.notMember(setVariables[2], 3));



        Solver solver = new CPSolver();
//        ChocoLogging.toSolution();
        solver.read(model);
        solver.solveAll();
//        ChocoLogging.flushLogs();

        assertEquals(48, solver.getNbSolutions());
    }




}
