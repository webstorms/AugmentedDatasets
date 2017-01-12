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
import org.deeplearning4j.nn.conf.layers.LocalResponseNormalization;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import edu.uct.research.Project.Settings;

public class AlexNet extends Model {

	@Override
	public Builder build(int labels) {

		Builder conf = new NeuralNetConfiguration.Builder()
				.seed(123)
				.iterations(1)
				.activation("relu")
				.weightInit(WeightInit.XAVIER)
				.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(0.01)
				.momentum(0.9)
				.regularization(true)
				.l2(1e-3)
				.updater(Updater.ADAGRAD)
				.useDropConnect(true)
				.list()
				.layer(0, new ConvolutionLayer.Builder(4, 4)
						.name("cnn1")
						.nIn(Settings.channels)
						.stride(1, 1)
						.nOut(20)
						.build())
				.layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, new int[]{2, 2})
						.name("pool1")
						.build())
				.layer(2, new ConvolutionLayer.Builder(3, 3)
						.name("cnn2")
						.stride(1,1)
						.nOut(40)
						.build())
				.layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, new int[]{2, 2})
						.name("pool2")
						.build())
				.layer(4, new ConvolutionLayer.Builder(3, 3)
						.name("cnn3")
						.stride(1,1)
						.nOut(60)
						.build())
				.layer(5, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, new int[]{2, 2})
						.name("pool3")
						.build())
				.layer(6, new ConvolutionLayer.Builder(2, 2)
						.name("cnn3")
						.stride(1,1)
						.nOut(80)
						.build())
				.layer(7, new DenseLayer.Builder()
						.name("ffn1")
						.nOut(160)
						.dropOut(0.5)
						.build())
				.layer(8, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
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
