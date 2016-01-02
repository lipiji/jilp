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

/**
 * The class {@code Result} is a result of a {@code Problem}.
 * 
 * @author lukasiewycz
 * 
 */
public interface Result {

	/**
	 * Returns the objective value.
	 * 
	 * @return the objective value
	 */
	public Number getObjective();

	/**
	 * Returns the primal value for a specific var as a boolean. (value!=0)
	 * 
	 * @param var
	 *            the var
	 * @return {@code true} if the value is not 0
	 */
	public boolean getBoolean(Object var);

	/**
	 * Returns the primal value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @return the resulting value
	 */
	public Number get(Object var);
	
	/**
	 * Sets the primal value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the value
	 */
	public void put(Object var, Number value);
	
	/**
	 * Returns the primal value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @return the resulting value
	 */
	public Number getPrimalValue(Object var);
	
	/**
	 * Sets the primal value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the value
	 */
	public void putPrimalValue(Object var, Number value);
	
	/**
	 * Returns the dual value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @return the resulting value
	 */
	public Number getDualValue(Object var);
	
	/**
	 * Sets the dual value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the value
	 */
	public void putDualValue(Object var, Number value);

	/**
	 * Returns {@code true} if the result contains the variable.
	 * 
	 * @param var
	 *            the variable
	 * @return {@code true} if the result contains the variable
	 */
	public Boolean containsVar(Object var);

}
