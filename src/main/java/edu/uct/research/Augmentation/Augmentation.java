package edu.uct.research.Augmentation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Augmentation {
	
	public abstract String getName();
	
	public abstract ArrayList<BufferedImage> apply(BufferedImage src);


}
