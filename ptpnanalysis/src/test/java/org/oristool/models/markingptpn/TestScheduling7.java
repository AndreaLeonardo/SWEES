package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Test con 3 Task in cui RM fallisce e EDF no, aumentando progressivamente
//il tempo di computazione
class TestScheduling7 {
	TaskSet taskSet;
	ArrayList<Task> tasks1;
	
	@BeforeEach
	void setUp() {
		taskSet = new TaskSet();
		tasks1 = new ArrayList<>();
		Task task1 =  new Task(30, 100);
		Task task2 = new Task(40, 90);
		Task task3 = new Task(50, 80);
				
		taskSet.addTask(task1);
		taskSet.addTask(task2);
		taskSet.addTask(task3);
		
		//Copia del taskSet
		for (Task t : taskSet.getTasks()) {
			tasks1.add(t);
		}
	}
	
	
	@Test
	void testEDF() {
		for(int i =12; i<18; i++) {
			EDFScheduling edf = new EDFScheduling();
			ArrayList<double[]> bounds = edf.schedule(taskSet, 1, i);
		
			assertTrue(bounds.get(0)[1] <= taskSet.getTasks().get(0).getDeadline());
			assertTrue(bounds.get(1)[1] <= taskSet.getTasks().get(1).getDeadline());
			assertTrue(bounds.get(2)[1] <= taskSet.getTasks().get(2).getDeadline());
		}
	}
	
	@Test
	void testRM() {		
		for(int i = 12; i<18; i++) {
			TaskSet tmpTaskSet = new TaskSet();
			for(Task t : tasks1) {
				tmpTaskSet.addTask(t);
			}
			RMScheduling rm = new RMScheduling();
			ArrayList<double[]> bounds = rm.schedule(tmpTaskSet, 1, i);
			assertTrue(bounds.get(0)[1] <= tasks1.get(0).getDeadline());
			assertTrue(bounds.get(1)[1] <= tasks1.get(1).getDeadline());
			if (i == 17) {
				// L'ultimo task sfora la deadline se il tempo di computazione
				// è >= 17
				assertFalse(bounds.get(2)[1] <= tasks1.get(2).getDeadline());
			}else {
				assertTrue(bounds.get(2)[1] <= tasks1.get(2).getDeadline());
			}
		}
	}
}
