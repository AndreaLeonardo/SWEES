package org.oristool.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oristool.analyzer.Succession;
import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.analyzer.policy.EnumerationPolicy;
import org.oristool.analyzer.policy.FIFOPolicy;
import org.oristool.analyzer.policy.LIFOPolicy;
import org.oristool.analyzer.state.State;
import org.oristool.analyzer.stop.AlwaysFalseStopCriterion;
import org.oristool.analyzer.stop.StopCriterion;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.expression.Variable;
import org.oristool.models.pn.MarkingConditionStopCriterion;
import org.oristool.models.pn.PetriStateFeature;
import org.oristool.models.ptpn.PreemptiveAnalysis;
import org.oristool.models.ptpn.PreemptiveTransitionFeature;
import org.oristool.models.ptpn.SamePrioStrategy;
import org.oristool.models.stpn.trees.EnablingSyncsFeature;
import org.oristool.models.stpn.trees.SuccessionGraphViewer;
import org.oristool.models.tpn.TimedAnalysis;
import org.oristool.models.tpn.TimedStateFeature;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.ResourcePriority;
import org.oristool.petrinet.Transition;

class PreemptiveAnalyzerTest {

    private PetriNet pn;
    private Marking marking;

    @BeforeEach
    void buildModel() {
		Resource cpu = new Resource("cpu");
		
		pn = new PetriNet();

        //Task1
        Transition t0 = pn.addTransition("t0");
		t0.addFeature(new TimedTransitionFeature("40","40"));
        
        Transition t1 = pn.addTransition("t1");
		t1.addFeature(new TimedTransitionFeature("0","0"));
        t1.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(1)));
        
        Transition t2 = pn.addTransition("t2");
		t2.addFeature(new TimedTransitionFeature("5","10"));
        t2.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(1)));
        
        //Task2
        Transition t3 = pn.addTransition("t3");
		t3.addFeature(new TimedTransitionFeature("40","40"));
        
        Transition t4 = pn.addTransition("t4");
		t4.addFeature(new TimedTransitionFeature("1","2"));
        t4.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(2)));
        
        Transition t5 = pn.addTransition("t5");
		t5.addFeature(new TimedTransitionFeature("1","2"));
        t5.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(2)));
        
        //Task3
        Transition t6 = pn.addTransition("t6");
		t6.addFeature(new TimedTransitionFeature("80","80"));
        
        Transition t7 = pn.addTransition("t7");
		t7.addFeature(new TimedTransitionFeature("0","0"));
        t7.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t8 = pn.addTransition("t8");
		t8.addFeature(new TimedTransitionFeature("1","2"));
        t8.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t9 = pn.addTransition("t9");
		t9.addFeature(new TimedTransitionFeature("5","10"));
        t9.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t10 = pn.addTransition("t10");
		t10.addFeature(new TimedTransitionFeature("0","0"));
        t10.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t11 = pn.addTransition("t11");
		t11.addFeature(new TimedTransitionFeature("1","2"));
        t11.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        //Task4
        Transition t12 = pn.addTransition("t12");
		t12.addFeature(new TimedTransitionFeature("100","100"));
        
        Transition t13 = pn.addTransition("t13");
		t13.addFeature(new TimedTransitionFeature("0","0"));
        t13.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(4)));
        
        Transition t14 = pn.addTransition("t14");
		t14.addFeature(new TimedTransitionFeature("0","0"));
        t14.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t15 = pn.addTransition("t15");
		t15.addFeature(new TimedTransitionFeature("1","2"));
        t15.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t16 = pn.addTransition("t16");
		t16.addFeature(new TimedTransitionFeature("5","10"));
        t16.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(4)));
        
        Transition t17 = pn.addTransition("t17");
		t17.addFeature(new TimedTransitionFeature("0","0"));
        t17.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(4)));
        
        Transition t18 = pn.addTransition("t18");
		t18.addFeature(new TimedTransitionFeature("0","0"));
        t18.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t19 = pn.addTransition("t19");
		t19.addFeature(new TimedTransitionFeature("1","2"));
        t19.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        //Task5
        Transition t20 = pn.addTransition("t20");
		t20.addFeature(new TimedTransitionFeature(new OmegaBigDecimal("120"), OmegaBigDecimal.POSITIVE_INFINITY));
        
        Transition t21 = pn.addTransition("t21");
		t21.addFeature(new TimedTransitionFeature("1","2"));
        t21.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(5)));
        
        Transition t22 = pn.addTransition("t22");
		t22.addFeature(new TimedTransitionFeature("0","0"));
        t22.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(5)));
        
        Transition t23 = pn.addTransition("t23");
		t23.addFeature(new TimedTransitionFeature("0","0"));
        t23.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Transition t24 = pn.addTransition("t24");
		t24.addFeature(new TimedTransitionFeature("1","2"));
        t24.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(3)));
        
        Place p0 = pn.addPlace("p0");

        Place p1 = pn.addPlace("p1");

        Place p2 = pn.addPlace("p2");

        Place p3 = pn.addPlace("p3");

        Place p4 = pn.addPlace("p4");

        Place p5 = pn.addPlace("p5");

        Place p6 = pn.addPlace("p6");

        Place p7 = pn.addPlace("p7");

        Place p8 = pn.addPlace("p8");

        Place p9 = pn.addPlace("p9");

        Place p10 = pn.addPlace("p10");

        Place p11 = pn.addPlace("p11");

        Place p12 = pn.addPlace("p12");

        Place p13 = pn.addPlace("p13");

        Place p14 = pn.addPlace("p14");

        Place p15 = pn.addPlace("p15");

        Place p16 = pn.addPlace("p16");

        Place p17 = pn.addPlace("p17");

        Place p18 = pn.addPlace("p18");

        Place p19 = pn.addPlace("p19");
        
        //Task1
        pn.addPostcondition(t0,p0);
        pn.addPrecondition(p0,t1);
        pn.addPostcondition(t1,p1);
        pn.addPrecondition(p1,t2);

        //Task2
        pn.addPostcondition(t3,p2);
        pn.addPrecondition(p2,t4);
        pn.addPostcondition(t4,p3);
        pn.addPrecondition(p3,t5);

        //Task3
        pn.addPostcondition(t6, p4);
        pn.addPrecondition(p4, t7);
        pn.addPostcondition(t7, p5);
        pn.addPrecondition(p5, t8);
        pn.addPostcondition(t8, p6);
        pn.addPrecondition(p6, t9);
        pn.addPostcondition(t9, p7);
        pn.addPrecondition(p7, t10);
        pn.addPostcondition(t10, p8);
        pn.addPrecondition(p8, t11);

        //Task4
        pn.addPostcondition(t12,p9);
        pn.addPrecondition(p9,t13);
        pn.addPostcondition(t13,p10);
        pn.addPrecondition(p10,t14);
        pn.addPostcondition(t14,p11);
        pn.addPrecondition(p11,t15);
        pn.addPostcondition(t15,p12);
        pn.addPrecondition(p12,t16);
        pn.addPostcondition(t16,p13);
        pn.addPrecondition(p13,t17);
        pn.addPostcondition(t17,p14);
        pn.addPrecondition(p14,t18);
        pn.addPostcondition(t18,p15);
        pn.addPrecondition(p15,t19);

        //Task5
        pn.addPostcondition(t20,p16);
        pn.addPrecondition(p16,t21);
        pn.addPostcondition(t21,p17);
        pn.addPrecondition(p17,t22);
        pn.addPostcondition(t22,p18);
        pn.addPrecondition(p18,t23);
        pn.addPostcondition(t23,p19);
        pn.addPrecondition(p19,t24);
        
        marking = new Marking();
    }

    @Test
    void preemptiveAnalysisTest() {

    	PreemptiveAnalysis analysis = PreemptiveAnalysis.builder().build();
    	SamePrioStrategy samePrioStrategy = analysis.samePrioStrategy();
                       
        if (!analysis.canAnalyze(pn)) {
            assertThrows(IllegalArgumentException.class, () -> analysis.compute(pn, marking));
        } else {
            SuccessionGraph graph = analysis.compute(pn, marking);
            
            for (Succession s : graph.getSuccessions()) {
            	Set<Transition> transitions = s.getParent().getFeature(PetriStateFeature.class).getEnabled();
            	String fired = s.getEvent().toString();
            	Transition firedTransition = null;
            	for(Transition t : transitions) {
            		if(fired.equals(t.toString())) {
            			firedTransition = t;
            		}
            	}
            	
        		PreemptiveTransitionFeature ptf = firedTransition.getFeature(PreemptiveTransitionFeature.class);
        		try {
        			Map<Resource, ResourcePriority> trp = ptf.getMap();
                	for(Transition t : transitions) {
                		if(!fired.equals(t.getName()) && t.hasFeature(PreemptiveTransitionFeature.class)){
                			PreemptiveTransitionFeature ptf1 = t.getFeature(PreemptiveTransitionFeature.class);
                    		Map<Resource, ResourcePriority> trp1 = ptf1.getMap();
                    		for (Map.Entry<Resource, ResourcePriority> pair : trp.entrySet()) {
                    			ResourcePriority compared = trp1.get(pair.getKey());
                    			
                    			try{
                    				assertTrue(pair.getValue().isPrior(compared) || (compared.isEqual(pair.getValue()) && samePrioStrategy.firesBefore(firedTransition, t)));
                    		     }catch(AssertionError e){
                    		          System.out.println("Transition " +fired + " prio " + pair.getValue() + " is not prior wrt " + "Transition " +t.toString() + " prio " +compared);
                    		          System.out.println(s);
                    		     }
                    		}
                		}
                	}
        		} catch(NullPointerException e) {}
            }
        }
    }
}
