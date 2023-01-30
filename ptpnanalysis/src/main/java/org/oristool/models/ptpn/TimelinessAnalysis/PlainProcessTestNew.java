package org.oristool.models.ptpn.TimelinessAnalysis;

import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.models.ptpn.PreemptiveAnalysis;
import org.oristool.models.ptpn.PreemptiveTransitionFeature;
import org.oristool.models.stpn.trees.SuccessionGraphViewer;
import org.oristool.models.tpn.TimedTransitionFeature;


public class PlainProcessTestNew{
	
	
	public static void main(String[] args) {
		Resource cpu = new Resource("cpu");
		
		PetriNet pn = new PetriNet();

        Place place1 = pn.addPlace("place1");
        Transition trans1 = pn.addTransition("trans1");
        Place place2 = pn.addPlace("place2");

        pn.addPrecondition(place1, trans1);
        pn.addPostcondition(trans1, place2);
        
        trans1.addFeature(new TimedTransitionFeature("1","3"));
        
        PreemptiveTransitionFeature ptfTrans1 = new PreemptiveTransitionFeature();
        ptfTrans1.addResourcePriority(cpu, new ResourcePriority(3));
        trans1.addFeature(ptfTrans1);
        
        
        Place place3 = pn.addPlace("place3");
        Transition trans2 = pn.addTransition("trans2");
        Place place4 = pn.addPlace("place4");

        pn.addPrecondition(place3, trans2);
        pn.addPostcondition(trans2, place4);
        
        trans2.addFeature(new TimedTransitionFeature("1","3"));
        
        PreemptiveTransitionFeature ptfTrans2 = new PreemptiveTransitionFeature();
        ptfTrans2.addResourcePriority(cpu, new ResourcePriority(2));
        trans2.addFeature(ptfTrans2);
        
        
        Place place5 = pn.addPlace("place5");
        Transition trans3 = pn.addTransition("trans3");
        Place place6 = pn.addPlace("place6");

        pn.addPrecondition(place5, trans3);
        pn.addPostcondition(trans3, place6);
        
        trans3.addFeature(new TimedTransitionFeature("3","5"));
        
        PreemptiveTransitionFeature ptfTrans3 = new PreemptiveTransitionFeature();
        ptfTrans3.addResourcePriority(cpu, new ResourcePriority(1));
        trans3.addFeature(ptfTrans3);
        
        
        
        
        Marking m = new Marking();
        m.addTokens(place1, 1);
        m.addTokens(place3, 1);
        m.addTokens(place5, 1);
        
        PreemptiveAnalysis preeAnalysis = PreemptiveAnalysis.builder().build();
        SuccessionGraph graph = preeAnalysis.compute(pn, m);
        SuccessionGraphViewer.show(graph);
        PreemptiveTimelinessAnalysis analysis = PreemptiveTimelinessAnalysis.builder().build();
        analysis.compute(pn, graph, trans3, trans1);
        
	}
}
	

