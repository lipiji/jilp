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

import junit.framework.Assert;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryCPLEX;
import net.sf.javailp.SolverFactoryGLPK;
import net.sf.javailp.SolverFactoryGurobi;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.SolverFactoryMiniSat;
import net.sf.javailp.SolverFactoryMosek;
import net.sf.javailp.SolverFactorySAT4J;

import org.junit.Test;

public class BooleanTest {

	@Test
	public void testCPLEXMin() {
		testMin(new SolverFactoryCPLEX());
	}

	@Test
	public void testCPLEXMax() {
		testMax(new SolverFactoryCPLEX());
	}

	@Test
	public void testGLPKMin() {
		testMin(new SolverFactoryGLPK());
	}

	@Test
	public void testGLPKMax() {
		testMax(new SolverFactoryGLPK());
	}

	@Test
	public void testLpSolveMin() {
		testMin(new SolverFactoryLpSolve());
	}

	@Test
	public void testLpSolveMax() {
		testMax(new SolverFactoryLpSolve());
	}

	@Test
	public void testMosekMin() {
		testMin(new SolverFactoryMosek());
	}

	@Test
	public void testMosekMax() {
		testMax(new SolverFactoryMosek());
	}

	@Test
	public void testSAT4JMin() {
		testMin(new SolverFactorySAT4J());
	}

	@Test
	public void testSAT4JMax() {
		testMax(new SolverFactorySAT4J());
	}

	@Test
	public void testMiniSatMin() {
		testMin(new SolverFactoryMiniSat());
	}

	@Test
	public void testMiniSatMax() {
		testMax(new SolverFactoryMiniSat());
	}

	@Test
	public void testMiniSatSAT() {
		testSAT(new SolverFactoryMiniSat());
	}

	@Test
	public void testSAT4JSAT() {
		testSAT(new SolverFactorySAT4J());
	}
	
	@Test
	public void testGurobiMin() {
		testMin(new SolverFactoryGurobi());
	}

	@Test
	public void testGurobiMax() {
		testMax(new SolverFactoryGurobi());
	}
	
	@Test
	public void testGurobiSAT() {
		testSAT(new SolverFactoryGurobi());
	}
	
	

	protected void testMin(SolverFactory factory) {

		Problem problem = getProblem(8, 0, true);
		problem.setOptimizationType(OptType.MIN);
		Solver solver = factory.get();
		solver.setParameter(Solver.VERBOSE, 0);

		Result result = solver.solve(problem);

		Assert.assertEquals(219, result.getObjective().intValue());
	}

	protected void testMax(SolverFactory factory) {

		Problem problem = getProblem(8, 0, true);
		problem.setOptimizationType(OptType.MAX);
		Solver solver = factory.get();
		solver.setParameter(Solver.VERBOSE, 0);
		solver.setParameter(Solver.TIMEOUT, 100);

		Result result = solver.solve(problem);

		Assert.assertEquals(537, result.getObjective().intValue());
	}

	protected void testSAT(SolverFactory factory) {

		Problem problem = getProblem(40, 0, false);
		problem.setOptimizationType(OptType.MIN);
		Solver solver = factory.get();
		solver.setParameter(Solver.VERBOSE, 0);

		Result result = solver.solve(problem);

		Assert.assertNotNull(result);
	}

	protected Problem getProblem(int size, int seed, boolean obj) {
		Problem problem = new Problem();

		Random random = new Random(seed);

		Linear objective = new Linear();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int var = size * i + j;
				problem.setVarType(var, Boolean.class);
				objective.add(random.nextInt(100), var);
			}
		}
		if (obj) {
			problem.setObjective(objective);
		}

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
		return problem;
	}

}
