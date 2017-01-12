package edu.uct.research.DataSet;

import java.io.IOException;
import java.util.Collections;

import org.datavec.api.io.labels.PathLabelGenerator;
import org.datavec.api.split.InputSplit;
import org.datavec.image.recordreader.ImageRecordReader;

/*
 * The ImageRecordReader does not associate the nth folder as the nth class. This causes a problem when 
 * setting up the training and evaluation data folds. This extension ensures that the training and evaluation
 * folds preserve class identity with regard to the assignment to the respective labels.
 * 
 */

public class ExtImageRecordReader extends ImageRecordReader {

	public ExtImageRecordReader(int height, int width, int channels, PathLabelGenerator labelGenerator) {
        super(height, width, channels, labelGenerator);
        
    }
	
	@Override
    public void initialize(InputSplit split) throws IOException {
		super.initialize(split);
		Collections.sort(this.labels);
		
	}
	
	
}
