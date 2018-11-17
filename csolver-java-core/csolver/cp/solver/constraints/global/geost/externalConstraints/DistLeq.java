/*
 *  Javascript Constraint Solver (Csolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *  Choco Copyright (c) 1999-2010-2011, Ecole des Mines de Nantes
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

package csolver.cp.solver.constraints.global.geost.externalConstraints;

import csolver.kernel.solver.variables.integer.IntDomainVar;


/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 3 f�vr. 2009
 * Time: 16:29:15
 * To change this template use File | Settings | File Templates.
 */
public final class DistLeq extends ExternalConstraint  {

    public int id;
    public int D;
    public int o1;
    public int o2;
    public int q;
    public IntDomainVar DVar = null;

	public DistLeq(){}

	public DistLeq(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_)
	{
        this(ectrID,dimensions,objectIdentifiers,D_,q_,null);
	}

    public DistLeq(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_, IntDomainVar var)
    {
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        id=super.maxId; super.maxId++;


        D=D_;
        o1=objectIdentifiers[0];
        o2=objectIdentifiers[1];
        q=q_;
        oids[0]=o1;
        setObjectIds(oids); //only the first object is pruned.
        DVar=var;
    }


    public String toString() {
           StringBuilder r=new StringBuilder();

            if (DVar!=null){
                r.append("Leq(D=[").append(DVar.getInf()).append(",").append(DVar.getSup())
                        .append("],q=").append(q).append(",o1=").append(o1).append(",o2=").append(o2).append(")");
            }
            else {
                r.append("Leq(D=").append(D).append(",q=").append(q).append(",o1=")
                        .append(o1).append(",o2=").append(o2).append(")");
            }

            return r.toString();
    }

    public boolean hasDistanceVar() { return (DVar!=null); }

    public IntDomainVar getDistanceVar() { return DVar; }

    public int getCstrId() {
        return id;
    }

    
}
