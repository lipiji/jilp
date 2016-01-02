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

package net.sf.javailp.minisat;

/**
 * <p>
 * The {@code MiniSat} is the JNI class for the MiniSat+ solver.
 * </p>
 * <p>
 * The variables are integers starting from {@code 0}. A positive literal of the
 * variable {@code x} is {@code x+1}, the negative literal is {@code -(x+1)}.
 * </p>
 * 
 * @see <a href="http://minisat.se/MiniSat+.html">http://minisat.se/MiniSat+.html</a>
 * 
 * @author lukasiewycz
 * 
 */
public class MiniSat {

	static {
		System.loadLibrary("minisat");
	}

	protected long pointer = 0;

	/**
	 * Construct a {@code MiniSat} instance.
	 */
	public MiniSat() {
		pointer = make();
	}

	/**
	 * Set the objective of the problem. By default the objective is minimized.
	 * If you want the objective to be maximized negate the coefficients.
	 * 
	 * @param coeffs
	 *            the coefficients
	 * @param lits
	 *            the literals
	 */
	public void setObjective(int[] coeffs, int[] lits) {
		setObjective(pointer, coeffs, lits);
	}

	/**
	 * Adds a linear constraint.
	 * 
	 * @param coeffs
	 *            the coefficients
	 * @param lits
	 *            the literals
	 * @param comp
	 *            the comparator ("<=","=",">=")
	 * @param rhs
	 *            the right hand side value
	 * @return {@code true} if this constraint did not cause a contradiction
	 */
	public boolean addConstraint(int[] coeffs, int[] lits, String comp, int rhs) {

		final int ineq;
		if (comp.equals("<=")) {
			ineq = -1;
		} else if (comp.equals("<")) {
			ineq = -2;
		} else if (comp.equals(">=")) {
			ineq = 1;
		} else if (comp.equals(">")) {
			ineq = 2;
		} else {
			ineq = 0;
		}

		return addConstraint(pointer, coeffs, lits, ineq, rhs);
	}

	/**
	 * Solve the problem.
	 */
	public void solve() {
		solve(pointer);
	}

	/**
	 * Solve the problem without optimizing.
	 * 
	 * @return {@code true} if a feasible solution is found
	 */
	public boolean solveSingle() {
		boolean b = solveSingle(pointer);
		return b;
	}

	/**
	 * Returns the value of the result of a specified variable. The method
	 * should be called after {@code MiniSat#solve()} or {@code
	 * MiniSat#solveSingle()}.
	 * 
	 * @param var
	 *            the variable
	 * @return the boolean result value
	 */
	public boolean valueOf(int var) {
		return valueOf(pointer, var);
	}

	/**
	 * Sets the initial phase and activity for a specified variable-
	 * 
	 * @param var
	 *            the variable
	 * @param phase
	 *            the phase
	 * @param activity
	 *            the activity
	 */
	public void setVar(int var, boolean phase, double activity) {
		setVar(pointer, var, phase, activity);
	}

	/**
	 * Resets the decision heuristic, i.e., the phases and activities of the
	 * variables.
	 */
	public void reset() {
		reset(pointer);
	}

	/**
	 * Set the {@code inc} value
	 * 
	 * @param value
	 *            the inc value to be set
	 */
	public void setInc(double value) {
		setInc(pointer, value);
	}

	/**
	 * Set the {@code decay} value
	 * 
	 * @param value
	 *            the decay value to be set
	 */
	public void setDecay(double value) {
		setDecay(pointer, value);
	}

	/**
	 * Set the verbosity level (0=no output,1,2=most output).
	 * 
	 * @param level
	 *            the level to be set
	 */
	public void setVerbose(int level) {
		setVerbose(pointer, level);
	}

	/**
	 * Returns {@code true} if no contradiction appeared so far.
	 * 
	 * @return {@code true} if no contradiction appeared so far
	 */
	public boolean okay() {
		return okay(pointer);
	}

	/**
	 * Allocates a new solver.
	 * 
	 * @return the pointer to the solver
	 */
	protected native long make();

	/**
	 * Frees a solver.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 */
	protected native void free(long solver);

	/**
	 * Add variables to a solver (this method has no effect).
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param numVars
	 *            the number of variables
	 */
	protected native void addVariables(long solver, int numVars);

	/**
	 * Add a constraint.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param coeffs
	 *            the coefficients
	 * @param vars
	 *            the literals
	 * @param ineq
	 *            the comparator
	 * @param rhs
	 *            the right-hand-side value
	 * @return {@code true} if no contradiction appeared
	 */
	protected native boolean addConstraint(long solver, int[] coeffs,
			int[] vars, int ineq, int rhs);

	/**
	 * Set the phase and activity of a variable.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param var
	 *            the variable
	 * @param phase
	 *            the phase
	 * @param activity
	 *            the activity
	 */
	protected native void setVar(long solver, int var, boolean phase,
			double activity);

	/**
	 * Solve the problem.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 */
	protected native void solve(long solver);

	/**
	 * Solve without optimizing the objective.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @return {@code true} if no contradiction appeared
	 */
	protected native boolean solveSingle(long solver);

	/**
	 * Set the objective.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param coeffs
	 *            the coefficients
	 * @param lits
	 *            the literals
	 */
	protected native void setObjective(long solver, int[] coeffs, int[] lits);

	/**
	 * Set the verbose level.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param level
	 *            the verbose level
	 */
	protected native void setVerbose(long solver, int level);

	/**
	 * Reset the decision heuristic of the solver.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 */
	protected native void reset(long solver);

	/**
	 * Returns {@code true} if no contradiction appeared.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @return {@code true} if no contradiction appeared
	 */
	protected native boolean okay(long solver);

	/**
	 * Set the inc value.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param value
	 *            the inc value
	 */
	protected native void setInc(long solver, double value);

	/**
	 * Set the decay value.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param value
	 *            the decay value
	 */
	protected native void setDecay(long solver, double value);

	/**
	 * Returns the value of a variable.
	 * 
	 * @param solver
	 *            the pointer to the solver
	 * @param var
	 *            the variable
	 * @return boolean value
	 */
	protected native boolean valueOf(long solver, int var);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "MiniSat";
		s += " pointer=" + pointer;
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		free(pointer);
		super.finalize();
	}

}
