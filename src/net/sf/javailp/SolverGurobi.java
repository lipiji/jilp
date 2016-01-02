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

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The {@code SolverGurobi} is the {@code Solver} Gurobi.
 * 
 * @author fabiogenoese, lukasiewycz
 * 
 */
public class SolverGurobi extends AbstractSolver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Solver#solve(net.sf.javailp.Problem)
	 */
	public Result solve(Problem problem) {

		Map<Object, GRBVar> objToVar = new HashMap<Object, GRBVar>();
		Map<GRBVar, Object> varToObj = new HashMap<GRBVar, Object>();
		// Map<String, GRBVar> nameToVar = new HashMap<String, GRBVar>(nvar);

		try {
			GRBEnv env = new GRBEnv("gurobi.log");

			initWithParameters(env);

			GRBModel model = new GRBModel(env);

			OptType optType = problem.getOptType();
			Map<Object, Double> optimizationCoefficients = new HashMap<Object, Double>();
			Linear objective = problem.getObjective();
			if (objective != null) {
				for (Term term : objective) {
					Object variable = term.getVariable();
					double coeff = term.getCoefficient().doubleValue();
					if (optType == OptType.MAX) {
						coeff *= -1;
					}
					optimizationCoefficients.put(variable, coeff);
				}
			}

			int i = 1;
			for (Object variable : problem.getVariables()) {
				VarType varType = problem.getVarType(variable);
				Number lowerBound = problem.getVarLowerBound(variable);
				Number upperBound = problem.getVarUpperBound(variable);

				double lb = (lowerBound != null ? lowerBound.doubleValue()
						: -Double.MAX_VALUE);
				double ub = (upperBound != null ? upperBound.doubleValue()
						: Double.MAX_VALUE);

				final String name = variable.toString();
				final char type;
				switch (varType) {
				case BOOL:
					type = GRB.BINARY;
					break;
				case INT:
					type = GRB.INTEGER;
					break;
				default: // REAL
					type = GRB.CONTINUOUS;
					break;
				}

				Double coeff = optimizationCoefficients.get(variable);
				if (coeff == null) {
					coeff = 0.0;
				}

				GRBVar var = model.addVar(lb, ub, coeff, type, name);
				objToVar.put(variable, var);
				varToObj.put(var, variable);
				i++;
			}
			model.update();

			for (Constraint constraint : problem.getConstraints()) {
				GRBLinExpr expr = new GRBLinExpr();

				for (Term term : constraint.getLhs()) {
					GRBVar var = objToVar.get(term.getVariable());
					expr.addTerm(term.getCoefficient().doubleValue(), var);
				}

				final char operator;
				if (constraint.getOperator() == Operator.GE)
					operator = GRB.GREATER_EQUAL;
				else if (constraint.getOperator() == Operator.LE)
					operator = GRB.LESS_EQUAL;
				else
					operator = GRB.EQUAL;

				model.addConstr(expr, operator, constraint.getRhs()
						.doubleValue(), constraint.getName());
			}

			for(Hook hook: hooks){
				hook.call(env, model, objToVar, varToObj, problem);
			}
			
			model.optimize();

			Result result;
			if (problem.getObjective() != null) {
				result = new ResultImpl(problem.getObjective());
			} else {
				result = new ResultImpl();
			}

			for (Entry<Object, GRBVar> entry : objToVar.entrySet()) {
				Object variable = entry.getKey();
				GRBVar var = entry.getValue();

				double primalValue = var.get(GRB.DoubleAttr.X);

				if (problem.getVarType(variable).isInt()) {
					int v = (int) Math.round(primalValue);
					result.putPrimalValue(variable, v);
				} else {
					result.putPrimalValue(variable, primalValue);
				}
			}

			return result;

		} catch (GRBException e) {
			e.printStackTrace();
			return null;
		}

	}

	protected void initWithParameters(GRBEnv env) throws GRBException {
		Object verbose = parameters.get(Solver.VERBOSE);
		Object timeout = parameters.get(Solver.TIMEOUT);

		if (verbose != null && verbose instanceof Number) {
			Number number = (Number) verbose;
			final int value = number.intValue();
			final int msgLevel;
			switch (value) {
			case 0:
				msgLevel = 0;
				break;
			default: // > 0
				msgLevel = 1;
			}
			env.set(GRB.IntParam.OutputFlag, msgLevel);
		}

		if (timeout != null && timeout instanceof Number) {
			Number number = (Number) timeout;
			double value = number.doubleValue();
			env.set(GRB.DoubleParam.TimeLimit, value);
		}
	}

	/**
	 * The {@code Hook} for the {@code SolverGurobi}.
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
		 * @param model
		 *            the model
		 * @param objToVar
		 *            the map from objects to gurobi variables
		 * @param varToObj
		 *            the map from gurobi variables to objects
		 * @param problem
		 *            the problem
		 */
		public void call(GRBEnv env, GRBModel model,
				Map<Object, GRBVar> objToVar, Map<GRBVar, Object> varToObj,
				Problem problem);
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

}