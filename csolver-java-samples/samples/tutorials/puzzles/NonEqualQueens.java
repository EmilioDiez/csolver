package samples.tutorials.puzzles;

import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;

import java.util.logging.Level;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.model.variables.integer.IntegerVariable;
import nitoku.log.Logger;

import static csolver.kernel.CSolver.makeIntVar;

public class NonEqualQueens {

	public static CPModel m = new CPModel();
 	public static CPSolver solver = new CPSolver();
    
	
    public static void model(int n){
    	
    	IntegerVariable[] queens = new IntegerVariable[n];
    	
        for (int i = 0; i < n; i++) {
        	int name = i+1;
            queens[i] = makeIntVar("Q" + name, 1, n);
        }
        // diagonal constraints
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int k = j - i;
                m.addConstraint(neq(queens[i], queens[j]));
                m.addConstraint(neq(queens[i], plus(queens[j], k)));
                m.addConstraint(neq(queens[i], minus(queens[j], k)));
            }
        }
    }
    
	public static void main(String[] args) {
		Logger.setLevel(Level.INFO);
		model(13);
		solver.read(m);
		solver.solveAll();
	}

}
