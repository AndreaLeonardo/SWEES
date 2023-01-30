package org.oristool.models.markingptpn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.oristool.analyzer.Succession;
import org.oristool.analyzer.SuccessionEvaluator;
import org.oristool.analyzer.state.State;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.domain.DBMZone;
import org.oristool.math.expression.Variable;
import org.oristool.models.pn.MarkingUpdater;
import org.oristool.models.pn.PetriStateFeature;
import org.oristool.models.pn.PetriSuccessionEvaluator;
import org.oristool.models.ptpn.PreemptiveStateFeature;
import org.oristool.models.ptpn.SamePrioStrategy;
import org.oristool.models.tpn.TimedStateFeature;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.Transition;

public class MarkingPreemptiveSuccessionEvaluator implements SuccessionEvaluator<PetriNet, Transition>{
	private PetriSuccessionEvaluator petriSuccessionEvaluator;
    private SamePrioStrategy samePrioStrategy;

	public MarkingPreemptiveSuccessionEvaluator(MarkingUpdater tokensRemover, MarkingUpdater tokensAdder, 
			boolean checkNewlyEnabled, boolean excludeZeroProb, SamePrioStrategy samePrioStrategy) {
		petriSuccessionEvaluator = new PetriSuccessionEvaluator(tokensRemover, tokensAdder, checkNewlyEnabled);
		this.samePrioStrategy = samePrioStrategy;
	}

	@Override
	public Succession computeSuccession(PetriNet petriNet, State state, Transition fired) {  
		
		final Succession succession = petriSuccessionEvaluator.computeSuccession(petriNet, state, fired);
        final PetriStateFeature nextPetriStateFeature = succession.getChild().getFeature(PetriStateFeature.class);
        final TimedStateFeature prevTimedStateFeature = succession.getParent().getFeature(TimedStateFeature.class);
        final PreemptiveStateFeature prevPreeemptiveTimeStateFeature = succession.getParent().getFeature(PreemptiveStateFeature.class);
        final Set<Variable> progressing = Transition.newVariableSetInstance(prevPreeemptiveTimeStateFeature.getProgressing());
        final Set<Variable> suspended = Transition.newVariableSetInstance(prevPreeemptiveTimeStateFeature.getSudpended());
        
        final Variable firedVar = new Variable(fired.toString());
        final DBMZone prevDomain = prevTimedStateFeature.getDomain();        
		
        // conditioning
        progressing.remove(firedVar);
        
        DBMZone newDomain = new DBMZone(prevDomain);
        newDomain.imposeVarLower(firedVar, progressing);
        if (newDomain.isEmpty())
            return null;
        
        DBMZone tmpDomain = new DBMZone(newDomain);
        tmpDomain.normalize();
        
        // shift and project
        newDomain.setNewGround(firedVar);
        
        // removing disabled transitions from progressing and suspended
        progressing.removeAll(Transition.newVariableSetInstance(nextPetriStateFeature.getDisabled()));
        suspended.removeAll(Transition.newVariableSetInstance(nextPetriStateFeature.getDisabled()));
        
        // disabling
        newDomain.projectVariables(Transition.newVariableSetInstance(nextPetriStateFeature.getDisabled()));
        
        // adding constraints for newly enabled transitions
        newDomain.addVariables(Transition.newVariableSetInstance(nextPetriStateFeature.getNewlyEnabled()));

        for (Transition t : nextPetriStateFeature.getNewlyEnabled()){
            TimedTransitionFeature ttf = t.getFeature(TimedTransitionFeature.class);
            newDomain.setCoefficient(t.newVariableInstance(), Variable.TSTAR,ttf.getLFT());
            newDomain.setCoefficient(Variable.TSTAR, t.newVariableInstance(),ttf.getEFT().negate());
        }
        
        // changing constraints for persistent transitions also progressing and suspended not disabled
        if(!suspended.isEmpty()) {
	        for (Variable p : progressing){
	        	for (Variable s : suspended){
	        		OmegaBigDecimal Cps = tmpDomain.getCoefficient(p, s).add(tmpDomain.getCoefficient(Variable.TSTAR, firedVar));
	        		newDomain.setCoefficient(p, s, Cps);
	        		OmegaBigDecimal Csp = tmpDomain.getCoefficient(s, p).add(tmpDomain.getCoefficient(firedVar, Variable.TSTAR));
	        		newDomain.setCoefficient(s, p, Csp);
	        	}
	        }
	        for (Variable s : suspended){
	        	newDomain.setCoefficient(s, Variable.TSTAR, tmpDomain.getCoefficient(s, Variable.TSTAR));
	        	newDomain.setCoefficient(Variable.TSTAR, s, tmpDomain.getCoefficient(Variable.TSTAR, s));
	    	}
        }
        
        newDomain.normalize();

        TimedStateFeature nextTimedStateFeature = new TimedStateFeature();
        nextTimedStateFeature.setDomain(newDomain);
        succession.getChild().addFeature(nextTimedStateFeature);

        // setting progressing and suspended transitions for the next step
        Set<Transition> enabledTransitions = nextPetriStateFeature.getEnabled();
        Set<Transition> progressingTransitions = new HashSet<>();
        Set<Transition> suspendedTransitions = new HashSet<>();
        
        // This
        for (Transition t : petriNet.getTransitions()) {
        	if(t.hasFeature(MarkingPreemptiveTransitionFeature.class)) {
	        	Map<Resource, MarkingResourcePriority> transitionResourcePriority = t.getFeature(MarkingPreemptiveTransitionFeature.class).getMap();
	        	// Aggiornamento della priorità per le transizioni
	        	for (Map.Entry<Resource, MarkingResourcePriority> pair : transitionResourcePriority.entrySet()) {
	        		t.getFeature(MarkingPreemptiveTransitionFeature.class).setResourcePriority(pair.getKey(), 
	        				new MarkingResourcePriority((int)(pair.getValue().getPriority(nextPetriStateFeature.getMarking())), 
	        						pair.getValue().getExpr(), petriNet));
	     	        	}
        	}
        }
        
        for (Transition t : enabledTransitions) {
        	if(t.hasFeature(MarkingPreemptiveTransitionFeature.class)) {
	        	Map<Resource, MarkingResourcePriority> transitionResourcePriority = t.getFeature(MarkingPreemptiveTransitionFeature.class).getMap();
	        	boolean found = false;
	        	for (Transition t1 : enabledTransitions) {
	            	for (Map.Entry<Resource, MarkingResourcePriority> pair : transitionResourcePriority.entrySet()) {
	            		if(t1.hasFeature(MarkingPreemptiveTransitionFeature.class)) {
	            			Map<Resource, MarkingResourcePriority> transitionResourcePriorityCompare = t1.getFeature(MarkingPreemptiveTransitionFeature.class).getMap();
		            		MarkingResourcePriority compared = transitionResourcePriorityCompare.get(pair.getKey());
		            		if(compared != null) {
		            			if(compared.isPrior(pair.getValue()) || (compared.isEqual(pair.getValue()) && samePrioStrategy.firesAfter(t, t1)))
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
        	else if((nextPetriStateFeature.getNewlyEnabled()).contains(fired) || t != fired){
        		progressingTransitions.add(t);
        	}
        }        
        PreemptiveStateFeature nextPreemptiveStateFeature = new PreemptiveStateFeature();
        nextPreemptiveStateFeature.setProgressing(progressingTransitions);
        nextPreemptiveStateFeature.setSuspended(suspendedTransitions);
        succession.getChild().addFeature(nextPreemptiveStateFeature);
	
        return succession;
		
	}
}