package edu.uct.research.Augmentation.Colour;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;

import com.jhlabs.image.HSBAdjustFilter;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Project.Settings;

public class ColourAugmentation extends Augmentation {
	
	@Override
	public String getName() {
		return "RandomColourAugmentation";
	}

	@Override
	public ArrayList<BufferedImage> apply(BufferedImage src) {
		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

		int offset = (Settings.transformedLength - Settings.modelLength) / 2;
		
		// Add original down-sized image
		list.add(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength));
		// Add flip-version
		list.add(jitter(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength))); 
		
		return list;
		
	}
	
	private BufferedImage jitter(BufferedImage src) {
		HSBAdjustFilter mixer = new HSBAdjustFilter(0.08f, 0.14f, 0.08f);
		return mixer.filter(src, null);
		
	}
	
	
}
