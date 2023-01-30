package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test con 5 Task
class TestScheduling3 {
	TaskSet taskSet;
	ArrayList<Task> tasks1;
	
	@BeforeEach
	void setUp() {
		taskSet = new TaskSet();
		tasks1 = new ArrayList<>();
		Task task1 = new Task(8, 10);
		Task task2 =  new Task(14, 15);
		Task task3 = new Task(20, 20);
		Task task4 = new Task(20, 24);
		Task task5 = new Task (30, 30);
				
		taskSet.addTask(task1);
		taskSet.addTask(task2);
		taskSet.addTask(task3);
		taskSet.addTask(task4);
		taskSet.addTask(task5);

		
		//Copia del taskSet
		for (Task t : taskSet.getTasks()) {
			tasks1.add(t);
		}
	}
	
	@Test
	void testEDF() {
		EDFScheduling edf = new EDFScheduling();
		ArrayList<double[]> bounds = edf.schedule(taskSet, 0, 2);
		
		int c = 0;
		for(Task t : taskSet.getTasks()) {
			assertTrue(bounds.get(c)[1] <= t.getDeadline());
			c++;
		}
	}
	
	@Test
	void testRM() {
		RMScheduling rm = new RMScheduling();
		ArrayList<double[]> bounds = rm.schedule(taskSet, 0, 2);
		
		int c = 0;
		while(tasks1.isEmpty()) {
			int min = 9999;
			int minId = 0;
			
			for (Task t : tasks1) {
				if(t.getDeadline() < min) {
					min = t.getDeadline();
					minId = t.getId();
				}
			}
			for (Task t : tasks1) {
				if (t.getId() == minId) {
					// Il tempo massimo di completamento deve essere minore della deadline del task
					assertTrue(bounds.get(c)[1] <= t.getDeadline());
					c++;
					tasks1.remove(t);
					break;
				}
			}
		}
	}
	
	

}