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

/**
 * The {@code AbstractSolver} contains a map for the parameters.
 * 
 * @author lukasiewycz
 * 
 */
public abstract class AbstractSolver implements Solver {

	protected final Map<Object, Object> parameters = new HashMap<Object, Object>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Solver#getParameters()
	 */
	public Map<Object, Object> getParameters() {
		return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Solver#setParameter(java.lang.Object,
	 * java.lang.Object)
	 */
	public void setParameter(Object parameter, Object value) {
		parameters.put(parameter, value);
	}

}
