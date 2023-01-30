package org.oristool.models.markingptpn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.petrinet.TransitionFeature;
import org.oristool.util.Pair;

public class MarkingPreemptiveTransitionFeature implements TransitionFeature{
private Map<Resource, MarkingResourcePriority> transitionResourcePriority;
	
	public MarkingPreemptiveTransitionFeature(){
		this(Collections.<Resource, MarkingResourcePriority>emptyMap());
	}
	
	public MarkingPreemptiveTransitionFeature(MarkingPreemptiveTransitionFeature ptf){
		this(ptf.getMap());
	}
	
	public MarkingPreemptiveTransitionFeature(Map<Resource, MarkingResourcePriority> other){
		transitionResourcePriority = new HashMap<Resource, MarkingResourcePriority>(other);
	}
	
	public MarkingPreemptiveTransitionFeature(Resource r, MarkingResourcePriority p){
		this();
		addResourcePriority(r, p);
	}
	
	@SafeVarargs
	public MarkingPreemptiveTransitionFeature(Pair<Resource, MarkingResourcePriority>...pairs){
		this();
		for(Pair<Resource, MarkingResourcePriority> pair : pairs) {
			this.addResourcePriority(pair.first(), pair.second());
		}
	}
	
	public Map<Resource, MarkingResourcePriority> getMap() {
		return transitionResourcePriority;
	}
	
	public void addResourcePriority(Resource r, MarkingResourcePriority p) {
		ResourcePriority resPrio = transitionResourcePriority.get(r);
		if(resPrio == null)
			resPrio = transitionResourcePriority.put(r,  p);
		else
			throw new IllegalArgumentException("There is already a pair having this resource");
	}
	
	public void setResourcePriority(Resource r, MarkingResourcePriority p) {
		//transitionResourcePriority.put(r, p);
		ResourcePriority resPrio = transitionResourcePriority.get(r);
		if(resPrio != null)
			resPrio = transitionResourcePriority.put(r, p);
		else
			throw new IllegalArgumentException("There isn't any pair having this resource");
	}
	
	public void removeResourcePriority(Resource r) {
		//transitionResourcePriority.remove(r);
		ResourcePriority resPrio = transitionResourcePriority.get(r);
		if(resPrio != null)
			transitionResourcePriority.remove(r);
		else
			throw new IllegalArgumentException("There isn't any pair having this resource");
	}
	
	@Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("-- PreemptiveTransitionFeature --\n");
        
        for (Map.Entry<Resource, MarkingResourcePriority> pair : transitionResourcePriority.entrySet()) {
    		b.append(pair.getKey().toString());
    		b.append("; ");
    		b.append(pair.getValue().toString());
    		b.append("\n");
        }
        b.append("\n");
        return b.toString();
    }
	
	public static void main(String[] args) {
		MarkingPreemptiveTransitionFeature testMPTF;
		Resource r;
		PetriNet pn;
		
		pn = new PetriNet();
		pn.addPlace("p");
		r = new Resource("r");
		testMPTF = new MarkingPreemptiveTransitionFeature(r, new MarkingResourcePriority(1, "p", pn));
		for (Map.Entry<Resource, MarkingResourcePriority> pair : testMPTF.getMap().entrySet()) {
			 System.out.println(pair.getKey().toString());
			 System.out.println("Resource: r");
			 System.out.println(pair.getValue().toString());
		 }
		testMPTF.setResourcePriority(r, new MarkingResourcePriority(2, "p", pn));
		for (Map.Entry<Resource, MarkingResourcePriority> pair : testMPTF.getMap().entrySet()) {
			 System.out.println(pair.getKey().toString());
			 System.out.println("Resource: r");
			 System.out.println(pair.getValue().toString());
		 }
	}
}