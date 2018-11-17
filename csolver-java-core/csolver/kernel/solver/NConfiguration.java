package csolver.kernel.solver;

import java.util.HashMap;

import csolver.cp.solver.preprocessor.PreProcessConfiguration;

public class NConfiguration {

	HashMap<String, String> map = new HashMap<String, String>();
			
	public NConfiguration(){
		
		map.put( Configuration.STOP_AT_FIRST_SOLUTION, Configuration.VALUE_TRUE);
		map.put( Configuration.RESOLUTION_POLICY, "SATISFACTION");
		map.put( Configuration.RESTART_AFTER_SOLUTION, Configuration.VALUE_FALSE);
		map.put( Configuration.RESTART_LUBY, Configuration.VALUE_FALSE);
		map.put( Configuration.RESTART_GEOMETRICAL, Configuration.VALUE_FALSE);
		map.put( Configuration.RESTART_BASE, "512");
		map.put( Configuration.RESTART_LUBY_GROW, "2");
		map.put( Configuration.RESTART_GEOM_GROW, "1.2");
		map.put( Configuration.RESTART_POLICY_LIMIT, "BACKTRACK");
		map.put( Configuration.NOGOOD_RECORDING_FROM_RESTART, Configuration.VALUE_FALSE);
		map.put( Configuration.RECOMPUTATION_GAP, "1");
		map.put( Configuration.CARD_REASONNING,Configuration.VALUE_TRUE );
		map.put( Configuration.RANDOM_SEED, "0");
		map.put( Configuration.SEARCH_LIMIT, "UNDEF");
		map.put( Configuration.SEARCH_LIMIT_BOUND, "2147483647");
		map.put( Configuration.RESTART_LIMIT , "UNDEF");
		map.put( Configuration.RESTART_LIMIT_BOUND, "2147483647" );
		map.put( Configuration.INIT_SHAVING, Configuration.VALUE_FALSE);
		map.put( Configuration.INIT_DESTRUCTIVE_LOWER_BOUND, Configuration.VALUE_FALSE );
		map.put( Configuration.INIT_DLB_SHAVING, Configuration.VALUE_FALSE);
		map.put( Configuration.INIT_SHAVE_ONLY_DECISIONS, Configuration.VALUE_TRUE );
		map.put( Configuration.BOTTOM_UP, Configuration.VALUE_FALSE);
		map.put( Configuration.HORIZON_UPPER_BOUND, "21474836");
		map.put( Configuration.SOLUTION_POOL_CAPACITY, "1");
		map.put( Configuration.RESTORE_BEST_SOLUTION, Configuration.VALUE_TRUE);
		map.put( Configuration.REAL_PRECISION, "1.0e-6" );
		map.put( Configuration.REAL_REDUCTION, "0.99" );
		map.put( Configuration.RATION_HOLE,  "0.7f");
		map.put( Configuration.VEQ_ORDER, "1234567" );
		map.put( Configuration.CEQ_ORDER, "1234567" );
		map.put( PreProcessConfiguration.RESTART_MODE, Configuration.VALUE_FALSE);
		map.put( PreProcessConfiguration.INT_EQUALITY_DETECTION,Configuration.VALUE_TRUE );
		map.put( PreProcessConfiguration.TASK_EQUALITY_DETECTION, Configuration.VALUE_TRUE);
		map.put( PreProcessConfiguration.DISJUNCTIVE_DETECTION, Configuration.VALUE_TRUE);
		map.put( PreProcessConfiguration.EXPRESSION_DETECTION,Configuration.VALUE_TRUE );
		map.put( PreProcessConfiguration.CLIQUES_DETECTION,Configuration.VALUE_TRUE );
		map.put( PreProcessConfiguration.SYMETRIE_BREAKING_DETECTION,Configuration.VALUE_TRUE );
		map.put( PreProcessConfiguration.DISJUNCTIVE_MODEL_DETECTION,Configuration.VALUE_TRUE );
		map.put( PreProcessConfiguration.DMD_USE_TIME_WINDOWS,Configuration.VALUE_TRUE );
		map.put( PreProcessConfiguration.DMD_GENERATE_CLAUSES,Configuration.VALUE_TRUE );
		map.put( PreProcessConfiguration.DMD_REMOVE_DISJUNCTIVE,Configuration.VALUE_FALSE );
		map.put( PreProcessConfiguration.DISJUNCTIVE_FROM_CUMULATIVE_DETECTION,Configuration.VALUE_FALSE );
		
	}

	public HashMap<String, String>  getDefaultMap() {
		return map;
	}


}
