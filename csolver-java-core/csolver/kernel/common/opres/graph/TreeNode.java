package csolver.kernel.common.opres.graph;

import java.util.HashMap;

import csolver.kernel.common.util.tools.ArrayUtils;

/**
 *
 * opération sur les enfants : insertion, suppression, recherche
 * @author Arnaud Malapert
 *
 */
public final class TreeNode {

	protected TreeNode father;

	public int index;

	protected int incomingIndex=0;

	protected HashMap<Integer,TreeNode> children = new HashMap<Integer,TreeNode>();

	/**
	 * @param index
	 */
	public TreeNode(final int index) {
		super();
		this.index = index;
	}

	@Override
	public String toString() {
		return String.valueOf(index);
	}

	public void removeChild(final int i) {
		TreeNode child = children.remove(i);
		child.father=null; //NOPMD
	}

	public void setRoot() {
		if(father!=null) {
			father.removeChild(this.index);
		}
	}

	public void addChild(final TreeNode child) {
		child.father=this;
		this.children.put(child.index, child);
	}

	public TreeNode[] copyChildren() {
		//children( new TreeNode[children.size()]);
		
		//System.out.println("test class type:  " + children.getClass());
		
		TreeNode[] tn = new TreeNode[children.size()];
		
		if (children == null){
			//System.out.println("NULL:   copyChildren");
			return null;
		}
		//System.out.println("copyChildren at GraphDTC :: children.size::" + children.size());
		
		return ArrayUtils.getValues(tn, children);
	}

	public boolean isChild(final int i) {
		return children.containsKey(i);
	}


}