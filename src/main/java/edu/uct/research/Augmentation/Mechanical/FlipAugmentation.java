package edu.uct.research.Augmentation.Mechanical;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Project.Settings;

public class FlipAugmentation extends Augmentation {

	@Override
	public String getName() {
		return "FlipAugmentation";
	}

	@Override
	public ArrayList<BufferedImage> apply(BufferedImage src) {
		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

		int offset = (Settings.transformedLength - Settings.modelLength) / 2;
		
		// Add original down-sized image
		list.add(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength));
		// Add flip-version
		list.add(Scalr.crop(flip(src), offset, offset, Settings.modelLength, Settings.modelLength)); 
		
		return list;
		
	}
	
	private BufferedImage flip(BufferedImage src) {
		BufferedImage aug = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());			
		Graphics2D g = aug.createGraphics();
		g.drawImage(src, 0 + src.getWidth(), 0, -src.getWidth(), src.getHeight(), null);

		return aug;

	}


}
