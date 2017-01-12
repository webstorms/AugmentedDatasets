package edu.uct.research.Model;

import java.io.File;

import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Augmentation.NoAugmentation;
import edu.uct.research.CrossValidation.CrossValidation;
import edu.uct.research.CrossValidation.CrossValidationListener;
import edu.uct.research.CrossValidation.Measure;
import edu.uct.research.DataSet.DataPipeline;
import edu.uct.research.DataSet.DataSetInfoBase;
import edu.uct.research.Project.Settings;
import edu.uct.research.Project.Util;

public class Experiment {

	private File root;
	private CrossValidation validation;
	private String name;
	
	public Experiment(Augmentation augmentation, Model config, DataSetInfoBase info) {

		this.name = augmentation.getName();
		
		// Create Results/Dataset/Configuration/AugmentationType
		this.root = new File("Results/" + info.getName() + "/" + config.getName() + "/" + augmentation.getName());
		this.root.mkdirs();
		
		DataPipeline pipeline = new DataPipeline(info, Settings.k);
		RecordReaderDataSetIterator[] trainingFolds = pipeline.getAugmentedFolds(augmentation);
		RecordReaderDataSetIterator[] evaluationFolds = pipeline.getAugmentedFolds(new NoAugmentation());

		validation = new CrossValidation(trainingFolds, evaluationFolds, config.build(info.getAmoutOfClasses()), Settings.k, Settings.epochs);

	}

	public void train() {

		long startTime = System.currentTimeMillis();

		CrossValidationListener listener = new CrossValidationListener() {

			@Override
			public void onEpochComplete(int fold, int epoch) {
				System.out.println(name + ": Completed Epoch " + epoch + " on fold " + fold);

			}

			@Override
			public void onFoldComplete(int fold) {
				String output = "Fold: " + fold + "\n"
						+ "Top 1 Val Err: " + Util.Math.round(validation.getEvaluationAcc(fold, CrossValidation.TYPE_TOP1), 4) + "\n"
						+ "Top 1 Train Err: " + Util.Math.round(validation.getTrainAcc(fold, CrossValidation.TYPE_TOP1), 4) + "\n";
				Util.IO.writeFile(root + "/Folds", "txt", output.replace(".", ","));

			}

		};

		this.validation.attach(listener);
		this.validation.fit();

		long endTime = (System.currentTimeMillis() - startTime) / (1000 * 60); // in minutes

		// Once completed write out the results
		String output = "Time: " + endTime + "min" + "\n"
				+ "Error1: " + validation.getTop1().getMean(Measure.ACCURACY) + " " + validation.getTop1().getSTD(Measure.ACCURACY) + "\n"
				+ "Error5: " + validation.getTop5().getMean(Measure.ACCURACY) + " " + validation.getTop5().getSTD(Measure.ACCURACY);
		Util.IO.writeFile(root + "/Final", "txt", output.replace(".", ","));

	}

	// fold = the fold to be omitted from training
	// which implies the fold that will be used for evaluation

	static long startTime = System.currentTimeMillis();
	
	public void logEpochs(int fold) {
		
		CrossValidationListener listener = new CrossValidationListener() {

			@Override
			public void onEpochComplete(int fold, int epoch) {
				
				long endTime = (System.currentTimeMillis() - startTime) / (1000 * 60); // in minutes
				System.out.println("Time: " + endTime + ":" + (System.currentTimeMillis() - startTime) % (1000 * 60));
				
				String output = "";
				if(epoch % 4 == 0) {
					output = epoch
							+ " " + Util.Math.round(validation.getEvaluationAcc(fold, CrossValidation.TYPE_TOP1), 4)
							+ " " + Util.Math.round(validation.getTrainAcc(fold, CrossValidation.TYPE_TOP1), 4)
							+ " " + validation.getScore();
				}
				else if(epoch % 2 == 0) {
					output = epoch
							+ " " + Util.Math.round(validation.getEvaluationAcc(fold, CrossValidation.TYPE_TOP1), 4)
							+ " " + validation.getScore();
				}
				else {
					output = epoch
							+ " " + validation.getScore();
				}
				
				System.out.println(output);
				Util.IO.writeFile(root + "/Log", "txt", output.replace(".", ","));
				
				startTime = System.currentTimeMillis();
				
			}
			
			@Override
			public void onFoldComplete(int fold) {

			}

		};

		this.validation.attach(listener);
		this.validation.fit(fold);

	}


}