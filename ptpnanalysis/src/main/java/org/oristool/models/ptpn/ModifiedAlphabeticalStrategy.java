package org.oristool.models.ptpn;

import org.oristool.petrinet.Transition;

public class ModifiedAlphabeticalStrategy implements SamePrioStrategy{

	@Override
	public boolean firesBefore(Transition t, Transition t1) {
		String tS = t.toString().replaceAll("[0-9]","");
		String t1S = t1.toString().replaceAll("[0-9]","");
		if(tS.compareTo(t1S) == 0) {
			return extractInt(t.toString()) - extractInt(t1.toString()) < 0;
		}
		return tS.compareTo(t1S) < 0;
		
	}

	@Override
	public boolean firesAfter(Transition t, Transition t1) {
		String tS = t.toString().replaceAll("[0-9]","");
		String t1S = t1.toString().replaceAll("[0-9]","");
		if(tS.compareTo(t1S) == 0) {
			return extractInt(t.toString()) - extractInt(t1.toString()) > 0;
		}
		return tS.compareTo(t1S) > 0;
	}
	
    public int extractInt(String s) {
        String num = s.replaceAll("\\D", "");
        return num.isEmpty() ? 0 : Integer.parseInt(num);
    }
}