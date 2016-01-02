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
package net.sf.javailp.test;

import java.util.Random;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryCPLEX;
import net.sf.javailp.SolverFactoryGLPK;
import net.sf.javailp.SolverFactoryGurobi;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
	}

	public static void test() {
		SolverFactory factory = new SolverFactoryGurobi(); // use gurobi solver
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

		/**
		 * Constructing a Problem: Maximize: 143x+60y Subject to: 120x+210y <=
		 * 15000 110x+30y <= 4000 x+y <= 75
		 * 
		 * With x,y being integers
		 * 
		 */
		Problem problem = new Problem();

		Linear linear = new Linear();
		linear.add(143, "x");
		linear.add(60, "y");

		problem.setObjective(linear, OptType.MAX);

		linear = new Linear();
		linear.add(120, "x");
		linear.add(210, "y");

		problem.add(linear, "<=", 15000);

		linear = new Linear();
		linear.add(110, "x");
		linear.add(30, "y");

		problem.add(linear, "<=", 4000);

		linear = new Linear();
		linear.add(1, "x");
		linear.add(1, "y");

		problem.add(linear, "<=", 75);

		problem.setVarType("x", Integer.class);
		problem.setVarType("y", Integer.class);

		Solver solver = factory.get(); // you should use this solver only once
		// for one problem
		Result result = solver.solve(problem);

		System.out.println(result);

		/**
		 * Extend the problem with x <= 16 and solve it again
		 */
		problem.setVarUpperBound("x", 16);

		solver = factory.get();
		result = solver.solve(problem);

		System.out.println(result);

	}

	public static void testEuler() {

		int size = 30;

		Problem problem = new Problem();

		int sum = 0;
		int x = 1;
		Linear linear = new Linear();

		Random random = new Random(0);
		Linear objective = new Linear();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sum += x;
				x++;
				int var = i * size + j;
				linear.add(1, var);
				problem.setVarType(var, Integer.class);
				problem.setVarLowerBound(var, 1);
				problem.setVarUpperBound(var, size * size);

				objective.add(random.nextDouble(), var);
			}
		}
		problem.setObjective(objective);
		problem.add(linear, "=", sum);

		for (int i = 0; i < size; i++) {
			Linear l1 = new Linear();
			Linear l2 = new Linear();
			for (int j = 0; j < size; j++) {
				l1.add(1, i * size + j);
				l2.add(1, j * size + i);
			}
			l1.add(-1, "x");
			l2.add(-1, "x");

			problem.add(l1, "=", 0);
			problem.add(l2, "=", 0);
		}

		{
			Linear l1 = new Linear();
			Linear l2 = new Linear();
			for (int i = 0; i < size; i++) {
				l1.add(1, i * size + i);
				l2.add(1, i * size + (size - i - 1));

			}
			l1.add(-1, "x");
			l2.add(-1, "x");
			problem.add(l1, "=", 0);
			problem.add(l2, "=", 0);
		}

		SolverFactory factory = new SolverFactoryGLPK(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

		Solver solver = factory.get();
		Result result = solver.solve(problem);

		System.out.println(result);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int var = i * size + j;
				System.out.print(result.get(var) + " ");
			}
			System.out.println();
		}
	}

	public static void testQueens() {
		int size = 16;
		Problem problem = new Problem();

		Random random = new Random(0);

		Linear objective = new Linear();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int var = size * i + j;
				problem.setVarType(var, Boolean.class);
				objective.add(random.nextInt(100), var);
			}
		}
		problem.setObjective(objective);

		for (int i = 0; i < size; i++) {
			Linear l1 = new Linear();
			Linear l2 = new Linear();
			for (int j = 0; j < size; j++) {
				l1.add(1, i * size + j);
				l2.add(1, j * size + i);
			}

			problem.add(l1, "=", 1);
			problem.add(l2, "=", 1);
		}

		for (int k = -size + 1; k < size; k++) {
			// diagonal 1
			Linear linear = new Linear();
			for (int j = 0; j < size; j++) {
				int i = k + j;
				if (0 <= i && i < size) {
					linear.add(1, i * size + j);
				}
			}
			problem.add(linear, "<=", 1);
		}

		for (int k = 0; k < 2 * size - 1; k++) {
			// diagonal 2
			Linear linear = new Linear();
			for (int j = 0; j < size; j++) {
				int i = k - j;
				if (0 <= i && i < size) {
					linear.add(1, i * size + j);
				}
			}
			problem.add(linear, "<=", 1);
		}

		problem.setOptimizationType(OptType.MAX);

		SolverFactory factory = new SolverFactoryCPLEX(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 1);
		factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

		Solver solver = factory.get();
		Result result = solver.solve(problem);

		System.out.println(result);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int var = i * size + j;
				System.out.print(result.get(var) + " ");
			}
			System.out.println();
		}
	}
}
