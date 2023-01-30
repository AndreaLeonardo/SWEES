package org.oristool.models.ptpn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.oristool.analyzer.state.State;
import org.oristool.math.domain.DBMZone;
import org.oristool.math.expression.Variable;
import org.oristool.models.pn.InitialPetriStateBuilder;
import org.oristool.models.pn.PetriStateFeature;
import org.oristool.models.tpn.TimedStateFeature;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.petrinet.Transition;

public final class InitialPreemptiveStateBuilder{
	
	private boolean checkNewlyEnabled;
	
	public InitialPreemptiveStateBuilder(boolean checkNewlyEnabled) {
		this.checkNewlyEnabled = checkNewlyEnabled;
	}
	
	public State computeInitialState(PetriNet pn, Marking initialMarking) {
		State state = InitialPetriStateBuilder.computeInitialState(pn, initialMarking,checkNewlyEnabled);
		
		Set<Transition> enabledTransitions = state.getFeature(PetriStateFeature.class).getNewlyEnabled();
        Set<Transition> progressingTransitions = new HashSet<>();
        Set<Transition> suspendedTransitions = new HashSet<>();
        
        for (Transition t : enabledTransitions) {
        	if(t.hasFeature(PreemptiveTransitionFeature.class)) {
        		Map<Resource, ResourcePriority> transitionResourcePriority = t.getFeature(PreemptiveTransitionFeature.class).getMap();
	        	boolean found = false;
	        	for (Transition t1 : enabledTransitions) { 
	            	if(t1.hasFeature(PreemptiveTransitionFeature.class)) {
	            		for (Map.Entry<Resource, ResourcePriority> pair : transitionResourcePriority.entrySet()) {
		            		Map<Resource, ResourcePriority> transitionResourcePriorityCompare = t1.getFeature(PreemptiveTransitionFeature.class).getMap();
		            		ResourcePriority compared = transitionResourcePriorityCompare.get(pair.getKey());
		            		if(compared != null) {
		            			if(compared.isPrior(pair.getValue()))
		            				found = true;
		            		}
		                }
	            	}
	            }
	        	if(!found)
	        		progressingTransitions.add(t);
	        	else
	        		suspendedTransitions.add(t);
        	}
        	else {
        		progressingTransitions.add(t);
        	}
        }
        
        PreemptiveStateFeature psf = new PreemptiveStateFeature();
        psf.setProgressing(progressingTransitions);
        psf.setSuspended(suspendedTransitions);
        state.addFeature(psf);
        
        Set<Variable> enabledVariables = Transition.newVariableSetInstance(enabledTransitions);
        
        DBMZone domain = new DBMZone(enabledVariables);
        for (Transition t : enabledTransitions) {
            TimedTransitionFeature ttf = t.getFeature(TimedTransitionFeature.class);
            domain.setCoefficient(t.newVariableInstance(), Variable.TSTAR, ttf.getLFT());
            domain.setCoefficient(Variable.TSTAR, t.newVariableInstance(), ttf.getEFT().negate());
        }
        
        TimedStateFeature tsf = new TimedStateFeature();
        tsf.setDomain(domain);
        state.addFeature(tsf);
        System.out.println("Initial State: "+state);
		return state;
	}
}