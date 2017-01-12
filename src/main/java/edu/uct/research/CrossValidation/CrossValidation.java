package edu.uct.research.CrossValidation;

import java.io.File;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import edu.uct.research.Project.Util;

public class CrossValidation {

	public static final int TYPE_TOP1 = 0;
	public static final int TYPE_TOP5 = 1;
	
	private RecordReaderDataSetIterator[] trainingFolds;
	private RecordReaderDataSetIterator[] evaluationFolds;
	private Builder conf;
	private MultiLayerNetwork model;
	private int epochs;
	private int k;
	private Measure top1;
	private Measure top5;
	
	private CrossValidationListener listener;
	
	// Pass Builder instead of MultiLayerNetwork since need to build on every reset of the CNN: Otherwise get discrepancies in the results i.e. degraded performance
	// on the remaining folds
	
	public CrossValidation(RecordReaderDataSetIterator[] trainingFolds, RecordReaderDataSetIterator[] evaluationFolds, Builder conf, int k, int epochs) {
		this.trainingFolds = trainingFolds;
		this.evaluationFolds = evaluationFolds;
		this.conf = conf;
		this.k = k;
		this.epochs = epochs;
		this.top1 = new Measure(this.k);
		this.top5 = new Measure(this.k);

	}

	public void attach(CrossValidationListener listener) {
		this.listener = listener;
			
	}

	public Measure getTop1() {
		return this.top1;

	}
	
	public Measure getTop5() {
		return this.top5;

	}

	public double getScore() {
		return Util.Math.round(this.model.score(), 12);
		
	}
	
	public double getTrainAcc(int fold, int type) {
		double trainAcc = 0;
		for(int i = 0; i < k; i++) {
			if(i == fold) continue;
			if(type == CrossValidation.TYPE_TOP1) {
				trainAcc += getTop1Score(model, getTrainingFold(i)).topNAccuracy();
			}
			else if(type == CrossValidation.TYPE_TOP5) {
				trainAcc += getTop5Score(model, getTrainingFold(i)).topNAccuracy();
			}
			
		}
		trainAcc /= (k - 1);

		return trainAcc;

	}
	
	public double getEvaluationAcc(int fold, int type) {
		if(type == CrossValidation.TYPE_TOP1) {
			return this.getTop1Score(this.model, this.getEvaluationFold(fold)).topNAccuracy();
		}
		else { //if(type == CrossValidation.TYPE_TOP5)
			return this.getTop5Score(this.model, this.getEvaluationFold(fold)).topNAccuracy();
		}
		
	}

	public void fit() {
		// ith fold is the fold to be omitted from training on the k iterations
		for(int i = 0; i < k; i++) {
			fit(i);

		}

	}
	
	public void fit(int fold) {
		resetModel();
		trainModel(fold);
		
		// On completion of training the model with the k - 1 folds perform evaluation
		Evaluation eval1 = getTop1Score(this.model, getEvaluationFold(fold));
		this.top1.add(eval1);
		
		Evaluation eval5 = getTop5Score(this.model, getEvaluationFold(fold));
		this.top5.add(eval5);
		
		if(this.listener != null) this.listener.onFoldComplete(fold);
		
	}

	private void trainModel(int testFold) {

		// Train for epochs amount
		for(int i = 0; i < epochs; i++) {

			// Iterate over all training folds
			for(int j = 0; j < k; j++) {
				if(j == testFold) continue; // Don't train on the test fold
				this.model.fit(getTrainingFold(j)); // Train on fold j

			}
			if(this.listener != null) this.listener.onEpochComplete(testFold, i);

		}

	}

	private DataSetIterator getTrainingFold(int i) {
		this.trainingFolds[i].reset();
		return this.trainingFolds[i];

	}
	
	private DataSetIterator getEvaluationFold(int i) {
		this.evaluationFolds[i].reset();
		return this.evaluationFolds[i];

	}
	
	private void resetModel() {
		this.model = new MultiLayerNetwork(conf.build());
		this.model.init();
		
//		//Initialize the user interface backend
//	    UIServer uiServer = UIServer.getInstance();
//
//	    //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
//	    StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
//	    
//	    //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
//	    uiServer.attach(statsStorage);
//
//	    //Then add the StatsListener to collect this information from the network, as it trains
//	    model.setListeners(new StatsListener(statsStorage));
		
	}
	
	private Evaluation getTop1Score(MultiLayerNetwork model, DataSetIterator test) {
		Evaluation eval = model.evaluate(test, test.getLabels());
		
		return eval;

	}
	
	private Evaluation getTop5Score(MultiLayerNetwork model, DataSetIterator test) {
		Evaluation eval = model.evaluate(test, test.getLabels(), 5);
		
		return eval;

	}
	
	
}
