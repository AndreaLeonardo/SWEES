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


public class PlainPeriodicSporadicTestTimeliness{
	public static void main(String[] args) {
		Resource cpu = new Resource("cpu");
		
		PetriNet pn = new PetriNet();
		
		
		Transition t11 = pn.addTransition("t11");
        Place p11 = pn.addPlace("p11");
        Transition t12 = pn.addTransition("t12");
       
        pn.addPrecondition(p11, t12);
        pn.addPostcondition(t11, p11);
        
        t11.addFeature(new TimedTransitionFeature("5","5"));
        t12.addFeature(new TimedTransitionFeature("1","2"));
        
        PreemptiveTransitionFeature ptfTrans1 = new PreemptiveTransitionFeature();
        ptfTrans1.addResourcePriority(cpu, new ResourcePriority(1));
        t12.addFeature(ptfTrans1);
        
        
        Transition t21 = pn.addTransition("t21");
        Place p21 = pn.addPlace("p21");
        Transition t22 = pn.addTransition("t22");
       
        pn.addPrecondition(p21, t22);
        pn.addPostcondition(t21, p21);
        
        t21.addFeature(new TimedTransitionFeature(new OmegaBigDecimal("10"),new OmegaBigDecimal("10")));
        t22.addFeature(new TimedTransitionFeature("1.8","2.8"));
        
        PreemptiveTransitionFeature ptfTrans2 = new PreemptiveTransitionFeature();
        ptfTrans2.addResourcePriority(cpu, new ResourcePriority(2));
        t22.addFeature(ptfTrans2);
        
        
        Transition t31 = pn.addTransition("t31");
        Place p31 = pn.addPlace("p31");
        Transition t32 = pn.addTransition("t32");
       
        pn.addPrecondition(p31, t32);
        pn.addPostcondition(t31, p31);
        
        t31.addFeature(new TimedTransitionFeature("15","15"));
        t32.addFeature(new TimedTransitionFeature("2","2.8"));
        
        PreemptiveTransitionFeature ptfTrans3 = new PreemptiveTransitionFeature();
        ptfTrans3.addResourcePriority(cpu, new ResourcePriority(3));
        t32.addFeature(ptfTrans3);
        
        
        
        Marking m = new Marking();
        
        PreemptiveAnalysis preeAnalysis = PreemptiveAnalysis.builder().build();
        SuccessionGraph graph = preeAnalysis.compute(pn, m);
        SuccessionGraphViewer.show(graph);
        PreemptiveTimelinessAnalysis analysis = PreemptiveTimelinessAnalysis.builder().build();
        analysis.compute(pn, graph, t21, t22);
	}
}