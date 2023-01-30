package org.oristool.models.ptpn;

import java.util.Set;

import org.oristool.analyzer.AnalyzerComponentsFactory;
import org.oristool.analyzer.EnabledEventsBuilder;
import org.oristool.analyzer.NoOpProcessor;
import org.oristool.analyzer.SuccessionEvaluator;
import org.oristool.analyzer.SuccessionProcessor;
import org.oristool.analyzer.log.AnalysisMonitor;
import org.oristool.analyzer.policy.EnumerationPolicy;
import org.oristool.analyzer.policy.FIFOPolicy;
import org.oristool.analyzer.state.State;
import org.oristool.analyzer.stop.AlwaysFalseStopCriterion;
import org.oristool.analyzer.stop.MonitorStopCriterion;
import org.oristool.analyzer.stop.StopCriterion;
import org.oristool.models.pn.MarkingConditionStopCriterion;
import org.oristool.models.pn.MarkingUpdater;
import org.oristool.models.pn.PetriTokensAdder;
import org.oristool.models.pn.PetriTokensRemover;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.MarkingCondition;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Transition;

public class PreemptiveComponentsFactory implements AnalyzerComponentsFactory<PetriNet, Transition> {

	private EnumerationPolicy policy;
    private PreemptiveSuccessionEvaluator preemptiveSuccessionEvaluator;

    private StopCriterion localStopCriterion;
    private StopCriterion globalStopCriterion;
    private SuccessionProcessor postProcessor;

    private InitialPreemptiveStateBuilder preemptiveInitialStateBuilder;
    
    
    public PreemptiveComponentsFactory(){
    	this(false, false, false, new FIFOPolicy(), null, null, null);
    }
    
    public PreemptiveComponentsFactory(
            boolean checkNewlyEnabled, boolean excludeZeroProb, boolean enablingSyncs,
            EnumerationPolicy policy, MarkingCondition stopCondition,
            AnalysisMonitor monitor, SamePrioStrategy strategy) {
    	
    	this(checkNewlyEnabled,excludeZeroProb, enablingSyncs,
    			policy, stopCondition, monitor, null, null, strategy);
    }
    
    public PreemptiveComponentsFactory(
            boolean checkNewlyEnabled, boolean excludeZeroProb, boolean enablingSyncs,
            EnumerationPolicy policy, MarkingCondition stopCondition,
            AnalysisMonitor monitor, MarkingUpdater tokensRemover,
            MarkingUpdater tokensAdder, SamePrioStrategy strategy) {
    	
    	this(checkNewlyEnabled, excludeZeroProb,
    			enablingSyncs, policy,
                stopCondition != null ? new MarkingConditionStopCriterion(stopCondition) : null,
                monitor, tokensRemover, tokensAdder, strategy);
    	
    }
    
    public PreemptiveComponentsFactory(
            boolean checkNewlyEnabled, boolean excludeZeroProb, boolean enablingSyncs,
            EnumerationPolicy policy, StopCriterion stopCondition,
            AnalysisMonitor monitor, MarkingUpdater tokensRemover,
            MarkingUpdater tokensAdder, SamePrioStrategy strategy) {
    	this.policy = policy;
    	
    	preemptiveSuccessionEvaluator = new PreemptiveSuccessionEvaluator(
		    	tokensRemover != null ? tokensRemover : new PetriTokensRemover(),
		        tokensAdder != null ? tokensAdder : new PetriTokensAdder(),
		        checkNewlyEnabled, excludeZeroProb, strategy);
    	
    	if (stopCondition == null)
            localStopCriterion = new AlwaysFalseStopCriterion();
        else
            localStopCriterion = stopCondition;

        if (monitor == null)
            globalStopCriterion = new AlwaysFalseStopCriterion();
        else
            globalStopCriterion = new MonitorStopCriterion(monitor);
        postProcessor = NoOpProcessor.INSTANCE;
        
        preemptiveInitialStateBuilder = new InitialPreemptiveStateBuilder(checkNewlyEnabled);
	}
    
	@Override
	public EnumerationPolicy getEnumerationPolicy() {
		return policy;
	}

	@Override
	public EnabledEventsBuilder<PetriNet, Transition> getEnabledEventsBuilder() {
		
		return new EnabledEventsBuilder<PetriNet, Transition>() {
            @Override
            public Set<Transition> getEnabledEvents(PetriNet petriNet,State state) {
            	
                return state.getFeature(PreemptiveStateFeature.class).getProgressing();
            }
        };
        
	}

	@Override
	public SuccessionEvaluator<PetriNet, Transition> getSuccessionEvaluator() {
		return preemptiveSuccessionEvaluator;
	}

	@Override
	public SuccessionProcessor getPreProcessor() {
		return NoOpProcessor.INSTANCE;
	}

	@Override
	public SuccessionProcessor getPostProcessor() {
		return postProcessor;
	}

	@Override
	public StopCriterion getLocalStopCriterion() {
		return localStopCriterion;
	}

	@Override
	public StopCriterion getGlobalStopCriterion() {
		return globalStopCriterion;
	}
	
	public State buildInitialState(PetriNet pn, Marking initialMarking) {
		return preemptiveInitialStateBuilder.computeInitialState(pn, initialMarking);
	}
	
}