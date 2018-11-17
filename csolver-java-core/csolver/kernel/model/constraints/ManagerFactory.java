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
package csolver.kernel.model.constraints;

import java.util.HashMap;
import java.util.Map;

import csolver.cp.model.managers.constraints.BooleanManager;
import csolver.cp.model.managers.constraints.EqManager;
import csolver.cp.model.managers.constraints.expressions.AndManager;
import csolver.cp.model.managers.constraints.expressions.IfOnlyIfManager;
import csolver.cp.model.managers.constraints.expressions.IfThenElseManager;
import csolver.cp.model.managers.constraints.expressions.ImpliesManager;
import csolver.cp.model.managers.constraints.expressions.NandManager;
import csolver.cp.model.managers.constraints.expressions.NorManager;
import csolver.cp.model.managers.constraints.expressions.NotManager;
import csolver.cp.model.managers.constraints.expressions.OrManager;
import csolver.cp.model.managers.constraints.expressions.XnorManager;
import csolver.cp.model.managers.constraints.expressions.XorManager;
import csolver.cp.model.managers.constraints.global.AllDifferentManager;
import csolver.cp.model.managers.constraints.global.AtMostNValueManager;
import csolver.cp.model.managers.constraints.global.ClausesManager;
import csolver.cp.model.managers.constraints.global.CostRegularManager;
import csolver.cp.model.managers.constraints.global.CumulativeManager;
import csolver.cp.model.managers.constraints.global.DisjunctiveManager;
import csolver.cp.model.managers.constraints.global.ElementManager;
import csolver.cp.model.managers.constraints.global.FastRegularManager;
import csolver.cp.model.managers.constraints.global.ForbiddenIntervalsManager;
import csolver.cp.model.managers.constraints.global.GeostManager;
import csolver.cp.model.managers.constraints.global.GlobalCardinalityManager;
import csolver.cp.model.managers.constraints.global.IncreasingNValueManager;
import csolver.cp.model.managers.constraints.global.KnapsackProblemManager;
import csolver.cp.model.managers.constraints.global.LexChainManager;
import csolver.cp.model.managers.constraints.global.LexManager;
import csolver.cp.model.managers.constraints.global.LeximinManager;
import csolver.cp.model.managers.constraints.global.MultiCostRegularManager;
import csolver.cp.model.managers.constraints.global.OccurrenceManager;
import csolver.cp.model.managers.constraints.global.PackManager;
import csolver.cp.model.managers.constraints.global.PrecedenceDisjointManager;
import csolver.cp.model.managers.constraints.global.PrecedenceImpliedManager;
import csolver.cp.model.managers.constraints.global.PrecedenceReifiedManager;
import csolver.cp.model.managers.constraints.global.RegularManager;
import csolver.cp.model.managers.constraints.global.ReifiedAndManager;
import csolver.cp.model.managers.constraints.global.ReifiedImplManager;
import csolver.cp.model.managers.constraints.global.ReifiedManager;
import csolver.cp.model.managers.constraints.global.ReifiedNandManager;
import csolver.cp.model.managers.constraints.global.ReifiedNorManager;
import csolver.cp.model.managers.constraints.global.ReifiedOrManager;
import csolver.cp.model.managers.constraints.global.ReifiedXnorManager;
import csolver.cp.model.managers.constraints.global.ReifiedXorManager;
import csolver.cp.model.managers.constraints.global.SoftMultiCostRegularManager;
import csolver.cp.model.managers.constraints.global.SortingManager;
import csolver.cp.model.managers.constraints.global.StretchPathManager;
import csolver.cp.model.managers.constraints.global.TreeManager;
import csolver.cp.model.managers.constraints.integer.AbsoluteManager;
import csolver.cp.model.managers.constraints.integer.ChannelingManager;
import csolver.cp.model.managers.constraints.integer.DistanceManager;
import csolver.cp.model.managers.constraints.integer.EuclideanDivisionManager;
import csolver.cp.model.managers.constraints.integer.ExactlyManager;
import csolver.cp.model.managers.constraints.integer.IncreasingSumManager;
import csolver.cp.model.managers.constraints.integer.IntMemberManager;
import csolver.cp.model.managers.constraints.integer.IntNotMemberManager;
import csolver.cp.model.managers.constraints.integer.MetaTaskConstraintManager;
import csolver.cp.model.managers.constraints.integer.MinMaxManager;
import csolver.cp.model.managers.constraints.integer.ModuloManager;
import csolver.cp.model.managers.constraints.integer.SignOpManager;
import csolver.cp.model.managers.constraints.integer.TableManager;
import csolver.cp.model.managers.constraints.integer.TimesManager;
import csolver.cp.model.managers.constraints.set.AmongSetManager;
import csolver.cp.model.managers.constraints.set.ComplementSetManager;
import csolver.cp.model.managers.constraints.set.DisjunctionManager;
import csolver.cp.model.managers.constraints.set.IntersectionManager;
import csolver.cp.model.managers.constraints.set.InverseSetManager;
import csolver.cp.model.managers.constraints.set.IsIncludedManager;
import csolver.cp.model.managers.constraints.set.IsNotIncludedManager;
import csolver.cp.model.managers.constraints.set.NotMemberManager;
import csolver.cp.model.managers.constraints.set.SetLexicographicOrderingSetManager;
import csolver.cp.model.managers.constraints.set.SetMemberManager;
import csolver.cp.model.managers.constraints.set.SetValuePrecedeManager;
import csolver.cp.model.managers.constraints.set.UnionManager;
import csolver.cp.model.managers.operators.CosManager;
import csolver.cp.model.managers.operators.DivManager;
import csolver.cp.model.managers.operators.MaxManager;
import csolver.cp.model.managers.operators.MinManager;
import csolver.cp.model.managers.operators.MinusManager;
import csolver.cp.model.managers.operators.MultManager;
import csolver.cp.model.managers.operators.NegManager;
import csolver.cp.model.managers.operators.PlusManager;
import csolver.cp.model.managers.operators.PowerManager;
import csolver.cp.model.managers.operators.ScalarManager;
import csolver.cp.model.managers.operators.SinManager;
import csolver.cp.model.managers.operators.SqrtManager;
import csolver.cp.model.managers.operators.SumManager;
import csolver.cp.model.managers.variables.IntegerExpressionManager;
import csolver.cp.model.managers.variables.IntegerVariableManager;
import csolver.cp.model.managers.variables.RealVariableManager;
import csolver.cp.model.managers.variables.SetVariableManager;
import csolver.cp.model.managers.variables.TaskVariableManager;
import csolver.cp.solver.constraints.set.MemberXiY.MemberXiYManager;
import csolver.kernel.model.ModelException;
import csolver.kernel.model.variables.VariableManager;
import csolver.kernel.solver.search.limit.BackTrackLimit;
import csolver.kernel.solver.search.limit.FailLimit;
import csolver.kernel.solver.search.limit.NodeLimit;
import csolver.kernel.solver.search.limit.TimeLimit;
/**
 * Handle all object's managers referenced by property name.
 * The class ensures that there exists at most one instance of each manager.
 * @author Arnaud Malapert</br> 
 * @since 9 févr. 2010 version 2.1.1</br>
 * @version 2.1.1</br>
 */
		
