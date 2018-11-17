package csolver.kernel.visu.events;

import csolver.kernel.visu.components.IVisuVar;

public class VarChangeEvent {

	IVisuVar visuvar;
	
	public VarChangeEvent(IVisuVar visuvar){
		this.visuvar = visuvar;
	}
	
	public IVisuVar getVisuVar() {
		return visuvar;
	}

}
