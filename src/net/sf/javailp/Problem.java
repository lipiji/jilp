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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class {@code Problem} represents a linear problem consisting of multiple
 * constraints and up to one objective function.
 * 
 * @author lukasiewycz
 * 
 */
public class Problem {

	protected Linear objective = null;
	protected OptType optType = OptType.MIN;
	protected final List<Constraint> constraints = new ArrayList<Constraint>();

	protected final Set<Object> variables = new HashSet<Object>();
	protected final Map<Object, VarType> varType = new HashMap<Object, VarType>();
	protected final Map<Object, Number> varLowerBound = new HashMap<Object, Number>();
	protected final Map<Object, Number> varUpperBound = new HashMap<Object, Number>();

	/**
	 * Constructs a {@code Problem}.
	 */
	public Problem() {
		super();
	}

	/**
	 * Returns the objective function.
	 * 
	 * @return the objective function
	 */
	public Linear getObjective() {
		return objective;
	}

	/**
	 * Sets the objective function.
	 * 
	 * @param objective
	 *            the objective function
	 */
	public void setObjective(Linear objective) {
		for (Term term : objective) {
			variables.add(term.getVariable());
		}
		Linear linear = new Linear(objective);
		this.objective = linear;
	}

	/**
	 * Sets the objective function.
	 * 
	 * @param objective
	 *            the objective function
	 * @param optType
	 *            the optimization type
	 */
	public void setObjective(Linear objective, OptType optType) {
		setObjective(objective);
		setOptimizationType(optType);
	}

	/**
	 * Sets the objective function.
	 * 
	 * @param objective
	 *            the objective function
	 * @param optType
	 *            the optimization type (min,max)
	 */
	public void setObjective(Linear objective, String optType) {
		setObjective(objective);

		if (optType.equalsIgnoreCase("min")) {
			setOptimizationType(OptType.MIN);
		} else if (optType.equalsIgnoreCase("max")) {
			setOptimizationType(OptType.MAX);
		} else {
			System.err.println("Unknown optType: " + optType + " (current optimization type is " + this.optType + ")");
		}

	}

	/**
	 * Sets the optimization type.
	 * 
	 * @param optType
	 *            the optimization type to be set
	 */
	public void setOptimizationType(OptType optType) {
		this.optType = optType;
	}

	/**
	 * Returns the optimization type.
	 * 
	 * @return the optimization type
	 */
	public OptType getOptType() {
		return optType;
	}

	/**
	 * Returns the constraints.
	 * 
	 * @return the constraints.
	 */
	public List<Constraint> getConstraints() {
		return constraints;
	}

	/**
	 * Returns the number of objectives.
	 * 
	 * @return the number of objectives
	 */
	public int getConstraintsCount() {
		return constraints.size();
	}

	/**
	 * Returns the variables.
	 * 
	 * @return the variables
	 */
	public Collection<Object> getVariables() {
		return variables;
	}

	/**
	 * Returns the number of variables.
	 * 
	 * @return the number of variables
	 */
	public int getVariablesCount() {
		return variables.size();
	}

	/**
	 * Adds a constraint.
	 * 
	 * @param constraint
	 *            the constraint to be added
	 */
	public void add(Constraint constraint) {
		for (Term term : constraint.getLhs()) {
			variables.add(term.getVariable());
		}
		constraints.add(constraint);
	}

	/**
	 * Adds a constraint.
	 * 
	 * @param lhs
	 *            the left-hand-side linear expression
	 * @param operator
	 *            the operator
	 * @param rhs
	 *            the right-hand-side number
	 */
	public void add(Linear lhs, Operator operator, Number rhs) {
		Linear linear = new Linear(lhs);
		Constraint constraint = new Constraint(linear, operator, rhs);
		add(constraint);
	}

	/**
	 * Adds a constraint.
	 * 
	 * @param lhs
	 *            the left-hand-side linear expression
	 * @param operator
	 *            the operator (<=,=,>=)
	 * @param rhs
	 *            the right-hand-side number
	 */
	public void add(Linear lhs, String operator, Number rhs) {
		final Operator o;
		if (operator.equals("<=")) {
			o = Operator.LE;
		} else if (operator.equals("=")) {
			o = Operator.EQ;
		} else if (operator.equals(">=")) {
			o = Operator.GE;
		} else {
			throw new IllegalArgumentException("Unknown Boolean operator: " + operator);
		}
		add(lhs, o, rhs);
	}

	/**
	 * Adds a constraint.
	 * 
	 * @param name
	 *            the name of the constraint
	 * @param lhs
	 *            the left-hand-side linear expression
	 * @param operator
	 *            the operator
	 * @param rhs
	 *            the right-hand-side number
	 */
	public void add(String name, Linear lhs, Operator operator, Number rhs) {
		Linear linear = new Linear(lhs);
		Constraint constraint = new Constraint(name, linear, operator, rhs);
		add(constraint);
	}

