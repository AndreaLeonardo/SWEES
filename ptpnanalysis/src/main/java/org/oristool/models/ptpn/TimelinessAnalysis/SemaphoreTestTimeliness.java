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


public class SemaphoreTestTimeliness{
	public static void main(String[] args) {
		
		
		Resource cpu = new Resource("cpu");
		
		PetriNet pn = new PetriNet();
		
		Place mutex = pn.addPlace("mutex");

		Transition t11 = pn.addTransition("t11");
		t11.addFeature(new TimedTransitionFeature("5","5"));
		
        Place p11 = pn.addPlace("p11");
        
        Transition t12 = pn.addTransition("t12");
		t12.addFeature(new TimedTransitionFeature("0","0"));
		PreemptiveTransitionFeature ptft12 = new PreemptiveTransitionFeature();
        ptft12.addResourcePriority(cpu, new ResourcePriority(1));
        t12.addFeature(ptft12);
        
        Place p12 = pn.addPlace("p12");
        
        Transition t13 = pn.addTransition("t13");
		t13.addFeature(new TimedTransitionFeature("0","0"));
		PreemptiveTransitionFeature ptft13 = new PreemptiveTransitionFeature();
        ptft13.addResourcePriority(cpu, new ResourcePriority(1));
        t13.addFeature(ptft13);
        
        pn.addPostcondition(t11,p11);
        pn.addPrecondition(p11, t12);
        pn.addPrecondition(mutex, t12);
        pn.addPostcondition(t12,p12);
        pn.addPrecondition(p12, t13);
        pn.addPostcondition(t13,mutex);
        
        Transition t31 = pn.addTransition("t31");
		t31.addFeature(new TimedTransitionFeature("15","15"));
		
        Place p31 = pn.addPlace("p31");
        
        Transition t32 = pn.addTransition("t32");
		t32.addFeature(new TimedTransitionFeature("0","0"));
		PreemptiveTransitionFeature ptft32 = new PreemptiveTransitionFeature();
        ptft32.addResourcePriority(cpu, new ResourcePriority(3));
        t32.addFeature(ptft32);
        
        Place p32 = pn.addPlace("p32");
        
        Transition t33 = pn.addTransition("t33");
		t33.addFeature(new TimedTransitionFeature("0","0"));
		PreemptiveTransitionFeature ptft33 = new PreemptiveTransitionFeature();
        ptft33.addResourcePriority(cpu, new ResourcePriority(3));
        t33.addFeature(ptft33);
        
        pn.addPostcondition(t31,p31);
        pn.addPrecondition(p31, t32);
        pn.addPrecondition(mutex, t32);
        pn.addPostcondition(t32,p32);
        pn.addPrecondition(p32, t33);
        pn.addPostcondition(t33,mutex);
        
        
//        Transition t21 = pn.addTransition("t21");
//		t21.addFeature(new TimedTransitionFeature(new OmegaBigDecimal("10"), OmegaBigDecimal.POSITIVE_INFINITY));
//		
//		
//        Place p21 = pn.addPlace("p21");
//        
//        Transition t22 = pn.addTransition("t22");
//		t22.addFeature(new TimedTransitionFeature("1","2"));
//		PreemptiveTransitionFeature ptft22 = new PreemptiveTransitionFeature();
//        ptft22.addResourcePriority(cpu, new ResourcePriority(2));
//        t22.addFeature(ptft22);
//        
//        pn.addPostcondition(t21,p21);
//        pn.addPrecondition(p21, t22);
        
        Marking m = new Marking();
        m.addTokens(mutex, 1);
        
        PreemptiveAnalysis preeAnalysis = PreemptiveAnalysis.builder().build();
        SuccessionGraph graph = preeAnalysis.compute(pn, m);
        SuccessionGraphViewer.show(graph);
        PreemptiveTimelinessAnalysis analysis = PreemptiveTimelinessAnalysis.builder().build();
        analysis.compute(pn, graph, t11, t33);
	}
}