package org.oristool.models.markingptpn;

import java.util.ArrayList;

public interface Scheduling {
	public ArrayList<double[]> schedule(TaskSet tasks, int minTimeT1, int minTimeT2);
}
