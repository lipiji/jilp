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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

/**
 * The {@code SolverLpSolve} is the {@code Solver} lp_solve.
 * 
 * @author lukasiewycz
 * 
 */
public class SolverLpSolve extends AbstractSolver {

	/**
	 * The {@code Hook} for the {@code SolverLpSolve}.
	 * 
	 * @author lukasiewycz
	 * 
	 */
	public interface Hook {

		/**
		 * This method is called once before the optimization and allows to
		 * change some internal settings.
		 * 
		 * @param lp
		 *            the lp solver
		 * @param varToIndex
		 *            the map of variables to lp specific variables
		 */
		public void call(LpSolve lp, Map<Object, Integer> varToIndex);
	}

	protected final Set<Hook> hooks = new HashSet<Hook>();

	/**
	 * Adds a hook.
	 * 
	 * @param hook
	 *            the hook to be added
	 */
	public void addHook(Hook hook) {
		hooks.add(hook);
	}

	/**
	 * Removes a hook
	 * 
	 * @param hook
	 *            the hook to be removed
	 */
	public void removeHook(Hook hook) {
		hooks.remove(hook);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Solver#solve(net.sf.javailp.Problem)
	 */
	public Result solve(Problem problem) {

		Map<Integer, Object> indexToVar = new HashMap<Integer, Object>();
		Map<Object, Integer> varToIndex = new HashMap<Object, Integer>();

		int i = 1;
		for (Object variable : problem.getVariables()) {
			indexToVar.put(i, variable);
			varToIndex.put(variable, i);
			i++;
		}

		try {
			LpSolve lp = LpSolve.makeLp(0, problem.getVariablesCount());

			initWithParameters(lp);

			lp.setAddRowmode(true);

			for (Constraint constraint : problem.getConstraints()) {
				int size = constraint.size();

				int[] var = new int[size];
				double[] coeffs = new double[size];
				Linear linear = constraint.getLhs();

				convert(linear, var, coeffs, varToIndex);

				int operator;
				switch (constraint.getOperator()) {
				case LE:
					operator = LpSolve.LE;
					break;
				case GE:
					operator = LpSolve.GE;
					break;
				default: // EQ
					operator = LpSolve.EQ;
				}

				double rhs = constraint.getRhs().doubleValue();

				lp.addConstraintex(size, coeffs, var, operator, rhs);
			}

			lp.setAddRowmode(false);

			for (Object variable : problem.getVariables()) {
				int index = varToIndex.get(variable);

				VarType varType = problem.getVarType(variable);
				Number lowerBound = problem.getVarLowerBound(variable);
				Number upperBound = problem.getVarUpperBound(variable);

				if (varType == VarType.BOOL || varType == VarType.INT) {
					lp.setInt(index, true);
				}

				if (varType == VarType.BOOL) {
					int lb = 0;
					int ub = 1;
					if (lowerBound != null && lowerBound.doubleValue() > 0) {
						lb = 1;
					}
					if (upperBound != null && upperBound.doubleValue() < 1) {
						ub = 0;
					}
					lp.setLowbo(index, lb);
					lp.setUpbo(index, ub);
				} else {
					if (lowerBound != null) {
						lp.setLowbo(index, lowerBound.doubleValue());
					}
					if (upperBound != null) {
						lp.setUpbo(index, upperBound.doubleValue());
					}
				}

			}

			if (problem.getObjective() != null) {

				Linear objective = problem.getObjective();
				int size = objective.size();
				int[] var = new int[size];
				double[] coeffs = new double[size];

				convert(objective, var, coeffs, varToIndex);

				lp.setObjFnex(size, coeffs, var);

				if (problem.getOptType() == OptType.MIN) {
					lp.setMinim();
				} else {
					lp.setMaxim();
				}
			}

			for (Hook hook : hooks) {
				hook.call(lp, varToIndex);
			}

			int ret = lp.solve();
			
			// 0 means optimal
			// 1 means suboptimal
			// 12 means feasible
			if(ret != 0 && ret != 1 && ret != 12){
				lp.deleteLp();
				return null;
			}
			
			final Result result;
			if (problem.getObjective() != null) {
				result = new ResultImpl(problem.getObjective());
			} else {
				result = new ResultImpl();
			}

			
			
			double[] values = new double[problem.getVariablesCount()];
			double[] dualValues = new double[problem.getConstraintsCount()];
			lp.getVariables(values);
			// lp.getDualSolution(dualValues); throws lpsolve.LpSolveException: Target array is too short to hold values

			for (Object variable : problem.getVariables()) {

				int index = varToIndex.get(variable);
				VarType varType = problem.getVarType(variable);

				double value = values[index - 1];

				if (varType == VarType.INT || varType == VarType.BOOL) {
					int v = (int) Math.round(value);
					result.putPrimalValue(variable, v);
				} else {
					result.putPrimalValue(variable, value);
				}
			}

			lp.deleteLp();

			return result;

		} catch (LpSolveException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void initWithParameters(LpSolve lp) {
		Object timeout = parameters.get(Solver.TIMEOUT);
		Object verbose = parameters.get(Solver.VERBOSE);

		if (timeout != null && timeout instanceof Number) {
			Number number = (Number) timeout;
			long value = number.longValue();
			lp.setTimeout(value);
		}
		if (verbose != null && verbose instanceof Number) {
			Number number = (Number) verbose;
			int value = number.intValue();
			if (value == 0) {
				lp.setVerbose(0);
			} else if (value == 1) {
				lp.setVerbose(4);
			} else {
				lp.setVerbose(10);
			}
		}

	}

	protected void convert(Linear linear, int[] var, double[] coeffs, Map<Object, Integer> varToIndex) {
		int i = 0;
		for (Term term : linear) {
			var[i] = varToIndex.get(term.getVariable());
			coeffs[i] = term.getCoefficient().doubleValue();
			i++;
		}
	}
}
