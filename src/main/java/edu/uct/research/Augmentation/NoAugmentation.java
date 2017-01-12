package edu.uct.research.Augmentation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;

import edu.uct.research.Project.Settings;

public class NoAugmentation extends Augmentation {

	@Override
	public String getName() {
		return "NoAugmentation";
		
	}
	
	@Override
	public ArrayList<BufferedImage> apply(BufferedImage src) {
		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
		
		int offset = (Settings.transformedLength - Settings.modelLength) / 2;
		
		list.add(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength));
		
		return list;
		
	}
	

}
