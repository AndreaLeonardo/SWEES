package org.oristool.models.markingptpn;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class TestSchedulingRMWithResources {
    @Test
    public void testScheduling(){
        ArrayList<Task> tasks1 = new ArrayList<Task>();
        TaskSet ts = new TaskSet();
        Task t1 = new Task(15, 35);
        Task t2 = new Task(15, 35);
        Task t3 = new Task(20, 45);
        Task t4 = new Task(20, 45);
        String mux1 = "mux1";
        String mux2 = "mux2";
        t1.addResourceId(mux1);
        t2.addResourceId(mux1);
        t3.addResourceId(mux2);
        t4.addResourceId(mux2);
        ts.addTask(t1);
        ts.addTask(t2);
        ts.addTask(t3);
        ts.addTask(t4);

        for (Task t : ts.getTasks()) {
			tasks1.add(t);
		}
        
        Scheduling rm = new RMSchedulingWithResources();
        ArrayList<double[]> bounds = rm.schedule(ts, 1, 2);
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
					assertTrue(bounds.get(c)[1] <= t.getDeadline());
					c++;
					tasks1.remove(t);
					break;
				}
			}
		}
    }
}
