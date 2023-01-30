package org.oristool.logVerifier;

import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Postcondition;
import org.oristool.petrinet.Precondition;
import org.oristool.petrinet.Transition;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.models.ptpn.PreemptiveTransitionFeature;
import org.oristool.models.tpn.TimedTransitionFeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;


public class LogVerifier {
	
	private PetriNet pn;
	private Marking m;
	private int globalTime;
	private Map<Transition, Integer> sporadicPeriodicTransitions; // integer è il tempo di attesa
	private Set<Transition> enabledTransitions;
	private List<Transition> progressingTransitions;
	private Map<Transition, Integer> earliestFiringTimes;
	private Map<Transition, Integer> latestFiringTimes;

	
	public boolean isLogFeasible(PetriNet pn, String logName, Marking m) throws Exception {
		
		globalTime = 0;	//Absolute execution time 
		this.pn = pn;	//PetriNet Model
		this.m = m;
		acquireSporadicPeriodicTransitions();
		acquireFiringTimes();

		// Get logs
		File file = new File(logName);
    	BufferedReader log = new BufferedReader(new FileReader(file)); 
		String st;

		// Check logs
		while ((st = log.readLine()) != null) {
			// At each iteration get the next t and tau from logs
			Transition t = pn.getTransition(st);
			st = log.readLine();
			int tau = Integer.parseInt(st);
			
			globalTime = globalTime + tau;
			enabledTransitions = pn.getEnabledTransitions(m);

			System.out.println("Checking " + t.getName() + " " + tau);
			if(!isFirable(t))
				return false;
			
			if(!isTimeToFireFeasible(t, tau))
				return false;
			if(!updateAndCheckFiringTimes(t, tau))
				return false;
			
			updateEnabledTrasitions(t);
			System.out.println("Passed. Updated enabled transitions are: " + pn.getEnabledTransitions(m));
			
		}
		
		log.close();
		return true;
	}
	
	/**
	 * Check if t has minimum time to fire between progressing transitions
	 * 
	 * @param t current transition
	 * 
	 * @return true if t is firable. False otherwise
	 * 
	 * */
	private boolean isFirable(Transition t) {
		
		if(!enabledTransitions.contains(t)) {
			System.out.println(t.getName() + " is not enabled!");
			return false;
		}
		
		progressingTransitions = getProgressingTransitions();
		if (!progressingTransitions.contains(t)) {
			System.out.println(t.getName() + " is not progressing!");
			return false;
		}
		
		if(!isMinTimeToFire(t)) {
			System.out.println(t.getName() + " has not minimum time to fire!");
			return false;
		}
		
		return true;
	}

	/**
	 * Check if t has minimum time to fire between progressing transitions
	 * 
	 * @param t current transition
	 * 
	 * @return true if t has minimum time to fire. False otherwise
	 * 
	 * */
	private boolean isMinTimeToFire(Transition t) {
		
		if(sporadicPeriodicTransitions.containsKey(t))
				return true;
				
		for(Transition pt: progressingTransitions) {
			if(pn.getPreconditions(pt) != null){
				TimedTransitionFeature ttf = pt.getFeature(TimedTransitionFeature.class);
				if (ttf != null) {
					// Compare EFTs between t and all other progressing transitions
					int eftGreater = t.getFeature(TimedTransitionFeature.class).getEFT().compareTo(ttf.getEFT());
					if(eftGreater == 1) 
						return false;
				}
			}
			
		}
		return true;
	}
	
	
	/**
	 * Update and check deadlines of the firing time interval of all other progressing transitions
	 * 
	 * @param t current transition
	 * @param tau time to fire of the log
	 * 
	 * */
	private boolean updateAndCheckFiringTimes(Transition t, int tau) {
		for (Transition pt: progressingTransitions) {
			if(pt.getName() != t.getName() && !sporadicPeriodicTransitions.containsKey(pt)) {
				int newEFT = earliestFiringTimes.get(pt) - tau;
				int newLFT = latestFiringTimes.get(pt) - tau;
				
				if (newLFT < 0) { //deadline not respected
					System.out.println("deadline of " + pt.getName() + " not respected!");
					return false;
				}
				earliestFiringTimes.put(pt, newEFT);
				latestFiringTimes.put(pt, newLFT);
			}
		}
		// Update EFT and LFT for current transition t
		earliestFiringTimes.put(t, t.getFeature(TimedTransitionFeature.class).getEFT().intValue());
		latestFiringTimes.put(t, t.getFeature(TimedTransitionFeature.class).getLFT().intValue());
		
		return true;
	}
	
