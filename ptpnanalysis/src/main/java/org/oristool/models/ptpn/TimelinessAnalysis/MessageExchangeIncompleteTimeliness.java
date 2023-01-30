package org.oristool.models.ptpn.TimelinessAnalysis;

import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.models.ptpn.PreemptiveAnalysis;
import org.oristool.models.ptpn.PreemptiveTransitionFeature;
import org.oristool.models.stpn.trees.SuccessionGraphViewer;
import org.oristool.models.tpn.TimedTransitionFeature;


public class MessageExchangeIncompleteTimeliness{
	public static void main(String[] args) {
		Resource cpu = new Resource("cpu");
		
		PetriNet pn = new PetriNet();
		
		Transition t0 = pn.addTransition("t0");
		t0.addFeature(new TimedTransitionFeature("40","40"));
		
        Place p0 = pn.addPlace("p0");
        
        Transition t1 = pn.addTransition("t1");
		t1.addFeature(new TimedTransitionFeature("0","0"));
        t1.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(1)));
        
        Place p1 = pn.addPlace("p1");
        
        Transition t2 = pn.addTransition("t2");
		t2.addFeature(new TimedTransitionFeature("5","10"));
        t2.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(1)));
        
        
        Transition t3 = pn.addTransition("t3");
		t3.addFeature(new TimedTransitionFeature("40","40"));
		
        Place p2 = pn.addPlace("p2");
        
        Transition t4 = pn.addTransition("t4");
		t4.addFeature(new TimedTransitionFeature("1","2"));
		t4.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(2)));
        
        Place p3 = pn.addPlace("p3");
        
        Transition t5 = pn.addTransition("t5");
		t5.addFeature(new TimedTransitionFeature("1","2"));
		t5.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(2)));
       
        Place p4 = pn.addPlace("p4");
        
        
        pn.addPostcondition(t0, p0);
        pn.addPrecondition(p0, t1);
        pn.addPrecondition(p4, t1);
        pn.addPostcondition(t1, p1);
        pn.addPrecondition(p1, t2);
        
        pn.addPostcondition(t3, p2);
        pn.addPrecondition(p2, t4);
        pn.addPostcondition(t4, p3);
        pn.addPostcondition(t4, p4);
        pn.addPrecondition(p3, t5);
        
        Marking m = new Marking();
        
        PreemptiveAnalysis preeAnalysis = PreemptiveAnalysis.builder().build();
        SuccessionGraph graph = preeAnalysis.compute(pn, m);
        SuccessionGraphViewer.show(graph);
        PreemptiveTimelinessAnalysis analysis = PreemptiveTimelinessAnalysis.builder().build();
        analysis.compute(pn, graph, t0, t5);
	}
}