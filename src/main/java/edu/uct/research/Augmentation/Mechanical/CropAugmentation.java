package edu.uct.research.Augmentation.Mechanical;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Project.Settings;

public class CropAugmentation extends Augmentation {

	@Override
	public String getName() {
		return "CropAugmentation";
		
	}
	
	@Override
	public ArrayList<BufferedImage> apply(BufferedImage src) {
		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
		
		int offset = (Settings.transformedLength - Settings.modelLength);
		
		// Top left
		BufferedImage topLeft = Scalr.crop(src, 0, 0, Settings.modelLength, Settings.modelLength);
		
		// Top right
		BufferedImage topRight = Scalr.crop(src, offset, 0, Settings.modelLength, Settings.modelLength);
		
		// Bottom left
		BufferedImage bottomLeft = Scalr.crop(src, 0, offset, Settings.modelLength, Settings.modelLength);
		
		// Bottom right
		BufferedImage bottomRight = Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength);
		
		// Center
		BufferedImage center = Scalr.crop(src, offset / 2, offset / 2, Settings.modelLength, Settings.modelLength);
		
		list.add(topLeft);
		list.add(topRight);
		list.add(bottomLeft);
		list.add(bottomRight);
		list.add(center);
		
		return list;
		
	}
	
	
}