	/**
	 * get progressing transitions
	 * 
	 * @return list of progressing transitions
	 * */
	private List<Transition> getProgressingTransitions(){
		
		List<Transition> progressingTransitions = new ArrayList<Transition>();
		
		//creates an hashmap to associates each resource (key 1) and priority (key 2) to a list of transitions  
		Table<Resource, Integer, List<Transition>> resourceMap = HashBasedTable.create();		
		for(Transition et: enabledTransitions){ 
			PreemptiveTransitionFeature ptf = et.getFeature(PreemptiveTransitionFeature.class);
			if(ptf != null) {
				Map<Resource, ResourcePriority> resources = ptf.getMap();
				for (Map.Entry<Resource, ResourcePriority> pair : resources.entrySet()) {
					Resource resource = pair.getKey();
					ResourcePriority resourcePriority = pair.getValue();
					
					//get the list of transitions for (resource, priority) key pair
					List<Transition> lt = resourceMap.get(resource, resourcePriority.value());
					
					// if the (resource, priority) key does not exist yet then initialize a new empty list
					if (lt == null) {
						lt = new ArrayList<Transition>();
						resourceMap.put(resource, resourcePriority.value(), lt);
					}
					
					// Finally, add the current transition to the list associated with the (resource, priority) Hashmap key
					lt.add(et);
				}
			}else {
				// if et does not require any resource then is a progressing transition
				progressingTransitions.add(et);
			}
		}
				
		// for each resource get the transition with highest priority
		for (Resource r: resourceMap.rowKeySet()) {
			Integer highestPriority = Integer.MAX_VALUE;
			for (Integer rp: resourceMap.row(r).keySet()) 
				if (rp < highestPriority) 
					highestPriority = rp;
			for(Transition hpt: resourceMap.get(r, highestPriority))
				progressingTransitions.add(hpt);
		}
		
		return progressingTransitions;
	}
	
	/**
	 * Moves markers and updates enabled transitions
	 * 
	 * @param t fired transition
	 * */
	private void updateEnabledTrasitions(Transition t) {
		Collection<Precondition> preconditions = pn.getPreconditions(t);
		for (Precondition precondition: preconditions) {
			Place p = precondition.getPlace();
			m.removeTokens(p, 1);
		}
		Collection<Postcondition> postconditions = pn.getPostconditions(t);
		for (Postcondition postcondition: postconditions) {
			Place p = postcondition.getPlace();
			m.addTokens(p, 1);
		}
	}
	
	/**
	 * Check if tau is a feasible time to fire for transition t
	 * 
	 * @param t transition
	 * @param tau time to fire of the log
	 * 
	 * @return true if tau is feasible for t. False if not
	 * */
	private boolean isTimeToFireFeasible(Transition t, int tau) {
		int eft = earliestFiringTimes.get(t);
		int lft = latestFiringTimes.get(t);
		
		// if t is a sporadic or periodic transition checks global time
		if(sporadicPeriodicTransitions.containsKey(t)) {
			int diff = globalTime - sporadicPeriodicTransitions.get(t);
			if (diff < eft || lft < diff) {
				System.out.println("The initial transition " + t.getName() + " is not feasible!");
				return false;
			}
			sporadicPeriodicTransitions.replace(t, globalTime);
			
		} else if(tau < eft || lft < tau) {
			System.out.println("Time to fire of transition " + t.getName() + " non feasible!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Acquire the list of sporadic or periodic transitions from the model
	 * */
	private void acquireSporadicPeriodicTransitions() {
		sporadicPeriodicTransitions = new HashMap<Transition, Integer>();
		for (Transition transition: pn.getTransitions()){
			if(pn.getPreconditions(transition).isEmpty()) {
				sporadicPeriodicTransitions.put(transition,  0);
			}
		}
	}
	
	/**
	 * Acquires firing times from the model for each transition
	 * */
	private void acquireFiringTimes() {
		earliestFiringTimes = new HashMap<Transition, Integer>();
		latestFiringTimes = new HashMap<Transition, Integer>();

		for (Transition t: pn.getTransitions()) {
			earliestFiringTimes.put(t, t.getFeature(TimedTransitionFeature.class).getEFT().intValue());
			latestFiringTimes.put(t, t.getFeature(TimedTransitionFeature.class).getLFT().intValue());
		}
	}
}

