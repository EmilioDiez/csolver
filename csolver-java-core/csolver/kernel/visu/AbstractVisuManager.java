/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.visu;

import nitoku.log.Logger;

public abstract class AbstractVisuManager implements IVisuManager {

	protected int defaultWidth;
	protected int defaultHeight;
	
	
	
	@Override
	public final int getDefaultWidth() {
		return defaultWidth;
	}

	@Override
	public final void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	@Override
	public final int getDefaultHeight() {
		return defaultHeight;
	}

	@Override
	public final void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	protected abstract String getFileExtension();

	protected abstract boolean doShow(Object chart, int width, int height);


	
	@Override
	public void show(Object chart) {
		show(chart, getDefaultWidth(), getDefaultHeight());
	}

	@Override
	public void show(Object chart, int width, int height) {
		if ( doShow(chart, width, height) ) {
			Logger.config(AbstractVisuManager.class, "visu...[show][OK]");
		} else {
			Logger.warning(AbstractVisuManager.class, "visu...[show][FAIL]");
		}


	}


}