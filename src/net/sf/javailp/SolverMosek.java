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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mosek.Env;
import mosek.Error;
import mosek.Task;
import mosek.Warning;

/**
 * The {@code SolverCPLEX} is the {@code Solver} Mosek.
 * 
 * @author lukasiewycz
 * 
 */
public class SolverMosek extends AbstractSolver {

	protected final Env env;
	protected final Set<Hook> hooks = new HashSet<Hook>();

	/**
	 * Constructs a {@code SolverMosek}.
	 * 
	 * @param env
	 *            the {@code mosek} environment
	 */
	public SolverMosek(Env env) {
		super();
		this.env = env;
	}

	/**
	 * The {@code Hook} for the {@code SolverMosek}.
	 * 
	 * @author lukasiewycz
	 * 
	 */
	public interface Hook {

		/**
		 * This method is called once before the optimization and allows to
		 * change some internal settings.
		 * 
		 * @param env
		 *            the environment
		 * @param task
		 *            the task
		 * @param varToIndex
		 *            the map of variables to mosek specific variables
		 */
		public void call(Env env, Task task, Map<Object, Integer> varToIndex);
	}

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

	public Result solve(Problem problem) {

		Map<Integer, Object> indexToVar = new HashMap<Integer, Object>();
		Map<Object, Integer> varToIndex = new HashMap<Object, Integer>();
		Map<Constraint, Integer> conToIndex = new HashMap<Constraint, Integer>();
		Map<Object, List<Constraint>> varToConstraints = new HashMap<Object, List<Constraint>>();

		int i = 0;
		for (Object variable : problem.getVariables()) {
			indexToVar.put(i, variable);
			varToIndex.put(variable, i);
			varToConstraints.put(variable, new ArrayList<Constraint>());

			i++;
		}

		for (i = 0; i < problem.getConstraintsCount(); i++) {
			Constraint constraint = problem.getConstraints().get(i);
			Linear linear = constraint.getLhs();

			conToIndex.put(constraint, i);

			for (Term term : linear) {
				List<Constraint> list = varToConstraints.get(term.getVariable());
				list.add(constraint);
			}
		}

		int ncon = problem.getConstraintsCount();
		int nvar = problem.getVariablesCount();

		try {
			Task task = new Task(env, ncon, nvar);

			task.append(Env.accmode.con, ncon);
			task.append(Env.accmode.var, nvar);

			if (problem.getObjective() != null) {
				Linear objective = problem.getObjective();
				int[] var = new int[objective.size()];
				double[] coeffs = new double[objective.size()];
				convert(objective, var, coeffs, varToIndex);

				task.putclist(var, coeffs);

				if (problem.getOptType() == OptType.MIN) {
					task.putobjsense(Env.objsense.minimize);
				} else {
					task.putobjsense(Env.objsense.maximize);
				}

			}

			{

				List<List<Integer>> l1 = new ArrayList<List<Integer>>();
				List<List<Double>> l2 = new ArrayList<List<Double>>();

				for (Object variable : problem.getVariables()) {
					List<Integer> cons = new ArrayList<Integer>();
					List<Double> coeffs = new ArrayList<Double>();

					List<Constraint> constraints = varToConstraints.get(variable);

					for (Constraint constraint : constraints) {
						int index = conToIndex.get(constraint);
						Linear linear = constraint.getLhs();
						Number coeff = 0.0;
						for (Term term : linear) {
							if (term.getVariable().equals(variable)) {
								coeff = term.getCoefficient();
								break;
							}
						}

						cons.add(index);
						coeffs.add(coeff.doubleValue());
					}

					l1.add(cons);
					l2.add(coeffs);
				}

				for (int j = 0; j < nvar; j++) {
					List<Integer> cons = l1.get(j);
					List<Double> coeffs = l2.get(j);

					int[] asub = new int[cons.size()];
					double[] aval = new double[cons.size()];

					for (int k = 0; k < cons.size(); k++) {
						asub[k] = cons.get(k);
						aval[k] = coeffs.get(k);
					}

					task.putavec(mosek.Env.accmode.var, j, asub, aval);
				}

				int j = 0;
				for (Constraint constraint : problem.getConstraints()) {
					double rhs = constraint.getRhs().doubleValue();
					int comp;
					switch (constraint.getOperator()) {
					case LE:
						comp = Env.boundkey.up;
						break;
					case GE:
						comp = Env.boundkey.lo;
						break;
					default: // EQ
						comp = Env.boundkey.fx;
					}
					task.putbound(Env.accmode.con, j, comp, rhs, rhs);
					j++;
				}

			}

			{
				for (Object variable : problem.getVariables()) {
					int index = varToIndex.get(variable);

					VarType varType = problem.getVarType(variable);
					Number lowerBound = problem.getVarLowerBound(variable);
					Number upperBound = problem.getVarUpperBound(variable);

					if (varType == VarType.BOOL || varType == VarType.INT) {
						task.putvartype(index, Env.variabletype.type_int);
					}

					Double lb = null;
					Double ub = null;

					if (varType == VarType.BOOL) {
						lb = 0.0;
						ub = 1.0;
						if (lowerBound != null && lowerBound.doubleValue() > 0) {
							lb = 1.0;
						}
						if (upperBound != null && upperBound.doubleValue() < 1) {
							ub = 0.0;
						}
					} else {
						if (lowerBound != null) {
							lb = lowerBound.doubleValue();
						}
						if (upperBound != null) {
							ub = upperBound.doubleValue();
						}
					}

					int bounds = Env.boundkey.fr;
					if (lb != null && ub != null) {
						bounds = Env.boundkey.ra;
					} else if (lb != null) {
						bounds = Env.boundkey.lo;
					} else if (ub != null) {
						bounds = Env.boundkey.up;
					}

					if (lb == null) {
						lb = 0.0;
					}
					if (ub == null) {
						ub = 0.0;
					}

					task.putbound(mosek.Env.accmode.var, index, bounds, lb, ub);
				}
			}
			initWithParameters(task);

			for (Hook hook : hooks) {
				hook.call(env, task, varToIndex);
			}

			task.optimize();

			int[] prosta = new int[1];
			int[] solsta = new int[1];

			task.getsolutionstatus(mosek.Env.soltype.itg, prosta, solsta);
			
			int p = prosta[0];
			
			if(p == mosek.Env.prosta.prim_infeas || 
					p == mosek.Env.prosta.prim_and_dual_infeas ||
					p == mosek.Env.prosta.dual_infeas || 
					p == mosek.Env.prosta.prim_infeas_or_unbounded){
				task.dispose();
				return null;
			}

			double[] x = new double[nvar];
			task.getsolutionslice(mosek.Env.soltype.itg, mosek.Env.solitem.xx, 0, nvar, x);

			Result result;
			if (problem.getObjective() != null) {
				result = new ResultImpl(problem.getObjective());
			} else {
				result = new ResultImpl();
			}

			for (int j = 0; j < x.length; ++j) {
				Object variable = indexToVar.get(j);

				double value = x[j];
				if (problem.getVarType(variable).isInt()) {
					int v = (int) Math.round(value);
					result.putPrimalValue(variable, v);
				} else {
					result.putPrimalValue(variable, value);
				}
			}

			task.dispose();

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void initWithParameters(Task task) throws Warning, Error {
		Object timeout = parameters.get(Solver.TIMEOUT);
		Object verbose = parameters.get(Solver.VERBOSE);

		if (timeout != null && timeout instanceof Number) {
			Number number = (Number) timeout;
			long value = number.longValue();
			task.putdouparam(Env.dparam.mio_max_time, value);
		}
		if (verbose != null && verbose instanceof Number) {
			Number number = (Number) verbose;
			int value = number.intValue();
			if (value == 0) {
				task.putintparam(Env.iparam.log, 0);
			} else if (value > 0) {
				task.putintparam(Env.iparam.log, 1);
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
