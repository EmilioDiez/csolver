package csolver.kernel.common;

import java.util.HashMap;


public class NProperties {

	private HashMap<String, String> configurationMap;
	private HashMap<String, String> appMap;
	
//	public NProperties(NProperties nProperties) {
//		
//		if (nProperties != null){
//			if (nProperties.getConfigurationMap() != null){
//				this.configurationMap = nProperties.getConfigurationMap();
//			}
//			
//			if (nProperties.getAppMap() != null){
//				this.appMap = nProperties.getAppMap();
//			}
//		}else{
//			configurationMap = new HashMap<String, String>();
//			appMap = loadAppMap();
//		}
//	}

	public HashMap<String, String> getAppMap() {
		return appMap;
	}

	public HashMap<String, String> getConfigurationMap() {
		return configurationMap;
	}

	public NProperties() {
		configurationMap = new HashMap<String, String>();
		appMap = loadAppMap();
	}

	public String getProperty(String key) {
		
		try{
			if(configurationMap.containsKey(key)){
				return configurationMap.get(key);
			}
			
			if(appMap.containsKey(key)){
				//return appMap.get(key);
				return key;
			}
		}catch (Exception e){
			System.out.println("key: " + key );
			//System.out.println("configurationMap.get(key): " + configurationMap.get(key) );
		}
		
		System.out.println("key: " + key +  " not found");
		
		return null;
	}

	public void put(String key, String value) {
		configurationMap.put(key, value);
	}



	public void remove(String key) {
		configurationMap.remove(key);
	}
	
	public void clear(){
		configurationMap.clear();
	}
	
