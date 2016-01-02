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
import java.util.Map;
import java.util.Map.Entry;

import net.sf.javailp.minisat.MiniSat;

/**
 * The {@code SolverMiniSat} is the {@code Solver} MiniSat+.
 * 
 * @see <a href="http://minisat.se/MiniSat+.html">http://minisat.se/MiniSat+.html</a>
 * @author lukasiewycz
 * 
 */
public class SolverMiniSat extends AbstractSolver {

	protected MiniSat minisat;

	/**
	 * Constructs a {@code minisat+} solver.
	 */
	public SolverMiniSat() {
		minisat = new MiniSat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Solver#solve(net.sf.javailp.Problem)
	 */
	public Result solve(Problem problem) {

		Map<Object, Integer> varToInt = new HashMap<Object, Integer>();
		Map<Integer, Object> intToVar = new HashMap<Integer, Object>();

		{
			int i = 0;
			for (Object variable : problem.getVariables()) {
				check(variable, problem);
				intToVar.put(i, variable);
				varToInt.put(variable, i);
				i++;
			}
		}

		initWithParameters();

		for (Constraint constraint : problem.getConstraints()) {
			Linear linear = constraint.getLhs();
			Operator operator = constraint.getOperator();

			String comp = null;
			switch (operator) {
			case LE:
				comp = "<=";
				break;
			case GE:
				comp = ">=";
				break;
			default: // EQ
				comp = "=";
				break;
			}

			int rhs = toInt(constraint.getRhs());

			int coeffs[] = new int[linear.size()];
			int lits[] = new int[linear.size()];

			for (int i = 0; i < linear.size(); i++) {
				Term term = linear.get(i);
				int var = varToInt.get(term.getVariable());
				int coeff = toInt(term.getCoefficient());
				lits[i] = var + 1;
				coeffs[i] = coeff;
			}

			minisat.addConstraint(coeffs, lits, comp, rhs);
		}

		if (problem.getObjective() != null) {

			boolean isMin = problem.getOptType().equals(OptType.MIN);

			Linear objective = problem.getObjective();

			int coeffs[] = new int[objective.size()];
			int lits[] = new int[objective.size()];

			for (int i = 0; i < objective.size(); i++) {
				Term term = objective.get(i);
				int var = varToInt.get(term.getVariable());
				int coeff = toInt(term.getCoefficient());
				lits[i] = var + 1;
				coeffs[i] = isMin ? coeff : -coeff;
			}

			minisat.setObjective(coeffs, lits);
		}

		{
			for (Object variable : problem.getVariables()) {
				int var = varToInt.get(variable);

				Number lowerBound = problem.getVarLowerBound(variable);
				Number upperBound = problem.getVarUpperBound(variable);

				if (lowerBound != null && lowerBound.doubleValue() > 0) {
					int coeffs[] = new int[1];
					int lits[] = new int[1];
					coeffs[0] = 1;
					lits[0] = var + 1;
					minisat.addConstraint(coeffs, lits, ">=", 1);
				}
				if (upperBound != null && upperBound.doubleValue() < 1) {
					int coeffs[] = new int[1];
					int lits[] = new int[1];
					coeffs[0] = 1;
					lits[0] = var + 1;
					minisat.addConstraint(coeffs, lits, "<=", 0);
				}
			}
		}

		minisat.solve();

		if (minisat.okay()) {

			Number objvalue = null;
			Map<Object, Number> tmpresult = new HashMap<Object, Number>();

			for (Entry<Integer, Object> entry : intToVar.entrySet()) {
				int var = entry.getKey();
				Object variable = entry.getValue();

				boolean b = minisat.valueOf(var);
				tmpresult.put(variable, b ? 1 : 0);
			}

			if (problem.getObjective() != null) {
				Linear objective = problem.getObjective();
				objvalue = objective.evaluate(tmpresult);
			}

			final Result result;
			if (objvalue != null) {
				result = new ResultImpl(objvalue);
			} else {
				result = new ResultImpl();
			}

			for (Entry<Object, Number> entry : tmpresult.entrySet()) {
				Object variable = entry.getKey();
				Number value = entry.getValue();

				result.put(variable, value);
			}

			return result;

		} else {
			return null;
		}

	}

	protected void initWithParameters() {
		Object timeout = parameters.get(Solver.TIMEOUT);
		Object verbose = parameters.get(Solver.VERBOSE);

		if (timeout != null && timeout instanceof Number) {
			System.err.println("MiniSat does not support a timeout.");
		}
		if (verbose != null && verbose instanceof Number) {
			Number number = (Number) verbose;
			int value = number.intValue();
			minisat.setVerbose(value);
		}
	}

	protected void check(Object variable, Problem problem) {
		VarType type = problem.getVarType(variable);
		if (type != VarType.BOOL) {
			throw new IllegalArgumentException(
					"Variable "
							+ variable
							+ " is not a binary variable. MiniSat can only solve 0-1 ILPs.");
		}
	}

	protected int toInt(Number number) {
		int ivalue = number.intValue();
		double dvalue = number.doubleValue();

		if (dvalue != ivalue) {
			throw new IllegalArgumentException(
					"MiniSat can only solve 0-1 ILPs (all coefficients have to be integer values). Found coefficient: "
							+ dvalue);
		}

		return ivalue;
	}

}
