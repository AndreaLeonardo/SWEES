package org.oristool.models.ptpn;

import org.oristool.petrinet.Transition;

public interface SamePrioStrategy{
	/**
     * Method to check which between two transitions having the same priority  
     * has to be fired firstly
     *
     * @param t transition that we want to be the first
     * 
     * @param t1 transition that we want to be the second
     * 
     * @return true if t fires before t1
     */
	public boolean firesBefore(Transition t, Transition t1);
	
	/**
     * Method to check which between two transitions having the same priority  
     * has to be fired firstly
     *
     * @param t transition that we want to be the second
     * 
     * @param t1 transition that we want to be the first
     * 
     * @return true if t fires after t1
     */
	public boolean firesAfter(Transition t, Transition t1);
}