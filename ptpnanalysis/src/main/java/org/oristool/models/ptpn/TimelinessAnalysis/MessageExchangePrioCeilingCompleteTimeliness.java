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


public class MessageExchangePrioCeilingCompleteTimeliness{
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
        
        
        Transition t6 = pn.addTransition("t6");
		t6.addFeature(new TimedTransitionFeature("80","80"));
        
        Place p5 = pn.addPlace("p5");
        
        Transition t7 = pn.addTransition("t7");
		t7.addFeature(new TimedTransitionFeature("0","0"));
        t7.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Place p6 = pn.addPlace("p6");
        
        Transition t8 = pn.addTransition("t8");
		t8.addFeature(new TimedTransitionFeature("1","2"));
        t8.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));

        Place p7 = pn.addPlace("p7");
        
        Transition t9 = pn.addTransition("t9");
		t9.addFeature(new TimedTransitionFeature("5","10"));
        t9.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Place p8 = pn.addPlace("p8");
        
        Transition t10 = pn.addTransition("t10");
		t10.addFeature(new TimedTransitionFeature("0","0"));
		t10.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));

        Place p9 = pn.addPlace("p9");
        
        Transition t11 = pn.addTransition("t11");
		t11.addFeature(new TimedTransitionFeature("1","2"));
        t11.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Place p10 = pn.addPlace("p10");
        
        
        Transition t12 = pn.addTransition("t12");
		t12.addFeature(new TimedTransitionFeature("100","100"));
        
        Place p11 = pn.addPlace("p11");
        
        Transition t13 = pn.addTransition("t13");
		t13.addFeature(new TimedTransitionFeature("0","0"));
        t13.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(4)));
        
        Place p12 = pn.addPlace("p12");
        
        Transition t14 = pn.addTransition("t14");
		t14.addFeature(new TimedTransitionFeature("0","0"));
        t14.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));

        Place p13 = pn.addPlace("p13");
        
        Transition t15 = pn.addTransition("t15");
		t15.addFeature(new TimedTransitionFeature("1","2"));
        t15.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));

        Place p14 = pn.addPlace("p14");
        
        Transition t16 = pn.addTransition("t16");
		t16.addFeature(new TimedTransitionFeature("5","10"));
        t16.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(4)));
        
        Place p15 = pn.addPlace("p15");
        
        Transition t17 = pn.addTransition("t17");
		t17.addFeature(new TimedTransitionFeature("0","0"));
		t17.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(4)));
        
        Place p16 = pn.addPlace("p16");
        
        Transition t18 = pn.addTransition("t18");
		t18.addFeature(new TimedTransitionFeature("0","0"));
		t18.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));

        Place p17 = pn.addPlace("p17");
        
        Transition t19 = pn.addTransition("t19");
		t19.addFeature(new TimedTransitionFeature("1","2"));
        t19.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        

        Transition t20 = pn.addTransition("t20");
		t20.addFeature(new TimedTransitionFeature(new OmegaBigDecimal("120"),OmegaBigDecimal.POSITIVE_INFINITY));
        
        Place p18 = pn.addPlace("p18");
        
        Transition t21 = pn.addTransition("t21");
		t21.addFeature(new TimedTransitionFeature("1","2"));
        t21.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(5)));
        
        Place p19 = pn.addPlace("p19");
        
        Transition t22 = pn.addTransition("t22");
		t22.addFeature(new TimedTransitionFeature("0","0"));
        t22.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(5)));

        Place p20 = pn.addPlace("p20");
        
        Transition t23 = pn.addTransition("t23");
		t23.addFeature(new TimedTransitionFeature("0","0"));
        t23.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));

        Place p21 = pn.addPlace("p21");
        
        Transition t24 = pn.addTransition("t24");
        t24.addFeature(new TimedTransitionFeature("1","2"));
        t24.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Place p22 = pn.addPlace("p22");
        
        pn.addPostcondition(t6, p5);
        pn.addPrecondition(p5, t7);
        pn.addPrecondition(p22, t7);
        pn.addPostcondition(t7, p6);
        pn.addPrecondition(p6, t8);
        pn.addPostcondition(t8, p7);
        pn.addPostcondition(t8, p22);
        pn.addPrecondition(p7, t9);
        pn.addPostcondition(t9, p8);
        pn.addPrecondition(p8, t10);
        pn.addPrecondition(p10, t10);
        pn.addPostcondition(t10, p9);
        pn.addPrecondition(p9, t11);
        pn.addPostcondition(t11, p10);
        
        pn.addPostcondition(t12, p11);
        pn.addPrecondition(p11, t13);
        pn.addPostcondition(t13, p12);
        pn.addPrecondition(p12, t14);
        pn.addPrecondition(p22, t14);
        pn.addPostcondition(t14, p13);
        pn.addPrecondition(p13, t15);
        pn.addPostcondition(t15, p14);
        pn.addPostcondition(t15, p22);
        pn.addPrecondition(p14, t16);
        pn.addPostcondition(t16, p15);
        pn.addPrecondition(p15, t17);
        pn.addPostcondition(t17, p16);
        pn.addPrecondition(p16, t18);
        pn.addPrecondition(p10, t18);
        pn.addPostcondition(t18, p17);
        pn.addPrecondition(p17, t19);
        pn.addPostcondition(t19, p10);
        
        pn.addPostcondition(t20, p18);
        pn.addPrecondition(p18, t21);
        pn.addPostcondition(t21, p19);
        pn.addPrecondition(p19, t22);
        pn.addPostcondition(t22, p20);
        pn.addPrecondition(p20, t23);
        pn.addPrecondition(p22, t23);
        pn.addPostcondition(t23, p21);
        pn.addPrecondition(p21, t24);
        pn.addPostcondition(t24, p22);
       
        
        Marking m = new Marking();
        m.addTokens(p10, 1);
        m.addTokens(p22, 1);
        
        PreemptiveAnalysis preeAnalysis = PreemptiveAnalysis.builder().build();
        SuccessionGraph graph = preeAnalysis.compute(pn, m);
        SuccessionGraphViewer.show(graph);
        PreemptiveTimelinessAnalysis analysis = PreemptiveTimelinessAnalysis.builder().build();
        analysis.compute(pn, graph, t0, t5);
	}
}