	/**
	 * Adds a constraint.
	 * 
	 * @param name
	 *            the name of the constraint
	 * @param lhs
	 *            the left-hand-side linear expression
	 * @param operator
	 *            the operator (<=,=,>=)
	 * @param rhs
	 *            the right-hand-side number
	 */
	public void add(String name, Linear lhs, String operator, Number rhs) {
		final Operator o;
		if (operator.equals("<=")) {
			o = Operator.LE;
		} else if (operator.equals("=")) {
			o = Operator.EQ;
		} else if (operator.equals(">=")) {
			o = Operator.GE;
		} else {
			throw new IllegalArgumentException("Unknown Boolean operator: " + operator);
		}
		add(name, lhs, o, rhs);
	}

	/**
	 * Returns the variable type.
	 * 
	 * @param variable
	 *            the variable
	 * @return the type
	 */
	public VarType getVarType(Object variable) {
		VarType type = varType.get(variable);
		if (type != null) {
			return type;
		} else {
			return VarType.REAL;
		}
	}

	/**
	 * Sets the variable type of one variable.
	 * 
	 * @param variable
	 *            the variable
	 * @param type
	 *            the type
	 */
	public void setVarType(Object variable, VarType type) {
		varType.put(variable, type);
	}

	/**
	 * Sets the variable type of one variable. The allowed types are Integer,
	 * Boolean, and Double.
	 * 
	 * @param variable
	 *            the variable
	 * @param type
	 *            the type
	 */
	public void setVarType(Object variable, Class<?> type) {
		try {
			final VarType t;
			if (type.equals(Integer.class)) {
				t = VarType.INT;
			} else if (type.equals(Boolean.class)) {
				t = VarType.BOOL;
			} else if (type.equals(Double.class)) {
				t = VarType.REAL;
			} else {
				throw new IllegalArgumentException();
			}
			varType.put(variable, t);
		} catch (IllegalArgumentException e) {
			System.err.println(type + " is an unknown type");
		}
	}

	/**
	 * Returns the lower bound of a variable.
	 * 
	 * @param variable
	 *            the lower bound
	 * @return the variable or {@code null} if no lower bound exists
	 */
	public Number getVarLowerBound(Object variable) {
		return varLowerBound.get(variable);
	}

	/**
	 * Returns the upper bound of a variable.
	 * 
	 * @param variable
	 *            the upper bound
	 * @return the variable or {@code null} if no upper bound exists
	 */
	public Number getVarUpperBound(Object variable) {
		return varUpperBound.get(variable);
	}

	/**
	 * Sets the lower bound of a variable.
	 * 
	 * @param variable
	 *            the variable
	 * @param value
	 *            the lower bound value
	 */
	public void setVarLowerBound(Object variable, Number value) {
		varLowerBound.put(variable, value);
	}

	/**
	 * Sets the upper bound of a variable.
	 * 
	 * @param variable
	 *            the variable
	 * @param value
	 *            the upper bound value
	 */
	public void setVarUpperBound(Object variable, Number value) {
		varUpperBound.put(variable, value);
	}

	/**
	 * Sets the lower and upper bounds of a variable.
	 * 
	 * @param lower
	 *            the lower bound
	 * @param variable
	 *            the variable
	 * @param upper
	 *            the upper bound
	 */
	public void setVarBounds(Number lower, Object variable, Number upper) {
		setVarLowerBound(variable, lower);
		setVarUpperBound(variable, upper);
	}

	/**
	 * Sets the lower and upper bounds of a variable and the variable type.
	 * 
	 * @param lower
	 *            the lower bound
	 * @param variable
	 *            the variable
	 * @param upper
	 *            the upper bound
	 * @param type
	 *            the variable type
	 */
	public void setVarBoundsAndType(Number lower, Object variable, Number upper, Class<?> type) {
		setVarLowerBound(variable, lower);
		setVarUpperBound(variable, upper);
		setVarType(variable, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = "";
		String N = System.getProperty("line.separator");

		if (objective != null) {
			s += optType + N;
			s += " " + objective + N;
		} else {
			s += "Find one solution" + N;
		}
		s += "Subject To" + N;
		for (Constraint constraint : getConstraints()) {
			s += " " + constraint + N;
		}
		s += "Bounds" + N;
		for (Object variable : getVariables()) {
			Number lb = getVarLowerBound(variable);
			Number ub = getVarUpperBound(variable);

			if (lb != null || ub != null) {
				s += " ";
				if (lb != null) {
					s += lb + " <= ";
				}
				s += variable;
				if (ub != null) {
					s += " <= " + ub;
				}
				s += N;
			}
		}

		s += "Variables" + N;
		for (Object variable : getVariables()) {
			s += " " + variable + " " + getVarType(variable) + N;
		}

		return s;
	}

}
