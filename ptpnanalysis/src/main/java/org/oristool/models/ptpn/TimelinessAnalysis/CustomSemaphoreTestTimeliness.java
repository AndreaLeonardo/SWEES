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


public class CustomSemaphoreTestTimeliness{
	public static void main(String[] args) {
		
		
		Resource cpu = new Resource("cpu");
		
		PetriNet pn = new PetriNet();
		
		Place mutex = pn.addPlace("mutex");

		Transition t0 = pn.addTransition("t0");
		t0.addFeature(new TimedTransitionFeature("5","5"));
		
        Place p0 = pn.addPlace("p0");
        
        Transition t3 = pn.addTransition("t3");
		t3.addFeature(new TimedTransitionFeature("0","0"));
		PreemptiveTransitionFeature ptft12 = new PreemptiveTransitionFeature();
        ptft12.addResourcePriority(cpu, new ResourcePriority(3));
        t3.addFeature(ptft12);
        
        Place p3 = pn.addPlace("p3");
        
        Transition t6 = pn.addTransition("t6");
		t6.addFeature(new TimedTransitionFeature("1","3"));
		PreemptiveTransitionFeature ptft13 = new PreemptiveTransitionFeature();
        ptft13.addResourcePriority(cpu, new ResourcePriority(3));
        t6.addFeature(ptft13);
        
        Place p8 = pn.addPlace("p8");
        
        pn.addPrecondition(p8, t0);
        pn.addPostcondition(t0,p0);
        pn.addPrecondition(p0, t3);
        pn.addPrecondition(mutex, t3);
        pn.addPostcondition(t3,p3);
        pn.addPrecondition(p3, t6);
        pn.addPostcondition(t6,mutex);
        pn.addPostcondition(t6,p8);
        
        Transition t1 = pn.addTransition("t1");
		t1.addFeature(new TimedTransitionFeature("20","20"));
		
        Place p1 = pn.addPlace("p1");
        
        Transition t4 = pn.addTransition("t4");
		t4.addFeature(new TimedTransitionFeature("0","0"));
		PreemptiveTransitionFeature ptft32 = new PreemptiveTransitionFeature();
        ptft32.addResourcePriority(cpu, new ResourcePriority(2));
        t4.addFeature(ptft32);
        
        Place p4 = pn.addPlace("p4");
        
        Transition t7 = pn.addTransition("t7");
		t7.addFeature(new TimedTransitionFeature("2","4"));
		PreemptiveTransitionFeature ptft33 = new PreemptiveTransitionFeature();
        ptft33.addResourcePriority(cpu, new ResourcePriority(2));
        t7.addFeature(ptft33);
        
        pn.addPostcondition(t1,p1);
        pn.addPrecondition(p1, t4);
        pn.addPrecondition(mutex, t4);
        pn.addPostcondition(t4,p4);
        pn.addPrecondition(p4, t7);
        pn.addPostcondition(t7,mutex);
        
        
        Transition t2 = pn.addTransition("t2");
		t2.addFeature(new TimedTransitionFeature(new OmegaBigDecimal("1"), OmegaBigDecimal.POSITIVE_INFINITY));
		
        Place p2 = pn.addPlace("p2");
        
        Transition t5 = pn.addTransition("t5");
		t5.addFeature(new TimedTransitionFeature("0","0"));
		PreemptiveTransitionFeature ptft22 = new PreemptiveTransitionFeature();
        ptft22.addResourcePriority(cpu, new ResourcePriority(1));
        t5.addFeature(ptft22);
        
        Place p5 = pn.addPlace("p5");
        
        Transition t8 = pn.addTransition("t8");
		t8.addFeature(new TimedTransitionFeature("1","2"));
		PreemptiveTransitionFeature ptft39 = new PreemptiveTransitionFeature();
        ptft39.addResourcePriority(cpu, new ResourcePriority(1));
        t8.addFeature(ptft39);
        
        Place p7 = pn.addPlace("p7");
        
        pn.addPostcondition(t2,p2);
        pn.addPrecondition(p2, t5);
        pn.addPostcondition(t5,p5);
        pn.addPrecondition(p5, t8);
        pn.addPostcondition(t8,p7);
        
        Marking m = new Marking();
        m.addTokens(mutex, 1);
        m.addTokens(p8, 1);
        m.addTokens(p7, 1);
        
        PreemptiveAnalysis preeAnalysis = PreemptiveAnalysis.builder().build();
        SuccessionGraph graph = preeAnalysis.compute(pn, m);
        SuccessionGraphViewer.show(graph);
        PreemptiveTimelinessAnalysis analysis = PreemptiveTimelinessAnalysis.builder().build();
        analysis.compute(pn, graph, t0, t6);
	}
}