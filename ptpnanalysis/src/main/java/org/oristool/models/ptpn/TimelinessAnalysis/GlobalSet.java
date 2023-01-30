package org.oristool.models.ptpn.TimelinessAnalysis;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.oristool.math.OmegaBigDecimal;


public class GlobalSet{
	private int[][] a;
	private OmegaBigDecimal[] b;
	private int n; 
	private int m;
	
	public GlobalSet(int n, int m) {
		a = new int[n][m];
		b = new OmegaBigDecimal[n];
		this.n = n;
		this.m = m;
	}
	
	public GlobalSet(int m) {
		this(0, m);
	}
	
	void allPositiveVars() {
		for(int i = 0; i<m; i++) {
			int[] k = new int[m];
			for(int j = 0; j < m; j++) {
				k[j] = 0;
			}
			k[i] = -1;
			addInequality(k, new OmegaBigDecimal(0));
		}
	}
	
	public void addInequality(int[] value, OmegaBigDecimal coefficent) {
		int[][] newA = new int[n + 1][m];
		OmegaBigDecimal[] newB = new OmegaBigDecimal[n + 1];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				newA[i][j] = a[i][j];
			}
			newB[i] = b[i];
		}
		for(int j = 0; j < m; j++) {
			newA[n][j] = value[j];
		}
		newB[n] = coefficent;
		n++;
		a = newA;
		b = newB;
	}
	
	public int getVariables() {
		return m;
	}
	
	public int getRows() {
		return n;
	}
	
	public double getMatrixValue(int row, int column) {
		if(row < n && column < m && row >= 0 && column >= 0) {
			return a[row][column];
		}
		try {
			if(row >= n || row < 0)
				throw new OutOfRangeException(row, 0, n);
			if(column >= m || column < 0)
				throw new OutOfRangeException(column, 0, m);
		}catch(OutOfRangeException e) {
			System.out.println("Exception occurred: "+ e.getMessage());
		}
		return 0;
	}
	
	public double getCoefficientValue(int index) {
		if(index < n && index >= 0) {
			return b[index].doubleValue();
		}
		try {
			throw new OutOfRangeException(index, 0, n);
		}catch(OutOfRangeException e) {
			System.out.println("Exception occurred: "+ e.getMessage());
		}
		return 0;
	}
	
	@Override
	public String toString() {
		String s = new String();
		s += "Matrix: \n";
		for(int i = 0; i<n; i++) {
			for(int j = 0; j<m; j++) {
				s += a[i][j];
				s += " ";
			}
			s += "  ";
			s += b[i];
			s += "\n";
		}
		/*s += "Coefficients: \n";
		for(int i = 0; i<n; i++) {
			s += b[i];
			s += "\n";
		}*/
		return s;
	}
}






