	private HashMap<String, String> loadAppMap() {
		
		HashMap<String, String> _appMap = new HashMap<String, String>();

		//# LIMITS
		_appMap.put("limit.node","choco.cp.solver.search.limit.NodeLimit");
		_appMap.put("limit.time","choco.cp.solver.search.limit.TimeLimit");
		_appMap.put("limit.fail","choco.cp.solver.search.limit.FailLimit");
		_appMap.put("limit.backtrack","choco.cp.solver.search.limit.BackTrackLimit");
		//_appMap.put("limit.cputime","choco.cp.solver.search.limit.CpuTimeLimit");


		//# VARIABLES
		_appMap.put("variable.constantinteger","choco.cp.model.managers.variables.IntegerVariableManager");
		_appMap.put("variable.constantdouble","choco.cp.model.managers.variables.RealVariableManager");
		_appMap.put("variable.constantset","choco.cp.model.managers.variables.SetVariableManager");
		_appMap.put("variable.integer","choco.cp.model.managers.variables.IntegerVariableManager");
		_appMap.put("variable.integerexpression","choco.cp.model.managers.variables.IntegerExpressionManager");
		_appMap.put("variable.real","choco.cp.model.managers.variables.RealVariableManager");
		_appMap.put("variable.set","choco.cp.model.managers.variables.SetVariableManager");
		_appMap.put("variable.task","choco.cp.model.managers.variables.TaskVariableManager");


		//# OPERATORS
		_appMap.put("operator.abs","choco.cp.model.managers.constraints.integer.AbsoluteManager");
		_appMap.put("operator.cos","choco.cp.model.managers.operators.CosManager");
		_appMap.put("operator.div","choco.cp.model.managers.operators.DivManager");
		_appMap.put("operator.ifthenelse","choco.cp.model.managers.constraints.expressions.IfThenElseManager");
		_appMap.put("operator.minus","choco.cp.model.managers.operators.MinusManager");
		_appMap.put("operator.max","choco.cp.model.managers.operators.MaxManager");
		_appMap.put("operator.min","choco.cp.model.managers.operators.MinManager");
		_appMap.put("operator.mod","choco.cp.model.managers.constraints.integer.ModuloManager");
		_appMap.put("operator.mult","choco.cp.model.managers.operators.MultManager");
		_appMap.put("operator.neg","choco.cp.model.managers.operators.NegManager");
		_appMap.put("operator.plus","choco.cp.model.managers.operators.PlusManager");
		_appMap.put("operator.power","choco.cp.model.managers.operators.PowerManager");
		_appMap.put("operator.scalar","choco.cp.model.managers.operators.ScalarManager");
		_appMap.put("operator.sin","choco.cp.model.managers.operators.SinManager");
		_appMap.put("operator.sum","choco.cp.model.managers.operators.SumManager");

		//# CONSTRAINTS
		_appMap.put("constraint.abs","choco.cp.model.managers.constraints.integer.AbsoluteManager");
		_appMap.put("constraint.allDifferent","choco.cp.model.managers.constraints.global.AllDifferentManager");
		_appMap.put("constraint.amongset","choco.cp.model.managers.constraints.set.AmongSetManager");
		_appMap.put("constraint.and","choco.cp.model.managers.constraints.expressions.AndManager");
		_appMap.put("constraint.atMostNValue","choco.cp.model.managers.constraints.global.AtMostNValueManager");
		_appMap.put("constraint.channeling","choco.cp.model.managers.constraints.integer.ChannelingManager");
		_appMap.put("constraint.clauses","choco.cp.model.managers.constraints.global.ClausesManager");
		_appMap.put("constraint.complementset","choco.cp.model.managers.constraints.set.ComplementSetManager");
		_appMap.put("constraint.costregular","choco.cp.model.managers.constraints.global.CostRegularManager");
		_appMap.put("constraint.costknapsack","choco.cp.model.managers.constraints.global.KnapsackProblemManager");
		_appMap.put("constraint.cumulative","choco.cp.model.managers.constraints.global.CumulativeManager");
		_appMap.put("constraint.disjunctive","choco.cp.model.managers.constraints.global.DisjunctiveManager");
		_appMap.put("constraint.distance","choco.cp.model.managers.constraints.integer.DistanceManager");
		_appMap.put("constraint.eq","choco.cp.model.managers.constraints.EqManager");
		_appMap.put("constraint.exactly","choco.cp.model.managers.constraints.integer.ExactlyManager");
		_appMap.put("constraint.div","choco.cp.model.managers.constraints.integer.EuclideanDivisionManager");
		_appMap.put("constraint.false","choco.cp.model.managers.constraints.BooleanManager");
		_appMap.put("constraint.fastregular","choco.cp.model.managers.constraints.global.FastRegularManager");
		_appMap.put("constraint.forbiddenIntervals","choco.cp.model.managers.constraints.global.ForbiddenIntervalsManager");
		//#constraint.flow=choco.cp.model.managers.constraints.global.FlowConstraintManager);
		_appMap.put("constraint.geost","choco.cp.model.managers.constraints.global.GeostManager");
		_appMap.put("constraint.geq","choco.cp.model.managers.constraints.EqManager");
		_appMap.put("constraint.globalCardinaly","choco.cp.model.managers.constraints.global.GlobalCardinalityManager");
		_appMap.put("constraint.gt","choco.cp.model.managers.constraints.EqManager");
		_appMap.put("constraint.ifonlyif","choco.cp.model.managers.constraints.expressions.IfOnlyIfManager");
		_appMap.put("constraint.ifthenelse","choco.cp.model.managers.constraints.expressions.IfThenElseManager");
		_appMap.put("constraint.implies","choco.cp.model.managers.constraints.expressions.ImpliesManager");
		_appMap.put("constraint.isNotIncluded","choco.cp.model.managers.constraints.set.IsNotIncludedManager");
		_appMap.put("constraint.increasingnvalue","choco.cp.model.managers.constraints.global.IncreasingNValueManager");
		_appMap.put("constraint.increasingsum","choco.cp.model.managers.constraints.integer.IncreasingSumManager");
		_appMap.put("constraint.intmember","choco.cp.model.managers.constraints.integer.IntMemberManager");
		_appMap.put("constraint.intnotmember","choco.cp.model.managers.constraints.integer.NotMemberManager");
		_appMap.put("constraint.isIncluded","choco.cp.model.managers.constraints.set.IsIncludedManager");
		_appMap.put("constraint.inverseset","choco.cp.model.managers.constraints.set.InverseSetManager");
		_appMap.put("constraint.leq","choco.cp.model.managers.constraints.EqManager");
		_appMap.put("constraint.lex","choco.cp.model.managers.constraints.global.LexManager");
		_appMap.put("constraint.lexChain","choco.cp.model.managers.constraints.global.LexChainManager");
		_appMap.put("constraint.leximin","choco.cp.model.managers.constraints.global.LeximinManager");
		_appMap.put("constraint.lt","choco.cp.model.managers.constraints.EqManager");
		_appMap.put("constraint.max","choco.cp.model.managers.constraints.integer.MinMaxManager");
		_appMap.put("constraint.metaTaskConstraint","choco.cp.model.managers.constraints.integer.MetaTaskConstraintManager");
		_appMap.put("constraint.member","choco.cp.model.managers.constraints.set.SetMemberManager");
		_appMap.put("constraint.min","choco.cp.model.managers.constraints.integer.MinMaxManager");
		_appMap.put("constraint.mod","choco.cp.model.managers.constraints.integer.ModuloManager");
		_appMap.put("constraint.multicostregular","choco.cp.model.managers.constraints.global.MultiCostRegularManager");
		_appMap.put("constraint.nand","choco.cp.model.managers.constraints.expressions.NandManager");
		_appMap.put("constraint.neq","choco.cp.model.managers.constraints.EqManager");
		_appMap.put("constraint.not","choco.cp.model.managers.constraints.expressions.NotManager");
		_appMap.put("constraint.notMember","choco.cp.model.managers.constraints.set.NotMemberManager");
		_appMap.put("constraint.nor","choco.cp.model.managers.constraints.expressions.NorManager");
		_appMap.put("constraint.nth","choco.cp.model.managers.constraints.global.ElementManager");
		_appMap.put("constraint.occurence","choco.cp.model.managers.constraints.global.OccurrenceManager");
		_appMap.put("constraint.or","choco.cp.model.managers.constraints.expressions.OrManager");
		_appMap.put("constraint.binpacking1D","choco.cp.model.managers.constraints.global.PackManager");
		_appMap.put("constraint.precedencereified","choco.cp.model.managers.constraints.global.PrecedenceReifiedManager");
		_appMap.put("constraint.precedenceimplied","choco.cp.model.managers.constraints.global.PrecedenceImpliedManager");
		_appMap.put("constraint.precedencedisjoint","choco.cp.model.managers.constraints.global.PrecedenceDisjointManager");
		_appMap.put("constraint.regular","choco.cp.model.managers.constraints.global.RegularManager");
		_appMap.put("constraint.reifiedAnd","choco.cp.model.managers.constraints.global.ReifiedAndManager");
		_appMap.put("constraint.reifiedImplication","choco.cp.model.managers.constraints.global.ReifiedImplManager");
		_appMap.put("constraint.reifiedconstraint","choco.cp.model.managers.constraints.global.ReifiedManager");
		_appMap.put("constraint.reifiedNand","choco.cp.model.managers.constraints.global.ReifiedNandManager");
		_appMap.put("constraint.reifiedNor","choco.cp.model.managers.constraints.global.ReifiedNorManager");
		_appMap.put("constraint.reifiedOr","choco.cp.model.managers.constraints.global.ReifiedOrManager");
		_appMap.put("constraint.reifiedXnor","choco.cp.model.managers.constraints.global.ReifiedXnorManager");
		_appMap.put("constraint.reifiedXor","choco.cp.model.managers.constraints.global.ReifiedXorManager");
		_appMap.put("constraint.setDisjoint","choco.cp.model.managers.constraints.set.DisjunctionManager");
		_appMap.put("constraint.setInter","choco.cp.model.managers.constraints.set.IntersectionManager");
		_appMap.put("constraint.setlex","choco.cp.model.managers.constraints.set.SetLexicographicOrderingSetManager");
		_appMap.put("constraint.setvalueprecede","choco.cp.model.managers.constraints.set.SetValuePrecedeManager");
		_appMap.put("constraint.signop","choco.cp.model.managers.constraints.integer.SignOpManager");
		_appMap.put("constraint.softmulticostregular","choco.cp.model.managers.constraints.global.SoftMultiCostRegularManager");
		_appMap.put("constraint.sorting","choco.cp.model.managers.constraints.global.SortingManager");
		_appMap.put("constraint.stretchPath","choco.cp.model.managers.constraints.global.StretchPathManager");
		_appMap.put("constraint.table","choco.cp.model.managers.constraints.integer.TableManager");
		_appMap.put("constraint.times","choco.cp.model.managers.constraints.integer.TimesManager");
		_appMap.put("constraint.tree","choco.cp.model.managers.constraints.global.TreeManager");
		_appMap.put("constraint.true","choco.cp.model.managers.constraints.BooleanManager");
		_appMap.put("constraint.union","choco.cp.model.managers.constraints.set.UnionManager");
		_appMap.put("constraint.xnor","choco.cp.model.managers.constraints.expressions.XnorManager");
		_appMap.put("constraint.xor","choco.cp.model.managers.constraints.expressions.XorManager");
		
		
		return _appMap;
		
	}
	
}
