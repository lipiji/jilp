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
 * The class {@code SolverFactory} is used to create {@code Solver} instances.
 * 
 * @author lukasiewycz
 * 
 */
public interface SolverFactory {

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
	 * Returns a new solver instance.
	 * 
	 * @return a new solver instance
	 */
	public Solver get();

}
