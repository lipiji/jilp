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

import mosek.Env;
import mosek.Error;
import mosek.Warning;

/**
 * The {@code SolverFactoryCPLEX} is a {@code SolverFactory} for Mosek.
 * 
 * @author lukasiewycz
 * 
 */
public class SolverFactoryMosek extends AbstractSolverFactory {

	protected final Env env;

	/**
	 * Constructs a {@code SolverFactoryMosek}.
	 */
	public SolverFactoryMosek() {
		env = new Env();
		try {
			env.init();
		} catch (Error e) {
			e.printStackTrace();
		} catch (Warning e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.AbstractSolverFactory#getInternal()
	 */
	@Override
	protected Solver getInternal() {
		return new SolverMosek(env);
	}

}
