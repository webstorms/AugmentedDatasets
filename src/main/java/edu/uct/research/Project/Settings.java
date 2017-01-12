package edu.uct.research.Project;

import edu.uct.research.DataSet.Caltech101Info;
import edu.uct.research.DataSet.DataSetInfoBase;

public class Settings {
	
	public static DataSetInfoBase caltech101 = new Caltech101Info();
	
	public static int k = 4; // k-fold cross validation
	public static int channels = 3; // depth of image, only one channel since images are grey-scale
	public static int epochs = 100;
	public static final int batchSize = 16; //64;
	public static final int transformedLength = 256; // Transformed image dimensions
	public static final int modelLength = 224; // Dimension of image when passed into the network
	
	
}
