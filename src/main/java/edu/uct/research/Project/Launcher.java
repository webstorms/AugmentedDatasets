package edu.uct.research.Project;

import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;

import edu.uct.research.Augmentation.NoAugmentation;
import edu.uct.research.Augmentation.Colour.ColourAugmentation;
import edu.uct.research.Augmentation.Mechanical.CropAugmentation;
import edu.uct.research.Augmentation.Mechanical.FlipAugmentation;
import edu.uct.research.Augmentation.Mechanical.RotationAugmentation;
import edu.uct.research.Augmentation.Colour.FancyPCAAugmentation;
import edu.uct.research.Augmentation.Colour.EdgeEnhancerAugmentation;
import edu.uct.research.Model.Experiment;
import edu.uct.research.Model.FTModel;

public class Launcher {

	public static void main(String[] args) throws Exception {

		DataTypeUtil.setDTypeForContext(DataBuffer.Type.FLOAT);
		Settings.epochs = 30;

		// Control
		new Experiment(new NoAugmentation(), new FTModel(), Settings.caltech101).train();

		// Color
		new Experiment(new FancyPCAAugmentation(), new FTModel(), Settings.caltech101).train();
		new Experiment(new ColourAugmentation(), new FTModel(), Settings.caltech101).train();
		new Experiment(new EdgeEnhancerAugmentation(), new FTModel(), Settings.caltech101).train();
		
		// Mechanical
		new Experiment(new FlipAugmentation(), new FTModel(), Settings.caltech101).train();
		new Experiment(new RotationAugmentation(), new FTModel(), Settings.caltech101).train();
		new Experiment(new CropAugmentation(), new FTModel(), Settings.caltech101).train();

	}


}
