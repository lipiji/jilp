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

import java.util.Map;

/**
 * The {@code Solver}.
 * 
 * @author lukasiewycz
 * 
 */
public interface Solver {

	/**
	 * Identifier for the timeout value.
	 */
	public static final int TIMEOUT = 0;

	/**
	 * Identifier for the verbose value.
	 */
	public static final int VERBOSE = 1;
	
	/**
	 * Identifier for the post-solve value.
	 */
	public static final int POSTSOLVE = 2;

	/**
	 * Sets a parameter.
	 * 
	 * @param parameter
	 *            the parameter
	 * @param value
	 *            the value
	 */
	public void setParameter(Object parameter, Object value);

	/**
	 * Returns all set parameters.
	 * 
	 * @return the map of the parameters
	 */
	public Map<Object, Object> getParameters();

	/**
	 * Solve the optimization problem. Returns {@code null} if there exists no
	 * feasible solution for the problem.
	 * 
	 * @param problem
	 *            the optimization problem
	 * @return the result
	 */
	public Result solve(Problem problem);

}
