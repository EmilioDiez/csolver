/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.visu;

import csolver.kernel.common.logging.CSolverLogging;
import nitoku.log.Logger;

public interface IVisuManager {

	int getDefaultWidth();
	void setDefaultWidth(int width);
	
	int getDefaultHeight();
	void setDefaultHeight(int height);
		
	void show(Object chart, int width, int height);
	void show(Object chart);

}
