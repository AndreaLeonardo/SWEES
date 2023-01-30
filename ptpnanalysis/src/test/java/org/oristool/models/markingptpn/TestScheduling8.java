package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Test con 4 Task in cui RM fallisce e EDF no, aumentando progressivamente
// il tempo di computazione
class TestScheduling8 {
	TaskSet taskSet;
	ArrayList<Task> tasks1;
	
	@BeforeEach
	void setUp() {
		taskSet = new TaskSet();
		tasks1 = new ArrayList<>();
		Task task1 =  new Task(50, 110);
		Task task2 = new Task(60, 100);
		Task task3 = new Task(65, 110);
		Task task4 = new Task (75, 90);
				
		taskSet.addTask(task1);
		taskSet.addTask(task2);
		taskSet.addTask(task3);
		taskSet.addTask(task4);
		
		//Copia del taskSet
		for (Task t : taskSet.getTasks()) {
			tasks1.add(t);
		}
	}
	
	
	@Test
	void testEDF() {
		for(int i =14; i<20; i++) {
			EDFScheduling edf = new EDFScheduling();
			ArrayList<double[]> bounds = edf.schedule(taskSet, 1, i);
		
			assertTrue(bounds.get(0)[1] <= taskSet.getTasks().get(0).getDeadline());
			assertTrue(bounds.get(1)[1] <= taskSet.getTasks().get(1).getDeadline());
			assertTrue(bounds.get(2)[1] <= taskSet.getTasks().get(2).getDeadline());
			assertTrue(bounds.get(3)[1] <= taskSet.getTasks().get(3).getDeadline());
		}
	}
	
	@Test
	void testRM() {		
		for(int i = 14; i<20; i++) {
			TaskSet tmpTaskSet = new TaskSet();
			for(Task t : tasks1) {
				tmpTaskSet.addTask(t);
			}
			RMScheduling rm = new RMScheduling();
			ArrayList<double[]> bounds = rm.schedule(tmpTaskSet, 1, i);
			assertTrue(bounds.get(0)[1] <= tasks1.get(0).getDeadline());
			assertTrue(bounds.get(1)[1] <= tasks1.get(1).getDeadline());
			assertTrue(bounds.get(2)[1] <= tasks1.get(2).getDeadline());
			if (i == 19) {
				// L'ultimo task sfora la deadline se il tempo di computazione
				// è >= 19
				assertFalse(bounds.get(3)[1] <= tasks1.get(3).getDeadline());
			}else {
				assertTrue(bounds.get(3)[1] <= tasks1.get(3).getDeadline());
			}
		}
	}
}
