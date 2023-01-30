package org.oristool.models.markingptpn;

import java.util.ArrayList;

public class TaskSet {
	ArrayList<Task> tasks;
	
	public TaskSet() {
		this.tasks = new ArrayList<>();
	}
	
	public void addTask(Task t) {
		tasks.add(t);
	}
	
	public ArrayList<Task> getTasks(){
		return tasks;
	}
}
