package edu.uct.research.Model;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration.Builder;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.GaussianDistribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.LearningRatePolicy;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import edu.uct.research.Project.Settings;

public class FTModel extends Model {
	
	private SubsamplingLayer maxPool(String name,  int[] kernel) {
		return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
	}
	
	private ConvolutionLayer conv(String name, int in, int out, int[] kernel, int[] stride) {
		return new ConvolutionLayer.Builder(kernel, stride, new int[] {1, 1}).name(name).nIn(in).nOut(out).build();
	}
	
	private ConvolutionLayer conv(String name, int out, int[] kernel, int[] stride) {
		return new ConvolutionLayer.Builder(kernel, stride).name(name).nOut(out).build();
	}
	
	@Override
	public Builder build(int labels) {
		Builder conf = new NeuralNetConfiguration.Builder()
				.seed(123)
				.iterations(1)
				.activation("relu")
				.weightInit(WeightInit.XAVIER)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(0.01)
				.updater(Updater.NESTEROVS).momentum(0.9)
				
				.regularization(true)
				.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) // normalize to prevent vanishing or exploding gradients
				.l2(5 * 1e-4)
				.list()
				
				.layer(0, conv("cnn1", Settings.channels, 30, new int[] {6, 6}, new int[] {2, 2}))
				.layer(1, maxPool("pool1", new int[] {3, 3}))
				
				.layer(2, conv("cnn2", 40, new int[] {3, 3}, new int[] {2, 2}))
				.layer(3, maxPool("pool2", new int[] {3, 3}))
				
				.layer(4, conv("cnn3", 60, new int[] {3, 3}, new int[] {1, 1}))
				
				.layer(5, new DenseLayer.Builder()
                        .name("ffn1")
                        .nOut(140)
                        .build())
				
				.layer(6, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.nOut(labels)
						.activation("softmax")
						.build())
				
				.backprop(true).pretrain(false)
				.cnnInputSize(Settings.modelLength, Settings.modelLength, Settings.channels);
		
		return conf;

	}

	@Override
	public String getName() {
		return "FTModel";
		
	}


}
