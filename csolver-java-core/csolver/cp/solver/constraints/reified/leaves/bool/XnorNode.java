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

package csolver.cp.solver.constraints.reified.leaves.bool;

import csolver.cp.solver.constraints.integer.bool.BooleanFactory;
import csolver.cp.solver.constraints.integer.channeling.ReifiedBinXnor;
import csolver.kernel.common.util.tools.StringUtils;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.reified.BoolNode;
import csolver.kernel.solver.constraints.reified.INode;
import csolver.kernel.solver.constraints.reified.NodeType;
import csolver.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 23 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public final class XnorNode extends AbstractBoolNode implements BoolNode {

    public XnorNode(INode... subt) {
        super(subt, NodeType.XNOR);
    }

    public boolean checkTuple(int[] tuple) {
        return tuple[0] == tuple[1];
    }

    @Override
    public IntDomainVar extractResult(Solver s) {
        IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
        }
        if (vs.length == 1) {
            IntDomainVar v = s.createBooleanVar(StringUtils.randomName());
            s.post(new ReifiedBinXnor(v, vs[0], vs[1]));
            return v;
        } else {
            return vs[0];
        }
    }

    public SConstraint extractConstraint(Solver s) {
        IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
        }
        return BooleanFactory.xor(vs);
    }

    @Override
    public boolean isReified() {
        return true;
    }


    @Override
    public String pretty() {
        StringBuffer st = new StringBuffer("(");
        int i = 0;
        while (i < subtrees.length - 1) {
            st.append(subtrees[i].pretty()).append(" xnor ");
            i++;
        }
        st.append(subtrees[i].pretty()).append(")");
        return st.toString();
    }

}