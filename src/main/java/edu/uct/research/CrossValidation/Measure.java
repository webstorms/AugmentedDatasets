package edu.uct.research.CrossValidation;

import org.deeplearning4j.eval.Evaluation;

public class Measure {

	public static final int ACCURACY = 0;
	public static final int PRECISION = 1;
	public static final int RECALL = 2;
	public static final int F1 = 3;
	public static final int ERROR = 4;
	
	private double[][] data;
	private int samplesProvided = 0;
	private int k;

	public Measure(int k) {
		this.k = k;
		data = new double[5][k];

	}

	public void add(Evaluation eval) {
		data[Measure.ACCURACY][samplesProvided] = eval.topNAccuracy();
		data[Measure.PRECISION][samplesProvided] = eval.precision();
		data[Measure.RECALL][samplesProvided] = eval.recall();
		data[Measure.F1][samplesProvided] = eval.f1();
		data[Measure.ERROR][samplesProvided] = 1 - eval.topNAccuracy();
		samplesProvided++;

	}

	public boolean isComplete() {
		return this.samplesProvided == this.k;
		
	}
	
	public double getMean(int type) {
		if(!isComplete()) {
			try {
				throw new Exception("Cross Validation has not completed");
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		double mean = 0;
		for(double score : data[type]) { mean += score; }
		mean /= k;
		
		return mean;
		
	}

	public double getSTD(int type) {
		if(!isComplete()) {
			try {
				throw new Exception("Cross Validation has not completed");
			} catch (Exception e) { e.printStackTrace(); }
		}

		double std = 0;
		double mean = getMean(type);
		for(double score : data[type]) std += Math.pow(score - mean, 2);
		std /= k;
		std = Math.sqrt(std);

		return std;

	}


}
