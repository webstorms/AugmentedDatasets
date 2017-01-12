package edu.uct.research.DataSet;

import java.io.File;

public class Caltech101Info extends DataSetInfoBase {

	@Override
	public int getAmoutOfClasses() {
		return 101;
		
	}

	@Override
	public File getName() {
		return new File("Caltech-101_");
		
	}
	
	
}
