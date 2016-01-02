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
 * The Type of Boolean operator.
 * 
 * @author lukasiewycz
 * 
 */
public enum Operator {

	/**
	 * Less equal
	 */
	LE,
	/**
	 * Equal
	 */
	EQ,
	/**
	 * Greater equal
	 */
	GE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
		case LE:
			return "<=";
		case GE:
			return ">=";
		default: // EQ
			return "=";
		}
	}

}
