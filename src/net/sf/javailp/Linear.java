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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The class {@code Linear} is a linear expression consisting of variables and
 * their coefficients.
 * 
 * @author lukasiewycz
 * 
 */
public class Linear implements Iterable<Term> {

	protected final List<Term> terms = new ArrayList<Term>();

	/**
	 * Constructs an empty linear expression.
	 */
	public Linear() {
		super();
	}

	/**
	 * Constructs a linear expression with the predefined variables and their
	 * coefficients.
	 * 
	 * @param coefficients
	 *            the coefficients
	 * @param variables
	 *            the variables
	 */
	public Linear(List<Number> coefficients, List<Object> variables) {
		this();
		if (coefficients.size() != variables.size()) {
			throw new IllegalArgumentException(
					"The size of the varibales and coefficients must be equal.");
		} else {
			for (int i = 0; i < variables.size(); i++) {
				Object variable = variables.get(i);
				Number coefficient = coefficients.get(i);
				Term term = new Term(variable, coefficient);
				add(term);
			}
		}
	}

	/**
	 * Constructs a linear expression from the terms.
	 * 
	 * @param terms
	 *            the terms to be added
	 */
	public Linear(Iterable<Term> terms) {
		for (Term term : terms) {
			add(term);
		}
	}

	/**
	 * Returns the coefficients.
	 * 
	 * @return the coefficients
	 */
	public List<Number> getCoefficients() {
		List<Number> coefficients = new ArrayList<Number>();
		for (Term term : terms) {
			coefficients.add(term.getCoefficient());
		}
		return coefficients;
	}

	/**
	 * Returns the variables.
	 * 
	 * @return the variables
	 */
	public List<Object> getVariables() {
		List<Object> variables = new ArrayList<Object>();
		for (Term term : terms) {
			variables.add(term.getVariable());
		}
		return variables;
	}

	/**
	 * Adds an element to the linear expression.
	 * 
	 * @param coefficient
	 *            the coefficient
	 * @param variable
	 *            the variable
	 */
	public void add(Number coefficient, Object variable) {
		Term term = new Term(variable, coefficient);
		add(term);
	}

	/**
	 * Adds terms.
	 * 
	 * @param terms
	 *            the terms to be added
	 */
	public void add(Term... terms) {
		for (Term term : terms) {
			this.terms.add(term);
		}
	}

	/**
	 * Returns the size (number of variables) of the linear expression.
	 * 
	 * @return the size
	 */
	public int size() {
		return terms.size();
	}

	/**
	 * Removes all elements.
	 */
	public void clear() {
		terms.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < terms.size(); i++) {
			Term term = terms.get(i);
			Number coeff = term.getCoefficient();
			Object variable = term.getVariable();

			s.append(coeff).append("*").append(variable);
			if (i < size() - 1) {
				if ((i+1) % 100 == 0) {
					s.append("\n");
				}
				s.append(" + ");
			}
		}
		return s.toString();
	}

	/**
	 * Evaluates the value of the linear expression.
	 * 
	 * @param result
	 *            the result
	 * @return the value
	 */
	public Number evaluate(Map<Object, Number> result) {
		return evaluate(result, false);
	}
	
	/**
	 * Evaluates the value of the linear expression.
	 * 
	 * @param result
	 *            the result
	 * @param ignoreMissingValues
	 * 			  if true, values that are missing in result will be set to 0
	 * @return the value
	 */
	public Number evaluate(Map<Object, Number> result, boolean ignoreMissingValues) {
		double d = 0.0;
		boolean asDouble = false;

		for (Term term : terms) {
			Object variable = term.getVariable();

			Number coeff = term.getCoefficient();
			Number value = result.get(variable);
			if (coeff instanceof Double || value instanceof Double) {
				asDouble = true;
			}

			if (value != null) {
				d += coeff.doubleValue() * value.doubleValue();
			} else {
				if (!ignoreMissingValues) {
					throw new IllegalArgumentException("The variable " + variable
							+ " is missing in the given result.");
				}
			}
		}
		if (asDouble) {
			return d;
		} else {
			return (long) d;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Term> iterator() {
		return terms.iterator();
	}

	/**
	 * Returns the {@code i}-th {@code Term}.
	 * 
	 * @param i
	 *            the index
	 * @return the term
	 */
	public Term get(int i) {
		return terms.get(i);
	}

}
