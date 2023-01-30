package org.oristool.models.ptpn;

import org.oristool.petrinet.Transition;

public class AlphabeticalStrategy implements SamePrioStrategy{

	@Override
	public boolean firesAfter(Transition t, Transition t1) {
		String tS = t.toString();
		String t1S = t1.toString();
		
		while(tS.length() != 0 && t1S.length() != 0) {
	         Character chart = tS.charAt(0);
	         Character chart1 = t1S.charAt(0);
	         boolean tSchar = false;
	         boolean tS1char = false;
	         String subStr;
	         String subStr1;
        	 int i = 0;
        	 int j = 0;
	         if(Character.isDigit(chart)) {
	        	 tSchar = false;
	        	 while(i < tS.length() && Character.isDigit(tS.charAt(i))){
	        		 i++;
	        	 }
	         }
	         else if(Character.isLetter(chart)) {
	        	 tSchar = true;
	        	 while(i < tS.length() && Character.isLetter(tS.charAt(i))){
	        		 i++;
	        	 }
	         }
	         
	         if(Character.isDigit(chart1)) {
	        	 tS1char = false;
	        	 while(j < t1S.length() && Character.isDigit(t1S.charAt(j))){
	        		 j++;
	        	 }
	         }
	         else if(Character.isLetter(chart1)) {
	        	 tS1char = true;
	        	 while(j < t1S.length() && Character.isLetter(t1S.charAt(j))){
	        		 j++;
	        	 }
	         }
	         
	         if(tSchar == tS1char) {
	        	 subStr = tS.substring(0,i);
	        	 tS = tS.substring(i);
	        	 subStr1 = t1S.substring(0,j);
	        	 t1S = t1S.substring(j);
	        	 if(subStr.compareTo(subStr1) != 0) {
	        		 
	        		 if(tSchar) {
		        		 return subStr.compareTo(subStr1) > 0;
		        	 }
		        	 else {
		        		 return Integer.parseInt(subStr) - Integer.parseInt(subStr1) > 0;
		        	 }
	        	 }
	         }
	         else {
	        	 return tSchar ? true : false;
	         }
		}
		return true;
	}


	@Override
	public boolean firesBefore(Transition t, Transition t1) {
		String tS = t.toString();
		String t1S = t1.toString();
		if(tS != t1S) {
		System.out.println(tS + " " + t1S);
		}
		while(tS.length() != 0 && t1S.length() != 0) {
	         Character chart = tS.charAt(0);
	         Character chart1 = t1S.charAt(0);
	         boolean tSchar = false;
	         boolean tS1char = false;
	         String subStr;
	         String subStr1;
        	 int i = 0;
        	 int j = 0;
	         if(Character.isDigit(chart)) {
	        	 tSchar = false;
	        	 while(i < tS.length() && Character.isDigit(tS.charAt(i))){
	        		 i++;
	        	 }
	         }
	         else if(Character.isLetter(chart)) {
	        	 tSchar = true;
	        	 while(i < tS.length() && Character.isLetter(tS.charAt(i))){
	        		 i++;
	        	 }
	         }
	         
	         if(Character.isDigit(chart1)) {
	        	 tS1char = false;
	        	 while(j < t1S.length() && Character.isDigit(t1S.charAt(j))){
	        		 j++;
	        	 }
	         }
	         else if(Character.isLetter(chart1)) {
	        	 tS1char = true;
	        	 while(j < t1S.length() && Character.isLetter(t1S.charAt(j))){
	        		 j++;
	        	 }
	         }
	         
	         if(tSchar == tS1char) {
	        	 subStr = tS.substring(0,i);
	        	 tS = tS.substring(i);
	        	 subStr1 = t1S.substring(0,j);
	        	 t1S = t1S.substring(j);
	        	 if(subStr.compareTo(subStr1) != 0) {
	        		 
	        		 if(tSchar) {
	        			 System.out.println(subStr.compareTo(subStr1) < 0);
		        		 return subStr.compareTo(subStr1) < 0;
		        	 }
		        	 else {
		        		 System.out.println(Integer.parseInt(subStr) - Integer.parseInt(subStr1) < 0);
		        		 return Integer.parseInt(subStr) - Integer.parseInt(subStr1) < 0;
		        	 }
	        	 }
	         }
	         else {
	        	 System.out.println(tSchar ? false : true);
	        	 return tSchar ? false : true;
	         }
		}
		return false;
	}
	
}