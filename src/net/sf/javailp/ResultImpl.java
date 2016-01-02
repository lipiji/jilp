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

/**
 * The class {@code ResultImpl} is a {@code Map} based implementation of the
 * {@link Result}.
 * 
 * @author lukasiewycz
 * 
 */
public class ResultImpl implements Result {

	protected Map<Object,Number> primalValues;
	protected Map<Object,Number> dualValues;
	protected Number objectiveValue = null;
	protected Linear objectiveFunction = null;

	/**
	 * Constructs a {@code ResultImpl} for a {@code Problem} without objective
	 * function.
	 */
	public ResultImpl() {
		super();
		this.primalValues = new HashMap<Object,Number>();
		this.dualValues = new HashMap<Object,Number>();
	}

	/**
	 * Constructs a {@code ResultImpl} for a {@code Problem} with objective
	 * function and the optimal value.
	 */
	public ResultImpl(Number objectiveValue) {
		super();
		this.primalValues = new HashMap<Object,Number>();
		this.dualValues = new HashMap<Object,Number>();
		this.objectiveValue = objectiveValue;
	}

	/**
	 * Constructs a {@code ResultImpl} for a {@code Problem} with an objective
	 * function.
	 */
	public ResultImpl(Linear objectiveFunction) {
		super();
		this.primalValues = new HashMap<Object,Number>();
		this.dualValues = new HashMap<Object,Number>();
		this.objectiveFunction = objectiveFunction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#getObjective()
	 */
	public Number getObjective() {
		if (objectiveValue != null) {
			return objectiveValue;
		} else if (objectiveFunction != null) {
			objectiveValue = objectiveFunction.evaluate(this.primalValues);
			return objectiveValue;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#getBoolean(java.lang.Object)
	 */
	public boolean getBoolean(Object key) {
		Number number = primalValues.get(key);
		double v = number.doubleValue();
		if (v == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#get(java.lang.Object)
	 */
	public Number get(Object key) {
		return primalValues.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#put(java.lang.Object, java.lang.Number)
	 */
	public void put(Object key, Number value) {
		primalValues.put(key,value);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#getPrimalValue(java.lang.Object)
	 */
	public Number getPrimalValue(Object key) {
		return primalValues.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#putPrimalValue(java.lang.Object, java.lang.Number)
	 */
	public void putPrimalValue(Object key, Number value) {
		primalValues.put(key,value);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#getDualValue(java.lang.Object)
	 */
	public Number getDualValue(Object key) {
		return dualValues.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#putDualValue(java.lang.Object, java.lang.Number)
	 */
	public void putDualValue(Object key, Number value) {
		dualValues.put(key,value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#containsVar(java.lang.Object)
	 */
	public Boolean containsVar(Object var) {
		return primalValues.containsKey(var);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		return "Objective: " + getObjective() + " " + primalValues.toString();
	}

	private static final long serialVersionUID = 1L;

}