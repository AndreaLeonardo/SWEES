package org.oristool.models.ptpn.TimelinessAnalysis;

import java.util.HashSet;
import java.util.Set;


import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
public class Simplex implements ShortestPathEvaluator{
	private SimplexSolver solver = new SimplexSolver();

	@Override
	public double[] getOptimizedResult(GlobalSet matrix, double[] objectiveFunctionCoefficients) throws FalseBehaviorException{
		//for minimum
		LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(objectiveFunctionCoefficients, 0);
		//for maximum
		LinearObjectiveFunction negativeObjectiveFunction = new LinearObjectiveFunction(negateVector(objectiveFunctionCoefficients), 0);
		Set<LinearConstraint> lcs = new HashSet<>();
		int row = matrix.getRows();
		int vars = matrix.getVariables();
		for(int i = 0; i < row; i++) {
			double[] currentConstraint = new double[vars];
			for(int j = 0; j < vars; j++) {
				currentConstraint[j] = matrix.getMatrixValue(i, j);
			}
			LinearConstraint lc = new LinearConstraint(currentConstraint, Relationship.LEQ, matrix.getCoefficientValue(i));
			if(!lcs.contains(lc))
				lcs.add(lc);
		}
		
		try {
			//Min
			PointValuePair pvp = solver.optimize(objectiveFunction, new LinearConstraintSet(lcs));
			double finalResultMin = pvp.getValue();
			//-Max
			PointValuePair pvpNeg = solver.optimize(negativeObjectiveFunction, new LinearConstraintSet(lcs));
			double finalResultMax = pvpNeg.getValue();
			double[] returnVal = {finalResultMin, (-1)*finalResultMax};
			return returnVal;
		}catch(NoFeasibleSolutionException ex) {
			throw new FalseBehaviorException(ex.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		double [] d = {1,-1};
		return d;
	}
	
	private double[] negateVector(double[] vec) {
		for(int i = 0; i<vec.length; i++) {
			vec[i] *= -1;
		}
		return vec;
	}
}