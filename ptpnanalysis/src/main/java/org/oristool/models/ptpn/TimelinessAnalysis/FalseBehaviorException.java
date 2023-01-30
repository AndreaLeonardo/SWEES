package org.oristool.models.ptpn.TimelinessAnalysis;


public class FalseBehaviorException extends Exception {
public FalseBehaviorException(String str) {
		super("False Behavior: " + str);
	}
}