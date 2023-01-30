package org.oristool.models.ptpn.TimelinessAnalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.analyzer.policy.EnumerationPolicy;
import org.oristool.analyzer.state.State;
import org.oristool.petrinet.Transition;
import org.oristool.models.ptpn.TimelinessAnalysis.Path;

public class TimelinessAnalyzer{
	private PathExtractor pathExtractor;
	private TimingProfileDeriver timingProfileDeriver = new TimingProfileDeriver();
	private ShortestPathEvaluator shortestPathEvaluator;
	
	public TimelinessAnalyzer(ShortestPathEvaluator shortestPathEvaluator, EnumerationPolicy policy) {
		this.shortestPathEvaluator = shortestPathEvaluator;
		pathExtractor = new PathExtractor(policy);
	}
	
	public double[] analyze(SuccessionGraph graph, Transition tFirst, Transition tLast) {
		Set<Path> paths = pathExtractor.extract(graph, tFirst, tLast);
		Set<Path> falseBehaviors = new HashSet<Path>();
		Set<GlobalSet> matrixes = new HashSet<GlobalSet>();
		GlobalSet currentMatrix = null;
		Set<double[]> bounds = new HashSet<double[]>();
		Map<Path, double[]> boundMap= new HashMap<Path, double[]>();
		for(Path path : paths) {
			currentMatrix = timingProfileDeriver.derive(path);
			matrixes.add(currentMatrix);
			try {
				double[] tmpBound = shortestPathEvaluator.getOptimizedResult(currentMatrix, getFunctionCoefficient(currentMatrix.getVariables()));
				bounds.add(tmpBound);
				boundMap.put(path, tmpBound);
			}catch(FalseBehaviorException ex) {
				System.out.println(ex.getMessage() + " for path " + path.printTrace());
				//path.printPath();
				falseBehaviors.add(path);
			}
		}
		Iterator<Entry<Path, double[]>> iterator = boundMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<Path, double[]> currentEl = iterator.next();
			System.out.println("[" + currentEl.getValue()[0] + ", " + currentEl.getValue()[1] + "] " +
					" for trace: " + currentEl.getKey().printTrace());
		}
		double[] max = getMaximum(bounds);
		System.out.println("There are " + falseBehaviors.size() + " false behaviors and " + (paths.size() - falseBehaviors.size()) + " correct paths");
		System.out.println("The maximum is: " + "[" + max[0] + ", " + max[1] + "] ");
		return max;
	}
	
	private double[] getFunctionCoefficient(int size) {
		double[] m = new double[size];
		for(int i = 0; i < size; i++) {
			m[i] = 1;
		}
		return m;
	}
	
	private double[] getMaximum(Set<double[]> bounds) {
		double max = 0;
		double[] maxBound = new double[2];
		for(double[] bound : bounds) {
			if(bound[1] - bound[0] > max) {
				max = bound[1] - bound[0];
				maxBound = bound;
			}
		}
		return maxBound;
	}
	
}