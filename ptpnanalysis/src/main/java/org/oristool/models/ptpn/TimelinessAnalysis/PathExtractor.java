package org.oristool.models.ptpn.TimelinessAnalysis;

import java.util.HashSet;
import java.util.Set;
import org.oristool.analyzer.state.State;
import org.oristool.analyzer.Succession;
import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.analyzer.policy.EnumerationPolicy;
import org.oristool.petrinet.Transition;

public class PathExtractor{
	private EnumerationPolicy enumerationPolicy;
	
	public PathExtractor(EnumerationPolicy policy) {
		this.enumerationPolicy = policy;
	}
	
	public Set<Path> extract(SuccessionGraph stateClassGraph, Transition tFirst, Transition tLast){
		/*Set<Succession> initialStates =  transitionFinder.find(stateClassGraph, tFirst);
		for(Succession succession : initialStates) {
			Path tmpPath = new Path(succession.getParent());
			tmpPath.addNextTuple(succession.getChild(), (Transition)succession.getEvent());
			paths.add(tmpPath);
			enumerationPolicy.add(succession); 
		}
		
		while(!enumerationPolicy.isEmpty()) {
			Succession currentSuccession = enumerationPolicy.remove();
			Set<Succession> children = stateClassGraph.getOutgoingSuccessions(stateClassGraph.getNode(currentSuccession.getChild()));
			Set<Path> toBeAdded = new HashSet<>();
			if(children.size() > 1)
				System.out.println();
			//extract the paths that can be extended in this iteration of while loop
			Set<Path> selectedPaths = new HashSet<>(); //is a set of all the paths that contain the child as a leaf (they can be extended from child)
			for(Path path : paths) {
				State child = currentSuccession.getChild();
				if(path.hasState(child) && !path.hasChild(child)) {
					//path.addNextTuple(currentSuccession.getChild(), (Transition)currentSuccession.getEvent()); //TODO delete cast to Transition
					selectedPaths.add(path);
				}
			}
			int i = 0;
			for(Succession succession : children) {	
				i++;
				if(i>1)
					System.out.println(paths.size());
				for(Path path : selectedPaths) { //Those paths can be extended 
					if(succession.getEvent().toString().equals(tLast.toString())) {
						path.addNextTuple(succession.getChild(), tLast);
						//selectedPaths.remove(path);
						toBeAdded.add(path);
					}
					else {
						enumerationPolicy.add(succession);
						Path newPath = new Path(path);
						newPath.addNextTuple(succession.getChild(), (Transition)succession.getEvent());
						toBeAdded.add(newPath);
					}
				}
			}
			paths.removeAll(selectedPaths);
			paths.addAll(toBeAdded);
		}
		*/
		
		/* Previous version
		 * Set<Path> globalPaths = new HashSet<Path>();
		Set<Succession> graphSuccessions = stateClassGraph.getSuccessions();
		Set<State> pathRoots = new HashSet<State>();
		for(Succession succession : graphSuccessions) {
			if(succession.getEvent().equals(tFirst) && !pathRoots.contains(succession.getChild())) {
				Set<Path> innerPaths = new HashSet<Path>();
				for(Succession succ : stateClassGraph.getOutgoingSuccessions(stateClassGraph.getNode(succession.getChild()))) {
					if(!succ.getEvent().equals(tLast) && !succ.getEvent().equals(tFirst)) {
						Path tmpPath = new Path(succ.getParent(), tFirst, tLast);
						tmpPath.addNextTuple(succ.getChild(), (Transition)succ.getEvent());
						innerPaths.add(tmpPath);
						enumerationPolicy.add(succ);
					}
					else if(succ.getEvent().equals(tLast)) {
						Path tmpPath = new Path(succ.getParent(), tFirst, tLast);
						innerPaths.add(tmpPath);
					}
				}

				pathRoots.add(succession.getChild());
				
				//version with paths with tFirst 
				while(!enumerationPolicy.isEmpty()) {
					//System.out.println("First " + enumerationPolicy.);
					//System.out.println(globalPaths.size() + " " + innerPaths.size());
					Succession currentSuccession = enumerationPolicy.remove();
					if(!currentSuccession.getEvent().equals(tLast)) {
						Set<Path> selectedPaths = new HashSet<>(); //is a set of all the paths that contain the child as a leaf (they can be extended from child)
						for(Path path : innerPaths) {
							State child = currentSuccession.getChild();
							if(path.hasState(child) && !path.hasChild(child)) { 
								selectedPaths.add(path);
							}
						}
						if(innerPaths.size() > 800 && selectedPaths.size() > 1) {
							System.out.println();
						}
						for(Path selectedPath : selectedPaths) {
							innerPaths.remove(selectedPath);
							for(Succession childSuccession : stateClassGraph.getOutgoingSuccessions(stateClassGraph.getNode(currentSuccession.getChild()))) {
								Path newPath = new Path(selectedPath);
								if(!childSuccession.getEvent().equals(tLast)) {
									newPath.addNextTuple(childSuccession.getChild(), (Transition)childSuccession.getEvent());
									enumerationPolicy.add(childSuccession);
								}
								innerPaths.add(newPath);
								
							}
						}
					}
				}
				
				for(Path inner : innerPaths) {
					globalPaths.add(inner);
				}
			}
		}*/
		Set<Path> globalPaths = new HashSet<Path>();
		Set<Succession> graphSuccessions = stateClassGraph.getSuccessions();
		Set<State> pathRoots = new HashSet<State>();
		for(Succession succession : graphSuccessions) {
			if(succession.getEvent().equals(tFirst) && !pathRoots.contains(succession.getChild())) {
				Set<Path> innerPaths = new HashSet<Path>();
				for(Succession succ : stateClassGraph.getOutgoingSuccessions(stateClassGraph.getNode(succession.getChild()))) {
					if(!succ.getEvent().equals(tLast)) {
						Path tmpPath = new Path(succession.getParent(), tFirst, tLast);
						tmpPath.addNextTuple(succession.getChild(), tFirst);
						tmpPath.addNextTuple(succ.getChild(), (Transition)succ.getEvent());
						innerPaths.add(tmpPath);
						enumerationPolicy.add(succ);
					}
					else{
						Path tmpPath = new Path(succession.getParent(), tFirst, tLast);
						tmpPath.addNextTuple(succ.getParent(), tFirst);
						tmpPath.addNextTuple(succ.getChild(), tLast);
						innerPaths.add(tmpPath);
					}
				}

				if(innerPaths.size() > 0) {
					pathRoots.add(succession.getChild());
				}
				
				//version with paths with tFirst 
				while(!enumerationPolicy.isEmpty()) {
					Succession currentSuccession = enumerationPolicy.remove();
					Set<Path> selectedPaths = new HashSet<>(); //is a set of all the paths that contain the child as a leaf (they can be extended from child)
					State child = currentSuccession.getChild();
					for(Path path : innerPaths) {
						if(path.isLast(child)) { 
							selectedPaths.add(path);
						}
					}
					for(Path selectedPath : selectedPaths) {
						innerPaths.remove(selectedPath);
						for(Succession childSuccession : stateClassGraph.getOutgoingSuccessions(stateClassGraph.getNode(currentSuccession.getChild()))) {
							Path newPath = new Path(selectedPath);
							newPath.addNextTuple(childSuccession.getChild(), (Transition)childSuccession.getEvent());
							if(!childSuccession.getEvent().equals(tLast)) {
								enumerationPolicy.add(childSuccession);
							}
							innerPaths.add(newPath);
						}
					}
				}
				/*//version with paths without tFirst 
				while(!enumerationPolicy.isEmpty()) {
					Succession currentSuccession = enumerationPolicy.remove();
					if(!currentSuccession.getEvent().equals(tLast)) {
						Set<Path> selectedPaths = new HashSet<>(); //is a set of all the paths that contain the child as a leaf (they can be extended from child)
						State child = currentSuccession.getChild();
						for(Path path : innerPaths) {
							if(path.isLast(child)) { 
								selectedPaths.add(path);
							}
						}
						for(Path selectedPath : selectedPaths) {
							innerPaths.remove(selectedPath);
							if(!currentSuccession.getEvent().equals(tFirst)) {
								for(Succession childSuccession : stateClassGraph.getOutgoingSuccessions(stateClassGraph.getNode(currentSuccession.getChild()))) {
									Path newPath = new Path(selectedPath);
									if(!childSuccession.getEvent().equals(tLast) && !childSuccession.getEvent().equals(tFirst)) {
										newPath.addNextTuple(childSuccession.getChild(), (Transition)childSuccession.getEvent());
										enumerationPolicy.add(childSuccession);
									}
									else if(!childSuccession.getEvent().equals(tLast)) {
										newPath.addNextTuple(childSuccession.getChild(), (Transition)childSuccession.getEvent());
									}
									innerPaths.add(newPath);
								}
							} 
						}
					}
				}*/
				for(Path inner : innerPaths) {
					System.out.println(inner.printTrace());
					if(inner.isCompleted())
						globalPaths.add(inner);
				}
			}
		}
		return globalPaths;
	}
}