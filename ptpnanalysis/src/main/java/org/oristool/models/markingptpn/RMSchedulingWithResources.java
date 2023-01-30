package org.oristool.models.markingptpn;

import java.util.ArrayList;

import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.models.ptpn.PreemptiveAnalysis;
import org.oristool.models.ptpn.PreemptiveTransitionFeature;
import org.oristool.models.ptpn.TimelinessAnalysis.PreemptiveTimelinessAnalysis;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.petrinet.Transition;

public class RMSchedulingWithResources implements Scheduling {

	@Override
	public ArrayList<double[]> schedule(TaskSet taskSet, int minTimeT1, int minTimeT2) {
		PetriNet pn = new PetriNet();
		Resource cpu = new Resource("cpu");
		String minTimeString1 = Integer.toString(minTimeT1);
		String minTimeString2 = Integer.toString(minTimeT2);

		ArrayList<Task> tasks = taskSet.getTasks();

		ArrayList<Task> tasks1 = new ArrayList<>();
		for (Task t : tasks) {
			tasks1.add(t);
		}
		ArrayList<String> semaphores = new ArrayList<String>();

		Marking m = new Marking();

		int i = 0;

		while (!tasks.isEmpty()) {
			int min = 9999;
			int minId = 0;

			// Trova il task con la deadline più piccola
			for (Task t : tasks) {
				if (t.getDeadline() < min) {
					min = t.getDeadline();
					minId = t.getId();
				}
			}
			for (Task t : tasks) {
				if (t.getId() == minId) {
					Transition tmpTransition1 = pn.addTransition("transition" + i + "0");
					Place tmpPlace = pn.addPlace("place" + i + "0");
					Transition tmpTransition2 = pn.addTransition("transition" + i + "1");

					String periodString = Integer.toString(t.getPeriod());
					tmpTransition1.addFeature(new TimedTransitionFeature(periodString, periodString));
					pn.addPostcondition(tmpTransition1, tmpPlace);
					tmpTransition2.addFeature(new TimedTransitionFeature(minTimeString1, minTimeString2));
					tmpTransition2.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(i)));
					ArrayList<String> rArr = t.getResourceId();
					for (String s : rArr) {
						if (s != null) {
							if (!semaphores.contains(s)) {
								Place p = pn.addPlace(s);
								semaphores.add(s);
								m.setTokens(p, 1);
							}
							pn.addPrecondition(pn.getPlace(s), tmpTransition1);
							pn.addPostcondition(tmpTransition2, pn.getPlace(s));
						}
					}
					pn.addPrecondition(tmpPlace, tmpTransition2);

					i++;
					tasks.remove(t);
					break;
				}
			}
		}

		for (int c = 0; c < tasks1.size(); c++) {
			m.addTokens(pn.getPlace("place" + c + "0"), 0);
		}

		PreemptiveAnalysis analysis = PreemptiveAnalysis.builder().build();
		SuccessionGraph graph = analysis.compute(pn, m);

		PreemptiveTimelinessAnalysis analyzer = PreemptiveTimelinessAnalysis.builder().build();

		ArrayList<double[]> bounds = new ArrayList<double[]>();

		for (int c = 0; c < tasks1.size(); c++) {
			bounds.add(analyzer.compute(pn, graph,
					pn.getTransition("transition" + c + "0"), pn.getTransition("transition" + c + "1")));
		}

		int c = 0;
		while (!tasks1.isEmpty()) {
			int min = 9999;
			int minId = 0;

			// Serve per allineare i task all'array bounds, visto che esso è calcolato
			// secondo le
			// transizioni che a loro volta sono ordinate secondo le deadline
			for (Task t : tasks1) {
				if (t.getDeadline() < min) {
					min = t.getDeadline();
					minId = t.getId();
				}
			}
			for (Task t : tasks1) {
				if (t.getId() == minId) {
					System.out.println();
					System.out.println("Task: " + c);
					System.out.println("La deadline è: " + t.getDeadline());
					System.out.println("Il minimo tempo di completamento è: " + bounds.get(c)[0]);
					System.out.println("Il massimo tempo di completamento è: " + bounds.get(c)[1]);
					c++;
					tasks1.remove(t);
					break;
				}
			}
		}
		return bounds;
	}
}