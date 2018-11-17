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

package csolver.kernel.common.logging;

/**
 * A final class which handles csolver logging statements. Most of csolver class
 * propose a static final field Logger.
 */
public final class CSolverLogging {

	/**
	 * maximal search depth for logging statements
	 */
	private static int LOGGING_MAX_DEPTH = 25;

	/**
	 * display information about search every x nodes.
	 */
	private static int EVERY_X_NODES = 2000;

	/**
	 * set the maximal search depth for logging statements
	 */
	public static final void setLoggingMaxDepth(int loggingMaxDepth) {
		if (loggingMaxDepth > 1)
			LOGGING_MAX_DEPTH = loggingMaxDepth;
	}

	/**
	 * get the maximal search depth for logging statements
	 */
	public final static int getLoggingMaxDepth() {
		return LOGGING_MAX_DEPTH;
	}

	public static final int getEveryXNodes() {
		return EVERY_X_NODES;
	}

	public static final void setEveryXNodes(int everyXnodes) {
		if (everyXnodes > 0)
			EVERY_X_NODES = everyXnodes;
	}

	// public final static String toDotty() {
	// final StringBuilder b = new StringBuilder();
	// final HashMap<Logger,Integer> indexMap = new
	// HashMap<Logger,Integer>(CSOLVER_LoggerS.length);
	// //Create a node for each logger
	// for (int i = 0; i < CSOLVER_LoggerS.length; i++) {
	// indexMap.put(CSOLVER_LoggerS[i], i);
	// String name = CSOLVER_LoggerS[i].getName();
	// final int idx = name.lastIndexOf('.');
	// if( idx != -1) name = name.substring(idx + 1);
	// b.append(i).append("[ shape=record, label=\"{");
	// b.append(name).append("|").append(CSOLVER_LoggerS[i].getLevel());
	// b.append("}\"]");
	// }
	// //Create arcs between a logger and its parent
	// for (int i = 1; i < CSOLVER_LoggerS.length; i++) {
	// b.append(indexMap.get(CSOLVER_LoggerS[i].getParent()));
	// b.append(" -> ").append(i).append('\n');
	// }
	// return b.toString();
	// }

}
