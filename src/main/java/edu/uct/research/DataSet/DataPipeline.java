package edu.uct.research.DataSet;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.filters.RandomPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.BaseImageLoader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Augmentation.Colour.FancyPCAAugmentation;
import edu.uct.research.Project.Settings;

public class DataPipeline {

	private DataSetInfoBase datasetInfo;
	private int k;

	public DataPipeline(DataSetInfoBase info, int k) {
		this.datasetInfo = info;
		this.k = k;

	}

	// Retrieve all the folds as an array of RecordReaderDataSetIterator objects.
	// The Augmentation specifies which augmentation to apply on the original folds.

	public RecordReaderDataSetIterator[] getAugmentedFolds(Augmentation augmentation) { // good

		// Root name: DataSetName_AugmentationName
		File newDirectory = new File(this.datasetInfo.getName() + augmentation.getName());

		// Has the augmentation on the data set already been applied or not?
		if(!newDirectory.exists()) createAugmentedFolds(augmentation, newDirectory);

		RecordReaderDataSetIterator[] folds = new RecordReaderDataSetIterator[this.k];

		// Iterate over k folds
		for(File sub : newDirectory.listFiles()) {
			if(!sub.isDirectory()) continue;
			int index = Integer.valueOf(sub.getName().split("_")[1]);
			folds[index] = getFold(new File(newDirectory + "/" + sub.getName()));

		}

		return folds;

	}

	private RecordReaderDataSetIterator getFold(File fileName) {

		System.out.println("Retrieving fold: " + fileName.getAbsolutePath());

		FileSplit filesInDir = new FileSplit(fileName, BaseImageLoader.ALLOWED_FORMATS, new Random(123));
		ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
		RandomPathFilter pathFilter = new RandomPathFilter(new Random(123), BaseImageLoader.ALLOWED_FORMATS);
		
		InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 100);
		
		ExtImageRecordReader imageReader = new ExtImageRecordReader(Settings.modelLength, Settings.modelLength, Settings.channels, labelMaker);
		
		try {
			imageReader.initialize(filesInDirSplit[0]);
		} catch (IOException e) { }

		RecordReaderDataSetIterator fold = new RecordReaderDataSetIterator(imageReader, Settings.batchSize, 1, this.datasetInfo.getAmoutOfClasses());
		
		// Apply Normalization on the folds
		ImagePreProcessingScaler myScaler = new ImagePreProcessingScaler();
		fold.setPreProcessor(myScaler);

		return fold;

	}

	private void createAugmentedFolds(Augmentation augmentation, File root) {
		System.out.println("Creating: " + augmentation.getName());

		// Create root folder
		root.mkdirs();

		// Create k sub-directories
		for(int i = 0; i < this.k; i++) {
			new File(root + "/Fold_" + i).mkdirs();

		}

		// Iterate over all class directories within fold
		for(File sub : this.datasetInfo.getName().listFiles()) {
			if(!sub.isDirectory()) continue;

			// Iterate over all images within class folder
			for(File file : sub.listFiles()) {

				ArrayList<BufferedImage> images = augmentation.apply(copyImage(file));

				int classSize = sub.listFiles().length;
				String className = sub.getName();
				int identityNumber = Integer.valueOf(file.getName().split("\\.")[0]);
				
				for(int i = 0; i < images.size(); i++) {
					try {
						String newFile = root + "/Fold_" + getFold(identityNumber, classSize) + "/" + className;
						new File(newFile).mkdirs();
						ImageIO.write(images.get(i), "png", new File(newFile + "/" + identityNumber + "_" + i + ".png"));
						System.out.println("Wrote: " + newFile + "/" + identityNumber + "_" + i + ".png");
						
					}
					catch(Exception e) {
						e.printStackTrace();
					}

				}

			}
		}

	}

	private int getFold(int identityNumber, int classSize) throws Exception {
		int dif = classSize / this.k;

		for(int i = 1; i <= this.k; i++) {
			if((i - 1) * dif <= identityNumber && identityNumber < i * dif) {
				return i - 1;
			}

		}
		throw new Exception("classNumber could not be located given the class size and amount of folds: id: " + identityNumber + " cS: " + classSize);
		
	}
	
	private BufferedImage copyImage(File src) {
		BufferedImage b = null;
		try {
			BufferedImage source = ImageIO.read(src);
			b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = b.getGraphics();
			g.drawImage(source, 0, 0, null);
			g.dispose();
		} catch (IOException e) { }

		return b;

	}
	

}
