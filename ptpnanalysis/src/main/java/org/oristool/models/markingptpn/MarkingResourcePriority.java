package org.oristool.models.markingptpn;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.ResourcePriority;
	
public class MarkingResourcePriority extends ResourcePriority{
	private MarkingExpr markingExpr;
	private String expr;
	
	public MarkingResourcePriority(int priority, String expr, PetriNet pn) {
		super(priority);
		this.expr = expr;
		markingExpr = MarkingExpr.from(expr, pn);
	}
	
	public double getPriority(Marking m) {
		return markingExpr.evaluate(m);
	}
	
	public String getExpr() {
		return expr;
	}

}