package org.oristool.models.markingptpn;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class TestSchedulingEDFWithResources {
    @Test
    public void testScheduling(){
        TaskSet ts = new TaskSet();
        Task t1 = new Task(15, 35);
        Task t2 = new Task(15, 35);
        String mux1 = "mux1";
        t1.addResourceId(mux1);
        t2.addResourceId(mux1);
        ts.addTask(t1);
        ts.addTask(t2);
        
        EDFScheduling edf = new EDFScheduling();
		ArrayList<double[]> bounds = edf.schedule(ts, 1, 2);
		
		int c = 0;
		for(Task t : ts.getTasks()) {
			assertTrue(bounds.get(c)[1] <= t.getDeadline());
			c++;
		}
    }
}
