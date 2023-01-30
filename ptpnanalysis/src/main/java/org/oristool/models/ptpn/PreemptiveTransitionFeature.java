package org.oristool.models.ptpn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.petrinet.TransitionFeature;
import org.oristool.util.Pair;


public class PreemptiveTransitionFeature implements TransitionFeature{
	private Map<Resource, ResourcePriority> transitionResourcePriority;
	
	public PreemptiveTransitionFeature(){
		this(Collections.<Resource, ResourcePriority>emptyMap());
	}
	
	public PreemptiveTransitionFeature(PreemptiveTransitionFeature ptf){
		this(ptf.getMap());
	}
	
	public PreemptiveTransitionFeature(Map<Resource, ResourcePriority> other){
		transitionResourcePriority = new HashMap<Resource, ResourcePriority>(other);
	}
	
	public PreemptiveTransitionFeature(Resource r, ResourcePriority p){
		this();
		addResourcePriority(r, p);
	}
	
	@SafeVarargs
	public PreemptiveTransitionFeature(Pair<Resource, ResourcePriority>...pairs){
		this();
		for(Pair<Resource, ResourcePriority> pair : pairs) {
			this.addResourcePriority(pair.first(), pair.second());
		}
	}
	
	public Map<Resource, ResourcePriority> getMap() {
		return transitionResourcePriority;
	}
	
	public void addResourcePriority(Resource r, ResourcePriority p) {
		ResourcePriority resPrio = transitionResourcePriority.get(r);
		if(resPrio == null)
			resPrio = transitionResourcePriority.put(r,  p);
		else
			throw new IllegalArgumentException("There is already a pair having this resource");
	}
	
	public void setResourcePriority(Resource r, ResourcePriority p) {
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
        
        for (Map.Entry<Resource, ResourcePriority> pair : transitionResourcePriority.entrySet()) {
    		b.append(pair.getKey().toString());
    		b.append("; ");
    		b.append(pair.getValue().toString());
    		b.append("\n");
        }
        b.append("\n");
        return b.toString();
    }
}
