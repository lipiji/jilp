/**
 * Java ILP is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Java ILP is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Java ILP. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.javailp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The {@code AbstractSolverFactory} contains a map for the parameters and sets
 * these parameters for each created {@code Solver}.
 * 
 * @author lukasiewycz
 * 
 */
public abstract class AbstractSolverFactory implements SolverFactory {

	protected final Map<Object, Object> parameters = new HashMap<Object, Object>();
	
	/* (non-Javadoc)
	 * @see net.sf.javailp.SolverFactory#get()
	 */
	public Solver get() {
		Solver solver = getInternal();
		
		for(Entry<Object,Object> entry: parameters.entrySet()){
			Object parameter = entry.getKey();
			Object value = entry.getValue();
			
			solver.setParameter(parameter, value);
		}
		
		return solver;		
	}

	/* (non-Javadoc)
	 * @see net.sf.javailp.SolverFactory#getParameters()
	 */
	public Map<Object, Object> getParameters() {
		return parameters;
	}

	/* (non-Javadoc)
	 * @see net.sf.javailp.SolverFactory#setParameter(java.lang.Object, java.lang.Object)
	 */
	public void setParameter(Object parameter, Object value) {
		parameters.put(parameter, value);
	}
	
	/**
	 * Returns the {@code Solver}
	 * 
	 * @return the solver
	 */
	protected abstract Solver getInternal();

}
