package edu.uct.research.Model;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration.Builder;

public abstract class Model {
	
	public abstract Builder build(int labels);
	public abstract String getName();
	
}
