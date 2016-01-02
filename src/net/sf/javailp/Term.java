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
 * The {@code Term} is the basic element the {@link Linear}. It is a coefficient
 * and its variable.
 * 
 * @author lukasiewycz
 * 
 */
public class Term {

	protected final Object variable;
	protected final Number coefficient;

	/**
	 * Constructs a {@code Term}.
	 * 
	 * @param variable
	 *            the variable
	 * @param coefficient
	 *            the coefficient
	 */
	public Term(Object variable, Number coefficient) {
		super();
		this.variable = variable;
		this.coefficient = coefficient;
	}

	/**
	 * Returns the variable.
	 * 
	 * @return the variable
	 */
	public Object getVariable() {
		return variable;
	}

	/**
	 * Returns the coefficient.
	 * 
	 * @return the coefficient
	 */
	public Number getCoefficient() {
		return coefficient;
	}

}
