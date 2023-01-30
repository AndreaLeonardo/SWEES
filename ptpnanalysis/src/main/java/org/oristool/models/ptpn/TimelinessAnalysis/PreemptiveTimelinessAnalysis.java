package org.oristool.models.ptpn.TimelinessAnalysis;

import java.util.Random;
//import java.util.Random;
import java.util.function.Supplier;

import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.analyzer.policy.EnumerationPolicy;
import org.oristool.analyzer.policy.FIFOPolicy;
import org.oristool.models.Engine;
import org.oristool.models.ValidationMessageCollector;
import org.oristool.models.ptpn.PreemptiveAnalysis;
import org.oristool.models.stpn.trees.SuccessionGraphViewer;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Transition;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PreemptiveTimelinessAnalysis{

	PreemptiveTimelinessAnalysis() {

    }


    /**
     * Returns the shortest path algorithm used by this analysis. It is used
     * to extract the maximum computational time of a task.
     *
     * <p>The default is Symplex.
     *
     * @return the shortest path algorithm used by this analysis
     */
    public abstract ShortestPathEvaluator shortestPathAlgorithm();

    /**
     * Returns the supplier of enumeration policies used by this analysis.
     *
     * <p>A new policy instance is generated for each run.
     *
     * <p>By default, a FIFO policy is used.
     *
     * @return the supplier of state class expansion policies
     */
    public abstract Supplier<EnumerationPolicy> policy();

    /**
     * Creates a builder for analysis configurations (with default values).
     *
     * @return a builder of {@code PreemptiveAnalysis} instances.
     */
    public static Builder builder() {
        return new AutoValue_PreemptiveTimelinessAnalysis.Builder()
                .shortestPathAlgorithm(new Simplex())
        		.policy(FIFOPolicy::new);
               
    }

    @AutoValue.Builder
    public abstract static class Builder {

        /**
         * Forbids subclassing outside of this package.
         */
        Builder() {

        }
        
        /**
         * Sets the supplier of enumeration policies used by this analysis.
         *
         * <p>A new policy instance is generated for each run.
         *
         * <p>By default, a FIFO policy is used.
         *
         * @param value the supplier of state class expansion policies
         * @return this builder instance
         */
        public abstract Builder policy(Supplier<EnumerationPolicy> value);

        public abstract Builder shortestPathAlgorithm(ShortestPathEvaluator spe);

        /**
         * Builds a new instance with the provided configurations.
         *
         * @return a new {@code TimedAnalysis} instance
         */
        public abstract PreemptiveTimelinessAnalysis build();

    }

	public double[] compute(PetriNet model, SuccessionGraph stateClassGraph, Transition tFirst, Transition tLast) {
		
		TimelinessAnalyzer timelinessAnalyzer = new  TimelinessAnalyzer(shortestPathAlgorithm(), policy().get()); 
		/*Transition startTrans = model.getTransition(stateClassGraph.getOutgoingSuccessions(stateClassGraph.getRoot()).iterator().next().getEvent().toString());
		//randomly chosen last transition
		int size = model.getTransitions().size();
		int item = new Random().nextInt(size); 
		Transition endTrans = null;
		int i = 0;
		for(Transition obj : model.getTransitions()) {
		    if (i == item && obj.toString() != startTrans.toString())
		        endTrans = obj;
		    else if(i == item && obj.toString() == startTrans.toString())
		    	item++; //TODO cambiare perche potrebbe essere out of bound
		    i++;
		    if(obj.getName() == "trans2")
		    	endTrans = obj;
		}
		*/
		double[] vec = timelinessAnalyzer.analyze(stateClassGraph, tFirst, tLast);
		System.out.println("Bounds: " + vec[0] + " " + vec[1]);
		return vec;
	}
	
	
}