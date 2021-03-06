/**
 *  Copyright (c) 1999-2011, Ecole des Mines de Nantes
 *  All rights reserved.
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
 */
package csolver.kernel.common.util.disposable;

import java.io.Serializable;

import csolver.kernel.common.util.iterators.IStored;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 17/02/11
 */
public class PoolManager<E extends IStored> implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final E[] elements;
    final int capacity;
    int idx;

    public PoolManager() {
        this(8);
    }

    public PoolManager(int initialSize) {
        elements = (E[]) new IStored[initialSize];
        capacity = initialSize;
        idx = 0;
    }

    public E getE() {
        if (idx == 0) {
            return null;
        } else {
            E e = elements[--idx];
            e.pop();
            return e;
        }
    }

    public void returnE(E element) {
        if (!element.isStored() && idx < capacity) {
            element.push();
            elements[idx++] = element;
        }
    }
}
