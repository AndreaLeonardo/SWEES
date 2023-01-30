package org.oristool.models.markingptpn;

import java.util.Map;

import org.oristool.analyzer.Analyzer;
import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.models.Engine;
import org.oristool.models.ValidationMessageCollector;
import org.oristool.models.ptpn.PreemptiveAnalysis;
import org.oristool.models.ptpn.PreemptiveTransitionFeature;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.petrinet.Transition;

public class MarkingPreemptiveAnalysis implements Engine<PetriNet, Marking, SuccessionGraph>{
	private PreemptiveAnalysis analysis = PreemptiveAnalysis.builder().build();
	
	@Override
    public SuccessionGraph compute(PetriNet pn, Marking m) {
        if (!canAnalyze(pn))
            throw new IllegalArgumentException("Cannot analyze the input Petri net");

        MarkingPreemptiveComponentsFactory components = new MarkingPreemptiveComponentsFactory(true,
                analysis.excludeZeroProb(), true, analysis.policy().get(), analysis.stopOn().get(),
                analysis.monitor(), null, null, analysis.samePrioStrategy());
        
        Analyzer<PetriNet, Transition> analyzer = new Analyzer<>(components, pn, components.buildInitialState(pn, m));

        return analyzer.analyze();
    }

    @Override
    public boolean canAnalyze(PetriNet pn, ValidationMessageCollector c) {

        boolean canAnalyze = true;

        for (Transition t : pn.getTransitions()) {
            if (!t.hasFeature(TimedTransitionFeature.class)) {
                canAnalyze = false;
                c.addError("Transition '" + t + "' is not timed");
            }
        }
        
        for (Transition t : pn.getTransitions()) {
            if (t.hasFeature(PreemptiveTransitionFeature.class)) {
            	for (Map.Entry<Resource, ResourcePriority> pair : t.getFeature(PreemptiveTransitionFeature.class).getMap().entrySet()) {
            		if(pair.getKey() == null || pair.getValue() == null) {
            			canAnalyze = false;
                        c.addError("Transition '" + t + "' has an invalid preemptive feature");
                        
            		}
            	}            
            }
        }
        
        return canAnalyze;
        
    }
}