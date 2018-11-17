package csolver.kernel.common.util.tools.java.lang.reflect;

import csolver.cp.common.util.preprocessor.AbstractDetector;
import csolver.cp.solver.search.task.profile.EventRProf;
import csolver.kernel.common.opres.graph.TreeNode;
import csolver.kernel.memory.IStateLong;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.AbstractVariable;
import csolver.kernel.model.variables.MultipleVariables;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.model.variables.integer.IntegerExpressionVariable;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.real.RealVariable;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.variables.Var;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.scheduling.TaskVar;
import csolver.kernel.solver.variables.set.SetVar;

public class NArray {

//	public static <T> T[] newInstance(Class<?> componentType, int newLength) {
//	// TODO Auto-generated method stub
//	return null;
//}

//public static native <T> T[] newArray(T[] seed, int length)
///*-{
//return @com.google.gwt.lang.Array::createFrom([Ljava/lang/Object;I)(seed, length);
//}-*/;

	public static <T> T[] newInstance(Class<?> classe, int size) {
		
		T[] instance;
		
		if ((Object)classe == (Object)Object[].class){
			instance = (T[]) new Object[size];
			return instance;
		} else if ( classe == IntegerVariable.class){
			instance = (T[]) new IntegerVariable[size];
			return instance;
		}else if ( classe == SetVariable.class){
			instance = (T[]) new SetVariable[size];
			return instance;
		}else if ( classe == RealVariable.class){
			instance = (T[]) new RealVariable[size];
			return instance;
		}else if ( classe == Variable.class){
			instance = (T[]) new Variable[size];
			return instance;
		}else if ( classe == IntegerExpressionVariable.class){
			instance = (T[]) new IntegerExpressionVariable[size];
			return instance;
		}else if ( classe == MultipleVariables.class){
			instance = (T[]) new MultipleVariables[size];
			return instance;
		}else if ( classe == Constraint.class){
			instance = (T[]) new Constraint[size];
			return instance;
		}else if ( classe == IntDomainVar.class){
			instance = (T[]) new IntDomainVar[size];
			return instance;
		}else if ( classe == Var.class){
			instance = (T[]) new Var[size];
			return instance;
		}else if ( classe == IStateLong.class){
			instance = (T[]) new IStateLong[size];
			return instance;
		}else if ( classe == Integer.class){
			instance = (T[]) new Integer[size];
			return instance;
		}else if ( classe == TaskVariable.class){
			instance = (T[]) new TaskVariable[size];
			return instance;
		}else if ( classe == EventRProf.class){
			instance = (T[]) new EventRProf[size];
			return instance;
		}else if ( classe == AbstractVariable.class){
			instance = (T[]) new AbstractVariable[size];
			return instance;
		}else if ( classe == AbstractDetector.class){
			instance = (T[]) new AbstractDetector[size];
			return instance;
		}else if ( classe == TaskVar.class){
			instance = (T[]) new TaskVar[size];
			return instance;
		}else if ( classe == SetVar.class){
			instance = (T[]) new SetVar[size];
			return instance;
		}else if ( classe == TreeNode.class){
			instance = (T[]) new TreeNode[size];
			return instance;
		}else{
			//TODO: fix this, develop a proper instantiation method
//			NLogger Logger = ChocoLogging.getEngineLogger(); 
//			Logger.severe( " NArrays the classe.getName(): " + classe.getName() +
//			               " needs to be included in NArrays.newInstance() ");
			System.out.println( " NArrays the class.getName(): " + classe.getName() +
		               " needs to be included in NArrays.newInstance() ");
		}
		      
		instance = (T[]) new Object[size];
		return instance;
	}

        
}
