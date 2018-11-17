/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.cp.solver.visu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import csolver.cp.solver.propagation.ObservableVarEventQueue;
import csolver.cp.solver.propagation.VariableEventQueue;
import csolver.cp.solver.search.Tracer;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.propagation.PropagationEngine;
import csolver.kernel.solver.search.AbstractSearchLoop;
import csolver.kernel.solver.variables.Var;
import csolver.kernel.timer.nitoku.IObservableStepSearchLoop;
import csolver.kernel.visu.IVisu;
import csolver.kernel.visu.components.ISolverStateHandler;
import csolver.kernel.visu.components.VisuSolverState;
import csolver.kernel.visu.components.VisuVar;
import csolver.kernel.visu.components.VisuVarManager;
import csolver.kernel.visu.searchloop.ObservableStepSearchLoop;

/**
 * 
 * @author Emilio Diez
 *  
 */
public class NVisu implements IVisu {

	private final HashMap<Var, VisuVarManager> visuvars;
	
	public final Tracer tracer;
	
    protected ArrayList<VisuVar> varBricks;
   
	@Override
	 /**
	  * Initializes the {@code IVisu} from the {@code Solver}
	  *
	  * @param s the solver
	  */
	 public final void init(final Solver s) {
		
		//INIT--------- VisuVars and assign Bricks
		
		// Get the list of variables from the varBrickList 
		Variable[] variables = getVariables(s);
		
		// Create the list of visuvars 
		ArrayList<VisuVarManager> list = getVisuvariables(s, variables);
		
		//init the bricks
		if  (list != null){
			for (VisuVar brick : varBricks) {
				brick.init(s.getVar(brick.getVariable()));
			}
		
			//link the visuvar with their corresponding bricks
	        for(int i = 0; i < list.size(); i++){
	            VisuVarManager vv = list.get(i);
	            //look for bricks with that Variable and assign matched bricks with that visuvar 
	            ArrayList<VisuVar> bricks = getBrickList(vv.getVariable());         
	            for(int j = 0; j < bricks.size(); j++){
	            	//add brick to the visuvar
	            	vv.addBrick(bricks.get(j));
	            }            
	        }
		}

     
		//Set the VarEventQueue as a observable object to the tracer

		
//		PropagationEngine pe = s.getPropagationEngine();
//        try {
//            Field field = pe.getClass().getDeclaredField("varEventQueue");
//            field.setAccessible(true);
//            VariableEventQueue[] old_veq = (VariableEventQueue[]) field.get(pe);
//            VariableEventQueue[] new_veq = new VariableEventQueue[old_veq.length];
//            for(int i = 0 ; i < old_veq.length; i++){
//                ObservableVarEventQueue tmp = new ObservableVarEventQueue();
//                tracer.addObservable(tmp);
//                new_veq[i] = tmp;
//            }
//            field.set(pe, new_veq);
//            field.setAccessible(false);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
		
		PropagationEngine pe = s.getPropagationEngine();

	      VariableEventQueue[] old_veq = pe.getVarEventQueues();
	      VariableEventQueue[] new_veq = new VariableEventQueue[old_veq.length];
	      for(int i = 0 ; i < old_veq.length; i++){
	          ObservableVarEventQueue tmp = new ObservableVarEventQueue();
	          tracer.addObservable(tmp);
	          new_veq[i] = tmp;
	      }
	
		
		//tracer.addObservable((ObservableVarEventQueue) 
		//		s.getPropagationEngine().getVarEventQueues()[0]);
		//tracer.addObservable((ObservableVarEventQueue) 
		//		s.getPropagationEngine().getVarEventQueues()[1]);
		//tracer.addObservable((ObservableVarEventQueue) 
		//		s.getPropagationEngine().getVarEventQueues()[2]);
		
		//Set the variables to observe
		tracer.setVariables(visuvars.values());
		
		// Change the actual search loop by an observable and "step" one
		IObservableStepSearchLoop ssl = chooseSearchLoop(s);

		// + this.buildButtons(ssl);
		// If there is no button, run the resolution
		// + if (!visible[0] && !visible[1]) {
		ssl.setAction(IObservableStepSearchLoop.Step.PLAY);
		// + duration.setValue(1);
		// +}
		 
		tracer.setBreaklength(2);
		tracer.addObservable(ssl);
		
	 }
	
