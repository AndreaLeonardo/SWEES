package org.oristool.models.markingptpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.Transition;
import org.oristool.models.ptpn.TimelinessAnalysis.PreemptiveTimelinessAnalysis;
import org.oristool.analyzer.graph.SuccessionGraph;

public class EDFSchedulingWithResources implements Scheduling{
	Map<Integer, ArrayList<Integer>> transitionPlacesDict = new HashMap<Integer, ArrayList<Integer>>();
	
	@Override
	public ArrayList<double[]> schedule(TaskSet taskSet, int minTimeT1, int minTimeT2) {
		ArrayList<Integer> times = priorityTimeCalculator(taskSet.getTasks());
		ArrayList<String> semaphores = new ArrayList<String>();
		String minTimeString1 = Integer.toString(minTimeT1);
		String minTimeString2 = Integer.toString(minTimeT2);
		
		PetriNet pn = new PetriNet();
		Resource cpu = new Resource("cpu");
		
		// Crea la sottorete di petri del tempo
		int i = 0;
		for(int t : times) {
			Transition tmpT = pn.addTransition("timeTransition"+i);
			String str = Integer.toString(t);
			tmpT.addFeature(new TimedTransitionFeature(str, str));
			Place tmpP = pn.addPlace("timePlace"+i);
			pn.addPostcondition(tmpT, tmpP);
			if(i == times.size()-1) {
				pn.addPrecondition(tmpP, pn.getTransition("timeTransition0"));
			}
			if (i != 0){
				String tmp = Integer.toString(i-1);
				pn.addPrecondition(pn.getPlace("timePlace"+tmp), tmpT);
			}
			i++;
		}
		int n = pn.getPlaces().size();
		
		Marking m = new Marking();
		// Crea la sottorete dei task
		i = 0;
		for (Task t : taskSet.getTasks()) {
			Transition tmpTransition1 = pn.addTransition("transition"+i+"0");
			Place tmpPlace = pn.addPlace("place"+i+"0");
			String periodString = Integer.toString(t.getPeriod());
			tmpTransition1.addFeature(new TimedTransitionFeature(periodString, periodString));
			pn.addPostcondition(tmpTransition1, tmpPlace);
			Transition tmpTransition2 = pn.addTransition("transition"+i+"1");
			tmpTransition2.addFeature(new TimedTransitionFeature(minTimeString1, minTimeString2));
			ArrayList<String> rArr = t.getResourceId();
			for (String s : rArr) {
				if(s != null){
					if(!semaphores.contains(s)){
						Place p = pn.addPlace(s);
						semaphores.add(s);
						m.setTokens(p, 1);
					}
					pn.addPrecondition(pn.getPlace(s), tmpTransition1);
					pn.addPostcondition(tmpTransition2, pn.getPlace(s));
				}
			}
			// Stringa dei place
			String priority = "";
			
			// Estrae i places trovati in time calculator
			ArrayList<Integer> places = transitionPlacesDict.get(t.getId());
			
			// I Place vengono usati per creare la stringa della MarkingExpr
			for(int j=0; j<n; j++) {
				boolean found = false;
				if(places != null) {
					for (int k=0; k<places.size(); k++) {
						if(j == places.get(k)) {
							priority += ("timePlace"+j+"*0");
							found = true;
							break;
						}
					}
					if (!found) {
						priority += ("timePlace"+j+"*99");
					}
					
				}else {
					priority += ("timePlace"+j+"*99");
				}
				if(j!=n-1) {
					priority += "+";
				}
			}
			
			System.out.println(tmpTransition2.getName());
			System.out.println(priority);
			System.out.println();
			
			tmpTransition2.addFeature(new MarkingPreemptiveTransitionFeature(cpu, 
					new MarkingResourcePriority(99, priority, pn)));
			pn.addPrecondition(tmpPlace, tmpTransition2);
			i++;
		}
		
		
		m.addTokens(pn.getPlace("timePlace0"), 1);
		
		
		MarkingPreemptiveAnalysis analysis = new MarkingPreemptiveAnalysis();	
		SuccessionGraph graph = analysis.compute(pn, m);
		
		PreemptiveTimelinessAnalysis analyzer = PreemptiveTimelinessAnalysis.builder().build();
		int c;
		ArrayList<double[]> bounds = new ArrayList<double[]>();
		int nTasks = taskSet.getTasks().size();
		for (c = 0; c < nTasks; c++) {
			bounds.add(analyzer.compute(pn, graph, 
					pn.getTransition("transition"+c+"0"), pn.getTransition("transition"+c+"1")));
		}
		c = 0;
		for (Task t : taskSet.getTasks()) {
			System.out.println();
			System.out.println("Task: "+ c);
			System.out.println("La deadline è: "+ t.getDeadline());
			System.out.println("Il minimo tempo di completamento è: "+bounds.get(c)[0]);
			System.out.println("Il massimo tempo di completamento è: "+bounds.get(c)[1]);
			c++;
		}
		
		return bounds;
	}
	
	private ArrayList<Integer> priorityTimeCalculator(ArrayList<Task> tasks) {
		int mcm=1;
		for (Task t : tasks) {
			mcm = ArithmeticUtils.lcm(mcm, t.getPeriod());	
		}
		System.out.println("MCM: "+mcm);
		ArrayList<Integer> times = new ArrayList<Integer>();
		int prev = 0;
		int min;
		int chosen = 0;
		int sum = 0;
		int i = 0;
		
		while(true) {
			min = 9999;
			for (Task t : tasks) {
				if(calculateTime(t) - prev < min && calculateTime(t) - prev > 0) {
					min = calculateTime(t) - prev;
					chosen = t.getId();
				}
				if(calculateTime2(t) - prev < min){
					min = calculateTime2(t) - prev;
					chosen = t.getId();
				}
			}
			sum += min;
			if (sum > mcm)
				break;
			for (Task t : tasks) {
				if(t.getId() == chosen) {
					ArrayList<Integer> places;
					if(transitionPlacesDict.containsKey(t.getId())) {
						places = transitionPlacesDict.get(t.getId());
					}else {
						places = new ArrayList<Integer>();
					}
					places.add(i);
					transitionPlacesDict.put(t.getId(), places);
					t.counterUp();
					break;
				}
			}
			prev += min;
			times.add(min);
			i++;
		}
		System.out.println(times);
		
		return times;
	}
	
	private int calculateTime(Task t) {
		return (t.getCounter()*t.getPeriod() + t.getDeadline());
	}
	
	private int calculateTime2(Task t) {
		return ((t.getCounter()+1)*t.getPeriod() + t.getDeadline());
	}
}
