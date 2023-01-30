package org.oristool.models.ptpn;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.oristool.analyzer.state.StateFeature;
import org.oristool.petrinet.Transition;

public class PreemptiveStateFeature implements StateFeature{
	
	private Set<Transition> progressing;
	private Set<Transition> suspended;
	
	/**
     * Builds a deep copy of an input state feature.
    *
    * @param other another state feature
    */
   public PreemptiveStateFeature(PreemptiveStateFeature other) {

       this.progressing = new LinkedHashSet<Transition>(other.progressing);
       this.suspended = new LinkedHashSet<Transition>(other.suspended);
       
   }
   
   public PreemptiveStateFeature() {

       this.progressing = Collections.emptySet();
       this.suspended = Collections.emptySet();
       
   }
   
   public Set<Transition> getProgressing(){
	   return progressing;
   }
   
   public void setProgressing(Set<Transition> progressing) {
	   this.progressing = progressing;
   }
   
   public Set<Transition> getSudpended(){
	   return suspended;
   }
   
   public void setSuspended(Set<Transition> suspended) {
	   this.suspended = suspended;
   }
   
   @Override
   public boolean equals(Object other) {

       if (this == other)
           return true;

       if (!(other instanceof PreemptiveStateFeature))
           return false;

       PreemptiveStateFeature o = (PreemptiveStateFeature) other;

       return (progressing.equals(o.progressing) && suspended.equals(o.suspended));
      
   }
   
   
   @Override
   public int hashCode() {
       // FIXME the value should be cached
       return progressing.hashCode() + suspended.hashCode(); // domain.hashCode();
   }

   
   public String toString() {
       StringBuilder b = new StringBuilder();
       b.append("-- PreemptiveStateFeature --\n");
       b.append("Progressing: ");
       b.append(progressing);
       b.append("\n");
       b.append("Suspended: ");
       b.append(suspended);
       b.append("\n\n");
       return b.toString();
   }
}