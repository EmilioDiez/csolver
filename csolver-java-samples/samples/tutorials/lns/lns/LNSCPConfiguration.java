package samples.tutorials.lns.lns;

import java.util.HashMap;

import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.NConfiguration;

	/**
	 * additional settings for Large Neighborhood Search based on CP
	 * @author Sophie Demassey
	 * @see LNSCPSolver
	 */
	public class LNSCPConfiguration extends Configuration {
	
	/**
	 * the limit type set on the B&B in the initial step of LNS
	 * @see csolver.kernel.solver.search.limit.Limit
	 */
	public static final String LNS_INIT_SEARCH_LIMIT = "lns.initial.cp.search.limit.type";
	
	/** the limit value set on the B&B in the initial step of LNS */
	public static final String LNS_INIT_SEARCH_LIMIT_BOUND = "lns.initial.cp.search.limit.value";
	
	/**
	 * the limit type set on the backtracking in each neighborhood exploration of LNS
	 * @see csolver.kernel.solver.search.limit.Limit
	 */
	public static final String LNS_NEIGHBORHOOD_SEARCH_LIMIT = "lns.neighborhood.cp.search.limit.type";
	
	/** the limit value set on the backtracking in each neighborhood exploration of LNS */
	public static final String LNS_NEIGHBORHOOD_SEARCH_LIMIT_BOUND = "lns.neighborhood.cp.search.limit.value";
	
	/** the number of iterations of the loop in the second step of LNS */
	public static final String LNS_RUN_LIMIT_NUMBER = "lns.run.limit.number";
	
	/** a boolean indicating wether the CP model must be solved by LNS or B&B */
	public static final String LNS_USE = "lns.use";

	public LNSCPConfiguration()
	{
		//super(new BasicSettings());
	}

	/**
	 * Load the default value of keys defined in @Default annotation
	 * @param key the name of the field
	 */
	public String loadDefault(String key)
	{
		NConfiguration conf =  new NConfiguration();
		HashMap<String, String> defaultMap = conf.getDefaultMap();
	    
		defaultMap.put(LNS_INIT_SEARCH_LIMIT, "BACKTRACK");
		defaultMap.put(LNS_INIT_SEARCH_LIMIT_BOUND, "1000");
		defaultMap.put(LNS_NEIGHBORHOOD_SEARCH_LIMIT, "BACKTRACK");
		defaultMap.put(LNS_NEIGHBORHOOD_SEARCH_LIMIT_BOUND,"1000");
		defaultMap.put(LNS_RUN_LIMIT_NUMBER, "3");
		defaultMap.put(LNS_USE, "true");
		
		if (defaultMap.containsKey(key)){
			return defaultMap.get(key);
		}
	
	    throw new NullPointerException("cant find ");
	   
	
	}
}