	/**
	 * return the list of bricks that contain the Var var
	 * @param variable
	 * @return
	 */
	private ArrayList<VisuVar> getBrickList(Variable variable) {
		
		ArrayList<VisuVar> bricks = new ArrayList<VisuVar>();
		
		for (VisuVar brick : varBricks) {

			if (brick.getVariable().equals(variable)){
				bricks.add(brick);
			}
			
		}
		
		return bricks;
	}

	public static IObservableStepSearchLoop chooseSearchLoop(final Solver s) {
         AbstractSearchLoop ssl = s.getSearchStrategy().getSearchLoop();
         if(ssl instanceof ObservableStepSearchLoop){
             return (ObservableStepSearchLoop)ssl;
         }
         return new ObservableStepSearchLoop(s.getSearchStrategy());
	 }
	 
	/**
	 * Create the visu object
	 */
	public NVisu() {

		tracer = new Tracer();
		visuvars = new HashMap<Var, VisuVarManager>(50);
		
	}

	 /**
	  * Return the list of variables observed
	  *
	  * @param s the solver
	  * @param variables the array of variables
	  * @return ArrayList of IVisuVariable
	  */
	 private ArrayList<VisuVarManager> getVisuvariables(final Solver s, final Variable[] variables) {
		 
		 if (variables == null){
			 return null;
		 }
		 
		 ArrayList<VisuVarManager> list = new ArrayList<VisuVarManager>(variables.length);
		 for (Variable variable1 : variables) {
			 Var var = s.getVar(variable1);
			 VisuVarManager vv = visuvars.get(var);
			 if (vv == null) {
				 vv = new VisuVarManager(var, variable1);
				 visuvars.put(var, vv);
			 }
			 list.add(vv);
		 }
		 return list;
	 }

	public void addBrick(VisuVar varBrick) {
		if (varBricks == null){
			varBricks = new ArrayList<VisuVar>();
		}
		varBricks.add(varBrick);
	}
	
	public Variable[] getVariables(Solver s) {
		
		HashSet<Variable> list = new HashSet<Variable>();
		
		if (varBricks == null){
			return null;
		}
		
		for (VisuVar brick : varBricks) {
			 Variable variable = brick.getVariable();
			 if (!list.contains(variable)) {
				 list.add(variable);
			 }			 
		}
		
		Variable[] variables = new Variable[list.size()];
		list.toArray(variables);

		return variables;
	}

	public void pause(Solver s) {
		// Change the actual search loop by an observable and "step" one
		IObservableStepSearchLoop ssl = chooseSearchLoop(s);
		ssl.setAction(IObservableStepSearchLoop.Step.PAUSE);
	}
	
	public void play(Solver s) {
		IObservableStepSearchLoop ssl = chooseSearchLoop(s);
		ssl.setAction(IObservableStepSearchLoop.Step.PLAY);		
	}
	
	public void cancel(Solver s) {
		IObservableStepSearchLoop ssl = chooseSearchLoop(s);
		ssl.setAction(IObservableStepSearchLoop.Step.STOP);		
	}
	
	public void next(Solver s) {
		IObservableStepSearchLoop ssl = chooseSearchLoop(s);
		ssl.setAction(IObservableStepSearchLoop.Step.NEXT);		
	}
	
	public void nextSol(Solver s) {
		IObservableStepSearchLoop ssl = chooseSearchLoop(s);
		ssl.setAction(IObservableStepSearchLoop.Step.NEXTSOL);		
	}

	public VisuSolverState getSolverState() {
		return tracer.getSolverState();
	}

	public void addSolverStateHandler(ISolverStateHandler iSolverStateHandler) {
		
		if (tracer.getSolverState() == null){ 
			tracer.initSolverState();
		}
		
		tracer.getSolverState().addSolverStateHandler(iSolverStateHandler);
		
	}

	
}