public final class ManagerFactory {

	private static final Map<String, VariableManager<?>> VM_MAP = new HashMap<String, VariableManager<?>>();

	private static final Map<String, ExpressionManager> EM_MAP = new HashMap<String, ExpressionManager>();

	private static final Map<String, ConstraintManager<?>> CM_MAP = new HashMap<String, ConstraintManager<?>>();

	private static final String ERROR_MSG="Cant load the manager@ManagerFactory: ";
	
	static NodeLimit NodeLimit;
	static TimeLimit TimeLimit;
	static FailLimit FailLimit;
	static BackTrackLimit BackTrackLimit;
	
	private static Object loadManager(String name) {
		//We get it by reflection !
		//try {
		//	return Class.forName(name).newInstance();

		//LIMITS
		if(name.compareTo("limit.node")==0) return NodeLimit;
		if(name.compareTo("limit.time")==0) return TimeLimit;
		if(name.compareTo("limit.fail")==0) return FailLimit;
		if(name.compareTo("limit.backtrack")==0) return BackTrackLimit;

		// VARIABLES
		if(name.compareTo("variable.constantinteger")==0) return new IntegerVariableManager();
		if(name.compareTo("variable.constantdouble")==0) return new RealVariableManager();
		if(name.compareTo("variable.constantset")==0) return new SetVariableManager();
		if(name.compareTo("variable.integer")==0) return new IntegerVariableManager();
		if(name.compareTo("variable.integerexpression")==0) return new IntegerExpressionManager();
		if(name.compareTo("variable.real")==0) return new RealVariableManager();
		if(name.compareTo("variable.set")==0) return new SetVariableManager();
		if(name.compareTo("variable.task")==0) return new TaskVariableManager();
			
		// OPERATORS
		if(name.compareTo("operator.abs")==0) return new AbsoluteManager();
		if(name.compareTo("operator.cos")==0) return new CosManager();
		if(name.compareTo("operator.div")==0) return new DivManager();
		if(name.compareTo("operator.ifthenelse")==0) return new IfThenElseManager();
		if(name.compareTo("operator.minus")==0) return new MinusManager();
		if(name.compareTo("operator.max")==0) return new MaxManager();
		if(name.compareTo("operator.min")==0) return new MinManager();
		if(name.compareTo("operator.mod")==0) return new ModuloManager();
		if(name.compareTo("operator.mult")==0) return new MultManager();
		if(name.compareTo("operator.neg")==0) return new NegManager();
		if(name.compareTo("operator.plus")==0) return new PlusManager();
		if(name.compareTo("operator.power")==0) return new PowerManager();
		if(name.compareTo("operator.scalar")==0) return new ScalarManager();
		if(name.compareTo("operator.sin")==0) return new SinManager();
		if(name.compareTo("operator.sum")==0) return new SumManager();
		
		// CONSTRAINTS
		if(name.compareTo("constraint.abs")==0) return new AbsoluteManager();
		if(name.compareTo("constraint.allDifferent")==0) return new AllDifferentManager();
		if(name.compareTo("constraint.amongset")==0) return new AmongSetManager();
		if(name.compareTo("constraint.and")==0) return new AndManager();
		if(name.compareTo("constraint.atMostNValue")==0) return new AtMostNValueManager();
		if(name.compareTo("constraint.channeling")==0) return new ChannelingManager();
		if(name.compareTo("constraint.clauses")==0) return new ClausesManager();
		if(name.compareTo("constraint.costknapsack")==0) return new KnapsackProblemManager();
		if(name.compareTo("constraint.costregular")==0) return new CostRegularManager();
		if(name.compareTo("constraint.cumulative")==0) return new CumulativeManager();
		if(name.compareTo("constraint.disjunctive")==0) return new DisjunctiveManager();
		if(name.compareTo("constraint.distance")==0) return new DistanceManager();

		if(name.compareTo("constraint.complementset")==0) return new ComplementSetManager();
		if(name.compareTo("constraint.costregular")==0) return new CostRegularManager();
		if(name.compareTo("constraint.cumulative")==0) return new CumulativeManager();

		if(name.compareTo("constraint.eq")==0) return new EqManager();
		if(name.compareTo("constraint.exactly")==0) return new ExactlyManager();
		if(name.compareTo("constraint.div")==0) return new EuclideanDivisionManager();
		if(name.compareTo("constraint.false")==0) return new BooleanManager();

		if(name.compareTo("constraint.fastregular")==0) return new FastRegularManager();
		if(name.compareTo("constraint.geost")==0) return new GeostManager();
		if(name.compareTo("constraint.geq")==0) return new EqManager();
		if(name.compareTo("constraint.globalCardinaly")==0) return new GlobalCardinalityManager();
		if(name.compareTo("constraint.gt")==0) return new EqManager();
		if(name.compareTo("constraint.ifonlyif")==0) return new IfOnlyIfManager();
		if(name.compareTo("constraint.ifthenelse")==0) return new IfThenElseManager();
		if(name.compareTo("constraint.implies")==0) return new ImpliesManager();
		if(name.compareTo("constraint.isIncluded")==0) return new IsIncludedManager();
		if(name.compareTo("constraint.isNotIncluded")==0) return new IsNotIncludedManager();
		if(name.compareTo("constraint.inverseset")==0) return new InverseSetManager();
		if(name.compareTo("constraint.increasingnvalue")==0) return new IncreasingNValueManager();
		if(name.compareTo("constraint.leq")==0) return new EqManager();
		if(name.compareTo("constraint.lex")==0) return new LexManager();
		if(name.compareTo("constraint.lexChain")==0) return new LexChainManager();
		if(name.compareTo("constraint.leximin")==0) return new LeximinManager();
		if(name.compareTo("constraint.lt")==0) return new EqManager();
		if(name.compareTo("constraint.max")==0) return new MinMaxManager();
		if(name.compareTo("constraint.metaTaskConstraint")==0) return new MetaTaskConstraintManager();
		if(name.compareTo("constraint.intmember")==0) return new IntMemberManager();
		if(name.compareTo("constraint.member")==0) return new SetMemberManager();
		if(name.compareTo("constraint.intnotmember")==0) return new IntNotMemberManager();
		
		if(name.compareTo("constraint.forbiddenIntervals")==0) return new ForbiddenIntervalsManager();
		if(name.compareTo("constraint.increasingsum")==0) return new IncreasingSumManager();
		if(name.compareTo("constraint.min")==0) return new MinMaxManager();
		if(name.compareTo("constraint.mod")==0) return new ModuloManager();
		if(name.compareTo("constraint.multicostregular")==0) return new MultiCostRegularManager();
		if(name.compareTo("constraint.neq")==0) return new EqManager();
		if(name.compareTo("constraint.not")==0) return new NotManager();
		if(name.compareTo("constraint.notMember")==0) return new NotMemberManager();
		
		if(name.compareTo("constraint.nth")==0) return new ElementManager();
		if(name.compareTo("constraint.occurence")==0) return new OccurrenceManager();
		if(name.compareTo("constraint.or")==0) return new OrManager();
		if(name.compareTo("constraint.binpacking1D")==0) return new PackManager();
		if(name.compareTo("constraint.precedencereified")==0) return new PrecedenceReifiedManager();
		if(name.compareTo("constraint.precedenceimplied")==0) return new PrecedenceImpliedManager();
		if(name.compareTo("constraint.precedencedisjoint")==0) return new PrecedenceDisjointManager();
		if(name.compareTo("constraint.regular")==0) return new RegularManager();
		
		if(name.compareTo("constraint.table")==0) return new TableManager();
		if(name.compareTo("constraint.times")==0) return new TimesManager();
		if(name.compareTo("constraint.tree")==0) return new TreeManager();
		if(name.compareTo("constraint.true")==0) return new BooleanManager();
		if(name.compareTo("constraint.union")==0) return new UnionManager();
		if(name.compareTo("constraint.xnor")==0) return new XnorManager();
		if(name.compareTo("constraint.xor")==0) return new XorManager();
		
		if(name.compareTo("constraint.reifiedAnd")==0) return new ReifiedAndManager();
		if(name.compareTo("constraint.reifiedImplication")==0) return new ReifiedImplManager();
		if(name.compareTo("constraint.reifiedconstraint")==0) return new ReifiedManager();
		if(name.compareTo("constraint.reifiedXnor")==0) return new ReifiedXnorManager();
		if(name.compareTo("constraint.reifiedXor")==0) return new ReifiedXorManager();
		
		if(name.compareTo("constraint.reifiedOr")==0) return new ReifiedOrManager();
		if(name.compareTo("constraint.setDisjoint")==0) return new DisjunctionManager();

		if(name.compareTo("constraint.setInter")==0) return new IntersectionManager();
		if(name.compareTo("constraint.signop")==0) return new SignOpManager();
		if(name.compareTo("constraint.sorting")==0) return new SortingManager();
		if(name.compareTo("constraint.stretchPath")==0) return new StretchPathManager();
		
		if(name.compareTo("constraint.nor")==0) return new NorManager();
		if(name.compareTo("constraint.reifiedNor")==0) return new ReifiedNorManager();
		if(name.compareTo("constraint.setlex")==0) return new SetLexicographicOrderingSetManager();
		if(name.compareTo("constraint.setvalueprecede")==0) return new SetValuePrecedeManager();
		if(name.compareTo("constraint.softmulticostregular")==0) return new SoftMultiCostRegularManager();
		if(name.compareTo("constraint.nand")==0) return new NandManager();
		if(name.compareTo("constraint.reifiedNand")==0) return new ReifiedNandManager();
		
		//NOT STORED KEYS (the original choco code worked because the propery key was the class name!)
		if(name.compareTo("csolver.cp.model.managers.operators.SqrtManager")==0) 
			return new SqrtManager();
		if(name.compareTo("csolver.cp.model.managers.constraints.set.NotMemberManager")==0) 
			return new NotMemberManager();

		if(name.compareTo("csolver.cp.solver.constraints.set.MemberXiY$MemberXiYManager")==0) 
			return new MemberXiYManager();
	
		//choco.cp.model.managers.constraints.set.NotMemberManager !


		//} catch (ClassNotFoundException e) {
			
		//NOT FOUND!!
		throw new ModelException(ERROR_MSG+name);
		//} catch (InstantiationException e) {
		//	throw new ModelException(ERROR_MSG+name);
		//} catch (IllegalAccessException e) {
		//	throw new ModelException(ERROR_MSG+name);
		//}
	}

	public static VariableManager<?> loadVariableManager(String name) {
		VariableManager<?> vm = VM_MAP.get(name);
		if( vm == null) {
			vm = (VariableManager<?>) loadManager(name);
			VM_MAP.put(name, vm);
		}
		return vm;
	}

	public static ExpressionManager loadExpressionManager(String name) {
		ExpressionManager em = EM_MAP.get(name);
		if( em == null) {
			em = (ExpressionManager) loadManager(name);
			EM_MAP.put(name, em);
		}
		return em;
	}

	public static ConstraintManager<?> loadConstraintManager(String name) {
		ConstraintManager<?> cm = CM_MAP.get(name);
		if( cm == null) {
			cm = (ConstraintManager<?>) loadManager(name);
			CM_MAP.put(name, cm);
		}
		return cm;	
	}
	
	public static void clear() {
		VM_MAP.clear();
		EM_MAP.clear();
		CM_MAP.clear();
	}
}
