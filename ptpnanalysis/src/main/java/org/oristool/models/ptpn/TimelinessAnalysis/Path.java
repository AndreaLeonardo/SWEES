package org.oristool.models.ptpn.TimelinessAnalysis;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.oristool.petrinet.Transition;
import org.oristool.util.Pair;
import org.oristool.analyzer.Succession;
import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.analyzer.state.State;
import org.oristool.models.pn.PetriStateFeature;
import org.oristool.models.stpn.trees.SuccessionGraphViewer;

public class Path{
	private State root;
	private Map<State, Transition> successionMap = new LinkedHashMap<State, Transition>(); // State is reached by Transition
	private Transition tFirst;
	private Transition tLast; 
	private Pair<State, Transition> lastAdded = null;
	
	
	public Path(State root, Transition tFirst, Transition tLast) {
		this.root = root;
		this.tFirst = tFirst;
		this.tLast = tLast;
	}

	public Path(Path path) {
		this.root = path.getRoot();
		this.successionMap = path.getPath();
		this.tFirst = path.gettFirst();
		this.tLast = path.gettLast();
		this.lastAdded = path.getLastTuple();
	}
	
	public Pair<State, Transition> getLastTuple() {
		return lastAdded;
	}
	
	public State getRoot() {
		return root;
	}

	public void setRoot(State root) {
		this.root = root;
	}
	
	public boolean addNextTuple(State s, Transition t) {
		if(!successionMap.containsKey(s)) {
			successionMap.put(s, t);
			lastAdded = Pair.of(s, t);
			return true;
		}
		return false;
	}
	
	public boolean removeTuple(State s, Transition t) {
		if(successionMap.containsKey(s)) {
			successionMap.remove(s, t);
			if(lastAdded.first().equals(s) && lastAdded.second().equals(t)) {
				for(Map.Entry<State, Transition> entry : successionMap.entrySet()) {
					lastAdded = Pair.of(entry.getKey(), entry.getValue());
				}
				/*
				Iterator<Entry<State, Transition>> iterator = successionMap.entrySet().iterator();
				Entry<State, Transition> lastElement = iterator.next();
				while(iterator.hasNext()) {
					lastElement = iterator.next();
				}
				lastAdded = Pair.of(lastElement.getKey(), lastElement.getValue());*/
			}
			return true;
		}
		return false;
	}
	
	public Pair<State, Transition> getTuple(State s) {
		if(successionMap.containsKey(s)) {
			return Pair.of(s,successionMap.get(s));
		}
		return null;
	}
	
//	public boolean hasState(State s) {
//		return successionMap.containsKey(s) || s.equals(root);
//	}
	
	public boolean isLast(State s) {
		if(lastAdded.first().equals(s)) 
			return true;
		if(successionMap.size() == 0 && s.equals(root))
			return true;
		return false;
	}
	/*
	 * Checks if State s has a child
	 * 
	public boolean hasChild(State s) {
		boolean found = false;
		for(Map.Entry<State, Transition> entry : successionMap.entrySet()) {
			found = true;
			if(entry.getKey().equals(s)) {
				found = false;
			}
		}
		return found;
	}*/
	
	public int length() {
		return successionMap.size() + 1;
	}
	
	public Map<State, Transition> getPath() {
		return new LinkedHashMap<State, Transition>(successionMap);
	}
	
	public Set<Transition> getPathTransitions(){
		Set<Transition> transitions = new HashSet<Transition>();
		for(Map.Entry<State, Transition> entry : successionMap.entrySet()) {
			transitions.add(entry.getValue());
		}
		return transitions;
	}
	
	public Set<Transition> getEnabledTransitionPath(){
		Set<Transition> transitions = new HashSet<Transition>();
		transitions.addAll(root.getFeature(PetriStateFeature.class).getEnabled());
		for(Map.Entry<State, Transition> entry : successionMap.entrySet()) {
			transitions.addAll(entry.getKey().getFeature(PetriStateFeature.class).getEnabled());
		}
		transitions.addAll(getPathTransitions());
		return transitions;
	}
	
	public boolean isCompleted() {
		return lastAdded.second().equals(tLast);
	}
	public String toString() {
		/*String ret = new String();
		ret += "Root" + root.toString() + " \n";
		ret += successionMap.toString();*/
		return printTrace();		
    }
	
	public String printTrace() {
		String s = new String(" ");
		
		for(Map.Entry<State, Transition> entry : successionMap.entrySet()) {
			s += entry.getValue().toString() + "  ";
		}
		
		return s;
	}
	
	public void printPath() {
		SuccessionGraph graph = new SuccessionGraph();
		State previous = root;
		graph.addSuccession(new Succession(null, null, root));
		for(Map.Entry<State, Transition> entry : successionMap.entrySet()) {
			Succession succ = new Succession(previous, entry.getValue(), entry.getKey());
			previous = entry.getKey();
			graph.addSuccession(succ);
		}
		SuccessionGraphViewer.show(graph);
	}

	public Transition gettFirst() {
		return tFirst;
	}

	public Transition gettLast() {
		return tLast;
	}
}