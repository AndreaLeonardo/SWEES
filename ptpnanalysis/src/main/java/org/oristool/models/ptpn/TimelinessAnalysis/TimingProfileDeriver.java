package org.oristool.models.ptpn.TimelinessAnalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.oristool.analyzer.state.State;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.expression.Variable;
import org.oristool.models.pn.PetriStateFeature;
import org.oristool.models.ptpn.PreemptiveStateFeature;
import org.oristool.models.tpn.TimedStateFeature;
import org.oristool.petrinet.Transition;

public class TimingProfileDeriver{
	public enum TransState {progressing, progressingNE, suspended, suspendedNE, none};
	public GlobalSet derive(Path path) {
		State firstState = path.getPath().entrySet().iterator().next().getKey(); // it is not the root but the state reached by tFirst
		Set<Transition> totalTransitions = path.getEnabledTransitionPath(); //all the transitions appeared trough the path
		int ineqLength = path.length() - 2; //Number of variables (number of states in the path without the root and the last state)
		
		//init presence vector
		Map<Transition, TransState[]> presence = new HashMap<>();
		Map<Transition, OmegaBigDecimal[]> leqCoefficients = new HashMap<Transition, OmegaBigDecimal[]>();
		Map<Transition, OmegaBigDecimal[]> geqCoefficients = new HashMap<Transition, OmegaBigDecimal[]>();
		
		for(Transition t : totalTransitions) {
			TransState[] m = new TransState[ineqLength]; 
			OmegaBigDecimal[] cl = new OmegaBigDecimal[ineqLength];
			OmegaBigDecimal[] cg = new OmegaBigDecimal[ineqLength];
			for(int i = 0; i < ineqLength; i++) {
				m[i] = TransState.none;
				cl[i] = OmegaBigDecimal.ZERO;
				cg[i] = OmegaBigDecimal.ZERO;
			}
			presence.put(t, m);
			leqCoefficients.put(t, cl);
			geqCoefficients.put(t, cg);
		}
		
		
		
		Set<Transition> firstStateProgTransitions = firstState.getFeature(PreemptiveStateFeature.class).getProgressing();
		Set<Transition> firstStateSuspTransitions = firstState.getFeature(PreemptiveStateFeature.class).getSudpended();
		System.out.println(firstStateProgTransitions + "  " + firstStateSuspTransitions);
		System.out.println(firstState);
//		for(Transition trans : firstStateProgTransitions) {
//			if(firstState.getFeature(PetriStateFeature.class).getNewlyEnabled().contains(trans)) {
//				presence.get(trans)[0] = TransState.progressingNE;	//is progressing but newly enabled
//			}
//			else {
//				presence.get(trans)[0] = TransState.progressing;	//is progressing
//			}
//			leqCoefficients.get(trans)[0] = firstState.getFeature(TimedStateFeature.class).getDomain().
//					getBound(trans.newVariableInstance(), Variable.TSTAR);
//			geqCoefficients.get(trans)[0] = firstState.getFeature(TimedStateFeature.class).getDomain().
//					getBound(Variable.TSTAR, trans.newVariableInstance());
//		}
//		
//		for(Transition trans : firstStateSuspTransitions) {
//			if(firstState.getFeature(PetriStateFeature.class).getNewlyEnabled().contains(trans)) {
//				presence.get(trans)[0] = TransState.suspendedNE;	//is suspended but newly enabled
//			}
//			else {
//				presence.get(trans)[0] = TransState.suspended;	//is suspended
//			}
//			leqCoefficients.get(trans)[0] = firstState.getFeature(TimedStateFeature.class).getDomain().
//					getBound(trans.newVariableInstance(), Variable.TSTAR);
//			geqCoefficients.get(trans)[0] = firstState.getFeature(TimedStateFeature.class).getDomain().
//					getBound(Variable.TSTAR, trans.newVariableInstance());
//		}
		
		
		Map<State, Transition> successionPath = path.getPath();
		int iteration = 0;
		if(successionPath.size() > 0) {
			Iterator<Entry<State, Transition>> iterator = successionPath.entrySet().iterator();
			Entry<State, Transition> currentElement = null;

//			Entry<State, Transition> currentElement = iterator.next();
			while(iterator.hasNext() && iteration < ineqLength) {
				currentElement = iterator.next();
				Set<Transition> progressing = currentElement.getKey().getFeature(PreemptiveStateFeature.class).getProgressing();
				Set<Transition> suspended = currentElement.getKey().getFeature(PreemptiveStateFeature.class).getSudpended();
				for(Transition trans : progressing) {
					if(currentElement.getKey().getFeature(PetriStateFeature.class).getNewlyEnabled().contains(trans)) {
						presence.get(trans)[iteration] = TransState.progressingNE;	//is progressing but newly enabled
						leqCoefficients.get(trans)[iteration] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
								getBound(trans.newVariableInstance(), Variable.TSTAR);
						geqCoefficients.get(trans)[iteration] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
								getBound(Variable.TSTAR, trans.newVariableInstance());
					}
					else {
						presence.get(trans)[iteration] = TransState.progressing;	//is progressing
						if(iteration == 0) {
							leqCoefficients.get(trans)[0] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
									getBound(trans.newVariableInstance(), Variable.TSTAR);
							geqCoefficients.get(trans)[0] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
									getBound(Variable.TSTAR, trans.newVariableInstance());
						}
					}
				}
				
				for(Transition trans : suspended) {
					if(currentElement.getKey().getFeature(PetriStateFeature.class).getNewlyEnabled().contains(trans)) {
						presence.get(trans)[iteration] = TransState.suspendedNE;	//is suspended but newly enabled
						leqCoefficients.get(trans)[iteration] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
								getBound(trans.newVariableInstance(), Variable.TSTAR);
						geqCoefficients.get(trans)[iteration] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
								getBound(Variable.TSTAR, trans.newVariableInstance());
					}
					else {
						presence.get(trans)[iteration] = TransState.suspended;	//is suspended
						if(iteration == 0) {
							leqCoefficients.get(trans)[0] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
									getBound(trans.newVariableInstance(), Variable.TSTAR);
							geqCoefficients.get(trans)[0] = currentElement.getKey().getFeature(TimedStateFeature.class).getDomain().
									getBound(Variable.TSTAR, trans.newVariableInstance());
						}
					}
				}
				iteration++;
			}
		}
//		//from presence matrix to global set inequalities
//		GlobalSet optimizableMatrix = new GlobalSet(ineqLength);

		Set<Transition> lastSuspended = new HashSet<Transition>();
		GlobalSet optimizableMatrix = new GlobalSet(ineqLength);
		
		Iterator<Entry<Transition, TransState[]>> iterator = presence.entrySet().iterator();
		Entry<Transition, TransState[]> currentElement = null;
		Map<Transition, int[]> firstStateTransitionVector = new HashMap<>();
		while(iterator.hasNext()) {
			
			int[] currentIneq = newZeroVect(ineqLength);
			int[] currentNegativeIneq = newZeroVect(ineqLength);
			OmegaBigDecimal currentCoefficient = new OmegaBigDecimal(0);
			OmegaBigDecimal currentNegativeCoefficient = new OmegaBigDecimal(0);
			
			currentElement = iterator.next();
			boolean isPresentLast = false;
			boolean isActive = false;
			TransState[] matrix = currentElement.getValue();
			//check the value of the last state
			if(matrix[ineqLength - 1] != TransState.none) {
				if(!currentElement.getKey().toString().equals(path.gettLast().toString()))
					isPresentLast = true;
				isActive = true;
			}
			boolean lastIdx = true;
			//currentCoefficient = currentElement.getKey().getFeature(TimedTransitionFeature.class).getLFT();
			//currentNegativeCoefficient = currentElement.getKey().getFeature(TimedTransitionFeature.class).getEFT().negate();
			
			for(int i = ineqLength - 1; i >= 0; i--) { //TODO change to switch
				if(matrix[i] == TransState.progressing) {
					if(lastIdx == true)
						lastIdx = false;
					if(!isPresentLast)
						currentNegativeIneq[i] = -1;
					currentIneq[i] = 1;
					isActive = true;
				}
				else if(matrix[i] == TransState.suspended) {
					if(lastIdx == true) {
						lastSuspended.add(currentElement.getKey());
						lastIdx = false;
					}
					if(!isPresentLast)
						currentNegativeIneq[i] = 0;
					currentIneq[i] = 0;
					isActive = true;
				}
				else if(matrix[i] == TransState.progressingNE) {
					if(lastIdx == true)
						lastIdx = false;
					if(!isPresentLast) {
						currentNegativeIneq[i] = -1;
						currentNegativeCoefficient = geqCoefficients.get(currentElement.getKey())[i];
						optimizableMatrix.addInequality(currentNegativeIneq, (currentNegativeCoefficient));
						currentNegativeIneq = newZeroVect(ineqLength);
					}
					currentIneq[i] = 1;
					currentCoefficient = leqCoefficients.get(currentElement.getKey())[i];
					optimizableMatrix.addInequality(currentIneq, currentCoefficient);
					if(i == 0 && (firstStateProgTransitions.contains(currentElement.getKey()) || firstStateSuspTransitions.contains(currentElement.getKey())))
						firstStateTransitionVector.put(currentElement.getKey(), currentIneq);
					currentIneq = newZeroVect(ineqLength);
					isPresentLast = false;
					isActive = false;
				}
				else if(matrix[i] == TransState.suspendedNE) {
					if(lastIdx == true) {
						lastSuspended.add(currentElement.getKey());
						lastIdx = false;
					}
					if(!isPresentLast) {
						currentNegativeIneq[i] = 0;
						currentNegativeCoefficient = geqCoefficients.get(currentElement.getKey())[i];
						optimizableMatrix.addInequality(currentNegativeIneq, (currentNegativeCoefficient));
						currentNegativeIneq = newZeroVect(ineqLength);
					}
					currentIneq[i] = 0;
					currentCoefficient = leqCoefficients.get(currentElement.getKey())[i];
					optimizableMatrix.addInequality(currentIneq, currentCoefficient);
					if(i == 0 && (firstStateProgTransitions.contains(currentElement.getKey()) || firstStateSuspTransitions.contains(currentElement.getKey())))
						firstStateTransitionVector.put(currentElement.getKey(), currentIneq);
					currentIneq = newZeroVect(ineqLength);
					isPresentLast = false;
					isActive = false;
				}
			}
			//if the first state had to save inequalities (i don't which is the last element with iterators)
			if(isActive) {
				if(!isPresentLast) {
					currentNegativeCoefficient = geqCoefficients.get(currentElement.getKey())[0];
					optimizableMatrix.addInequality(currentNegativeIneq, currentNegativeCoefficient);
				}
				currentCoefficient = leqCoefficients.get(currentElement.getKey())[0];
				optimizableMatrix.addInequality(currentIneq, currentCoefficient);
				if(firstStateProgTransitions.contains(currentElement.getKey()) || firstStateSuspTransitions.contains(currentElement.getKey()))
					firstStateTransitionVector.put(currentElement.getKey(), currentIneq);
			}
		}
		if(lastSuspended.size() > 0)
			System.out.println();
		//cross constraints for first state
		Iterator<Entry<Transition, int[]>> firstStateIterator = firstStateTransitionVector.entrySet().iterator();
		Entry<Transition, int[]> currentFirstStateElement = null;
		
		while(firstStateIterator.hasNext()) {
			currentFirstStateElement = firstStateIterator.next();
			Iterator<Entry<Transition, int[]>> firstStateIteratorInternal = firstStateTransitionVector.entrySet().iterator();
			Entry<Transition, int[]> currentFirstStateElementInternal = null;
			
			if((currentFirstStateElement.getValue()[ineqLength - 1] == 0 && !lastSuspended.contains(currentFirstStateElement.getKey())) 
					|| currentFirstStateElement.getKey().equals(path.gettLast())) {
				while(firstStateIteratorInternal.hasNext()) { 
					currentFirstStateElementInternal = firstStateIteratorInternal.next();
					if(!currentFirstStateElement.getKey().equals(currentFirstStateElementInternal.getKey())) {
						if(currentFirstStateElementInternal.getValue()[ineqLength - 1] == 0 && !lastSuspended.contains(currentFirstStateElementInternal.getKey()) ||
								currentFirstStateElementInternal.getKey().equals(path.gettLast())) {
							OmegaBigDecimal coeff = firstState.getFeature(TimedStateFeature.class).getDomain().
									getBound(currentFirstStateElement.getKey().newVariableInstance(), 
											currentFirstStateElementInternal.getKey().newVariableInstance());

							int[] vec1 = currentFirstStateElement.getValue();
							int[] vec2 = currentFirstStateElementInternal.getValue();
							int[] innerIneq = newZeroVect(ineqLength);
							for(int i = 0; i < ineqLength; i++) {
								innerIneq[i] = vec1[i] - vec2[i];
							}
							
							if(checkZeroVec(innerIneq, coeff)) {
								optimizableMatrix.addInequality(innerIneq, coeff);
							}
						}
						else {
							OmegaBigDecimal coeff = firstState.getFeature(TimedStateFeature.class).getDomain().
									getBound(currentFirstStateElement.getKey().newVariableInstance(), 
											Variable.TSTAR);
							
							int[] vec1 = currentFirstStateElement.getValue();
							int[] vec2 = currentFirstStateElementInternal.getValue();
							int[] innerIneq = newZeroVect(ineqLength);
							for(int i = 0; i < ineqLength; i++) {
								innerIneq[i] = vec1[i] - vec2[i];
							}
							
							if(checkZeroVec(innerIneq, coeff)) {
								optimizableMatrix.addInequality(innerIneq, coeff);
							}
						}
						
					}
				}
			}
			else {
				while(firstStateIteratorInternal.hasNext()) { 
					currentFirstStateElementInternal = firstStateIteratorInternal.next();
//					if(!currentFirstStateElement.getKey().equals(currentFirstStateElementInternal.getKey()) && !currentFirstStateElementInternal.getKey().equals(path.gettFirst())) {
					if(!currentFirstStateElement.getKey().equals(currentFirstStateElementInternal.getKey())) {
						if(currentFirstStateElementInternal.getValue()[ineqLength - 1] == 0  && !lastSuspended.contains(currentFirstStateElementInternal.getKey()) || 
								currentFirstStateElementInternal.getKey().equals(path.gettLast())) {
							OmegaBigDecimal coeff = firstState.getFeature(TimedStateFeature.class).getDomain().
									getBound(currentFirstStateElement.getKey().newVariableInstance(), 
											currentFirstStateElementInternal.getKey().newVariableInstance());
							
							int[] vec1 = currentFirstStateElement.getValue();
							int[] vec2 = currentFirstStateElementInternal.getValue();
							int[] innerIneq = newZeroVect(ineqLength);
							for(int i = 0; i < ineqLength; i++) {
								innerIneq[i] = vec1[i] - vec2[i];
							}
							
							if(checkZeroVec(innerIneq, coeff)) {
								optimizableMatrix.addInequality(innerIneq, coeff);
							}
						}
					}
				}
			}
		}
		optimizableMatrix.allPositiveVars();
		
		return optimizableMatrix;
	}
	
	private int[] newZeroVect(int size) {
		int[] m = new int[size];
		for(int i = 0; i < size; i++) {
			m[i] = 0;
		}
		return m;
	}
	
	private boolean checkZeroVec(int[] value, OmegaBigDecimal coefficent) {
		boolean allZero = true;
		for(int val : value) {
			if(val != 0)
				allZero = false;
		}
		if(!coefficent.equals(OmegaBigDecimal.ZERO))
			allZero = false;
		return !allZero;
	}
}
