
package csolver.cp.model.managers.constraints.global;

import csolver.cp.model.managers.IntConstraintManager;
import csolver.cp.solver.constraints.global.IncreasingNValue;
import csolver.kernel.Options;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.variables.integer.IntDomainVar;

import java.util.List;

public final class IncreasingNValueManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] integerVariables, Object parameters, List<String> options) {
        IntDomainVar[] vars = new IntDomainVar[integerVariables.length-1];
        System.arraycopy(solver.getVar(integerVariables),1,vars,0,integerVariables.length-1);
        if(options.contains(Options.C_INCREASING_NVALUE_ATLEAST)){
            return new IncreasingNValue(solver.getVar(integerVariables[0]),vars, IncreasingNValue.Mode.ATLEAST);
        }
        if(options.contains(Options.C_INCREASING_NVALUE_ATMOST)){
            return new IncreasingNValue(solver.getVar(integerVariables[0]),vars, IncreasingNValue.Mode.ATMOST);
        }   
        return new IncreasingNValue(solver.getVar(integerVariables[0]),vars, IncreasingNValue.Mode.BOTH);
    }


}
