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
 * The {@code SolverFactoryLpSolve} is a {@code SolverFactory} for lp_solve.
 * 
 * @author lukasiewycz
 * 
 */
public class SolverFactoryLpSolve extends AbstractSolverFactory {

	/* (non-Javadoc)
	 * @see net.sf.javailp.AbstractSolverFactory#getInternal()
	 */
	protected Solver getInternal() {
		return new SolverLpSolve();
	}

}
