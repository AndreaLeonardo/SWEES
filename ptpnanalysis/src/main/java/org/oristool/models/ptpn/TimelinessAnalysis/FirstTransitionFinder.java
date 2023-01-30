package org.oristool.models.ptpn.TimelinessAnalysis;

import java.util.HashSet;
import java.util.Set;

import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.petrinet.Transition;
import org.oristool.analyzer.Succession;

public class FirstTransitionFinder{
	public FirstTransitionFinder() {
		// TODO Auto-generated constructor stub
	}
	
	Set<Succession> find(SuccessionGraph stateClassGraph, Transition tFirst){
		Set<Succession> graphSuccessions = stateClassGraph.getSuccessions();
		Set<Succession> firedTransitionSuccession = new HashSet<Succession>();
		for(Succession succession : graphSuccessions) {
			if(succession.getEvent().equals(tFirst)) {
				firedTransitionSuccession.addAll(stateClassGraph.getOutgoingSuccessions(stateClassGraph.getNode(succession.getChild())));
				//firedTransitionSuccession.add(succession);
			}
		}
		return firedTransitionSuccession;
		/*Set<Succession> rootSuccessions = stateClassGraph.getOutgoingSuccessions(stateClassGraph.getRoot());
		Set<Succession> firstSuccession = new HashSet<>();
		for(Succession succession : rootSuccessions) {
			if(succession.getEvent().equals(tFirst)) {
				firstSuccession.add(succession);
			}
		}
		return firstSuccession;*/
	}
}