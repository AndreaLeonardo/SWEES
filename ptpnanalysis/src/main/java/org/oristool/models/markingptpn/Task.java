package org.oristool.models.markingptpn;

import java.util.ArrayList;
import java.util.Random;

public class Task {
	private int id;
	private int deadline;
	private int period;
	private int counter = 0;
	private ArrayList<String> resourceId;
	
	public Task(int deadline, int period) {
		Random r = new Random();
		this.deadline = deadline;
		this.period = period;
		this.id = r.nextInt(100000000);
		this.resourceId = new ArrayList<String>();
	}
	
	public void counterUp() {
		this.counter++;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public int getDeadline() {
		return deadline;
	}

	public void addResourceId(String p){
		this.resourceId.add(p);
	}

	public ArrayList<String> getResourceId(){
		return this.resourceId;
	}
}
