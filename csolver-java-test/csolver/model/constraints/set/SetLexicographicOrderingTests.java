package csolver.model.constraints.set;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.set.RandomSetValSelector;
import csolver.cp.solver.search.set.RandomSetVarSelector;
import csolver.kernel.CSolver;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.Solver;

public class SetLexicographicOrderingTests {


    private int[] buildValues(Random r, int low, int up) {
        int nb = 1 + r.nextInt(up - low + 1);
        HashSet<Integer> set = new HashSet<Integer>(nb);
        for (int i = 0; i < nb; i++) {
            set.add(low + r.nextInt(up - low + 1));
        }
        int[] values = ArrayUtils.convertIntegers(set); //set.toArray();
        Arrays.sort(values);
        return values;
    }




    @Test
    public  void test01() {
        Model m = new CPModel();

        SetVariable[] sv = CSolver.makeSetVarArray("s", 2, 1, 3);

        m.addVariables(sv);
        m.addConstraint(CSolver.setLex(sv[0], sv[1]));

        Solver s = new CPSolver();
//         ChocoLogging.toSolution();
        s.read(m);
        s.solveAll();
//         ChocoLogging.flushLogs();
        assertEquals(36, s.getNbSolutions());

    }

    @Test
    public void testRandom01() {
        SetVariable[] svars;
        Random r;
        for (int i = 0; i < 1; i++) {
//            System.out.print("starting test " + i);
            r = new Random(i);
            CPModel m = new CPModel();

            int low = r.nextInt(10);
            int up = low + r.nextInt(10);

            svars = new SetVariable[Math.min(2 + i, 7)];
            for (int idx = 0; idx < svars.length; idx++) {
                svars[idx] = CSolver.makeSetVar("s"+idx, buildValues(r, low, up)) ;
            }

            for (int s0 = 0; s0 < svars.length - 1; s0++) {
                SetVariable v0 = svars[s0];
                SetVariable v1 = svars[s0+1];
                m.addConstraint(CSolver.setLex(v0, v1));
            }

            CPSolver s = new CPSolver();
            s.read(m);
            s.setVarSetSelector(new RandomSetVarSelector(s, i));
            s.setValSetSelector(new RandomSetValSelector(i));

            s.solveAll();

            int nbsols = 0;
            // no easiest way than generate and test :(
            CPModel m2 = new CPModel();
            m2.addVariables(svars);

            CPSolver s2 = new CPSolver();
            // enumeration des solutions pour leur test subs�quent
            s2.read(m2);

            s2.solve();


            do {
                if (checkSolution(s2, svars)) {
//                    System.out.print(".");
                    nbsols++;
                }
            } while (s2.nextSolution());

//            System.out.println("done");

            assertEquals("nb solutions, seed:" + i, s.getSolutionCount(), nbsols);
        }

    }

    private boolean checkSolution(CPSolver s, SetVariable[] svars) {
        for (int i = 0; i < svars.length - 1; i++) {
            int[] v0 = s.getVar(svars[i]).getValue();
            int[] v1 = s.getVar(svars[i+1]).getValue();

            if (! lexorder(v0,v1)) return false;

        }
        return true;
    }

    private boolean lexorder(int[] v0, int[] v1) {
     if (v0.length == 0) return true;
        if (v1.length == 0) return false;

        int idx = 0;
        do {
           if (v0[idx] < v1[idx]) return true;
           if (v0[idx] > v1[idx]) return false;
           idx++;
        } while (idx < Math.min(v0.length, v1.length));


        if (v1.length == idx) return (idx == v0.length);

        return true;

    }

}
