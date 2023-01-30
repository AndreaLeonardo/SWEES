package org.oristool.models.ptpn.TimelinessAnalysis;

public interface ShortestPathEvaluator{
	
	public double[] getOptimizedResult(GlobalSet matrix, double[] objectiveFunctionCoefficients) throws FalseBehaviorException;
}